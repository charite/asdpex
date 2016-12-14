# ASDPex

## Introduction

The application ASDPex and the scripts in this repository can be used to improve the alignments of the alternate loci provided by GRC, to search for alignable scaffold-discrepant positions (ASDPs) in the alignments, and to use the resulting ASDP file to screen sample VCF files from whole-genome sequencing for ASDPs (which are likely to be false-positive variant calls).

See Jäger et al. (2016) (Alternate-locus aware variant calling in whole genome sequencing. Genome Medicine 8:130)[https://genomemedicine.biomedcentral.com/articles/10.1186/s13073-016-0383-z]


## Prerequisites:

In addition to Java 8, you will need to install the tabix, bedtools, and samtools packages. If you are on a debian-based system, enter

```
sudo apt-get install tabix
sudo apt-get install bedtools
sudo apt-get install samtools
```

### Getting started

```
git clone https://github.com/charite/asdpex.git
cd asdpex
```


### Download data
cd to the 'scripts' directory, make the "downloadData.sh" executable, and execute the following command to download all the data we will need.

```
cd scripts
chmod +x downloadData.sh
./downloadData all data
cd ..
```
After you have downloaded the data, you will need to index the genome using samtools (the script will produce a message with the command you need, and if samtools is not in your path adjust the command accordingly).

### Build the executables

#### regionalign2vcf
The aligner was written using the SeqAn C++ library. Therfore the library has to be downloaded and the tool compiled. We have to change to the `seqan` folder and run the Makefile.
```
cd seqan
make
cd ..
```
This command should result in an executable programm called regionalign2vcf, which is later on needed.

#### asdpex
We use the maven build system to compile the code. First cd back to the main folder.
```
mvn package
```
If everything goes well, you will see a message including the words BUILD SUCCESS.

The jar file asdpex-cli-0.1.jar contains the main code used in this project. An overview of available command are shown with the following command.
```
java -jar asdpex-cli/target/asdpex-cli-0.2.jar
Program: de.charite.compbio.asdpex (functional annotation of VCF files)
Version: 0.2
Contact: Marten Jäger <marten.jaeger@charite.de>
         Peter N. Robinson <peter.robinson@jax.org>

Usage: java -jar asdpex.jar <command> [options]

Command: align       construct fasta and seed files and do the alignments
         annotate    functional annotation of VCF files
         create-db   creates a SQLite database used for this tool
         create-fa   construct fasta files for the alignments
         create-seed construct seed files for the alignments from the NCBI alignments

Example: java -jar asdpex.jar create-db -s asdpex.sqlite -d data
         java -jar asdpex.jar create-fa -o data


```


### Create database and init
Create the SQLite database and inititate with the downloaded data.

```
java -jar asdpex-cli/target/asdpex-cli-0.2.jar create-db -s asdpex.sqlite -d data
```



## Alignment and Identification of ASDPs
The following command will perform the alignment of the alternate loci with the corresponding regions of the primary assembly
and will store the discrepant alignment positions for each of the alternate loci in one individual VCF file (e.g., chr5_KI270897v1_alt.vcf).
The files will be written to the (new) directory "alignresults" (as indicated by the -o flag). The -q flag indicates the SQlite database that we created in the previous step.

```
java -jar asdpex-cli/target/asdpex-cli-0.2.jar \
  align -d data/ -s seqan/regionalign2vcf -o alignresults -q asdpex.sqlite
```

There should now be 261 separate VCF files in the __alignresults__ directory.  We merge these VCF files into a single file __allASDPs.vcf.gz__ and filter for
single nucleotide variants (SNVs). This and the following scripts
assume that [BGZIP](https://github.com/samtools/htslib "htslib repository") and [TABIX](https://github.com/samtools/htslib "htslib repository") are defined
as environment variables. If this is not the case in your system, you will need to modify the scripts accordingly (or set the environment variables).
```
./scripts/mergeVCFs.sh alignresults/ allASDPs.vcf
```
The file __allASDPs.vcf__ that is created in this step should contain 768316 ASDP candidates with differences less than 50 nt.
However, as mentioned in the manuscript, our procedure
applies a additional criteria for the goodness of the alignment in
regions surrounding discrepant positions on the basis of alignment windows of 50 nt that
were allowed to contain up to  1 mismatch per 5 bases (1:5).
This is implemented by the following Perl script.
```
perl scripts/filterBadASDPs.pl -i allASDPs.SNV.vcf.gz -w 50 -m 10 -c
```
This command will iterate aver all SNV ASDPs and merge them into MNV ASDPs. The above uses a window of 50 bases (-w 50), allowing up to 10 ASDPs (-m 10). O
therwise it will mark and remove the ASDPs in the window. The ASDPs that survive this filtering step are stored in the file __allASDPs.SNV.50_10.valid.vcf.gz__.

Upload the variants into the SQLite database to store all needed information together in a more portable way.
```
java -jar asdpex-cli/target/asdpex-cli-0.2.jar \
create-db -s asdpex.sqlite -a allASDPs.SNV.50_10.valid.vcf.gz
```

We have copied the files allASDPs.SNV.50_10.valid.vcf.gz and allASDPs.SNV.50_10.valid.vcf.gz.tbi into the directory __vcf__ in this repository. These are the files that are created by the code described above and that were used for the analysis described in the manuscript.



##Postprocess VCF files from Whole-Genome Sequencing (WGS)
As described in the main manuscript, we can now use the ASDP file generated above (__allASDPs.SNV.50_10.valid.vcf.gz__) to postprocess
a WGS file to mark up called variants that correspond to ASPDs. The following command performs this analysis and outputs a file (__<annot>.vcf.gz__)
that contains the annotations.

```
java -jar asdpex-cli/target/asdpex-cli-0.2.jar \
  annotate -a allASDPs.SNV.50_10.valid.vcf.gz -d data/ -v <infile>.vcf.gz \
  -o <annot>.vcf.gz
```
##Example
Here, we will take  GRCh38 high-confidence calls for NA12878 to show how to use the ASDPex program on real data. Download the VCF and the index (tbi) files from [ftp://ftp-trace.ncbi.nlm.nih.gov/giab/ftp/release/NA12878_HG001/NISTv3.3.1/GRCh38/](ftp://ftp-trace.ncbi.nlm.nih.gov/giab/ftp/release/NA12878_HG001/NISTv3.3.1/GRCh38/). Then (assuming we are in the same directory as in the above example), enter the following command:
```
$ java -jar asdpex-cli/target/asdpex-cli-0.2.jar annotate -a allASDPs.SNV.50_10.valid.vcf.gz\
   -d data/ -v HG001_GRCh38_GIAB_highconf_CG-IllFB-IllGATKHC-Ion-10X-SOLID_CHROM1-X_v.3.3.1_highconf_phased.vcf.gz \
   -o HG001-annot.vcf.gz
```
This will output an annotated (compressed) VCF file called __HG001-annot.vcf.gz__. The lines of the VCF file will be modified to indicate the presence of ASDPs inferred by ASDPex. For example,
```
chr22	42252402	.	C	T	50	ASDP	ALTGENOTYPE=HET;ALTLOCUS=chr22_GL383582v2_alt; ...
```
This line states that the variant called on chromosome 22 at position 42252402 was inferred to be a heterozygous ASDP-associated variant related to the
alternate locus __GL383582v2__. Similarly,
```
chr18	43747118	.	A	T	50	ASDP	ALTGENOTYPE=HOM_VAR;ALTLOCUS=chr18_KI270864v1_alt;...
```
This line states that the  variant called on chromosome 18 at position 43747118 was inferred to be a homozygous  ASDP-associated variant related to the
alternate locus __KI270864v1__.
In total, 2653 ASDP-associated variants are called (homozygous: 441; heterozygous: 2212). The following table shows the number of
ASDP-associated variants called for 32 regions.



Alternate Locus | ASDPs (n) 
--------------- | ----------
chr4_GL000257v2_alt | 470
chr8_KI270822v1_alt | 382
chr8_KI270810v1_alt | 184
chr7_KI270808v1_alt | 157
chr4_KI270788v1_alt | 116
chr11_KI270832v1_alt | 114
chr9_GL383542v1_alt | 89
chr6_KI270800v1_alt | 87
chr1_KI270760v1_alt | 79
chr12_GL383550v2_alt | 78
chr11_JH159136v1_alt | 71
chr4_KI270790v1_alt | 70
chr5_GL383531v1_alt | 69
chr14_KI270844v1_alt | 69
chr3_KI270781v1_alt | 64
chr18_KI270864v1_alt | 63
chr2_GL383521v1_alt | 59
chr22_GL383582v2_alt | 48
chr4_GL383527v1_alt | 46
chr3_KI270777v1_alt | 44
chr2_GL383522v1_alt | 42
chr15_GL383555v2_alt | 35
chr13_KI270839v1_alt | 34
chr1_KI270763v1_alt | 30
chr13_KI270843v1_alt | 27
chr22_KI270876v1_alt | 26
chr16_GL383557v1_alt | 23
chr6_KI270802v1_alt | 23
chr21_GL383580v2_alt | 17
chr6_GL383533v1_alt | 16
chr18_GL383571v1_alt | 15
chr6_KI270801v1_alt | 6


