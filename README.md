# ASDPex

## Introduction

The application ASDPex and the scripts in this repository can be used to improve the alignments of the alternate loci provided by GRC, to search for alignable scaffold-discrepant positions (ASDPs) in the alignments, and to use the resulting ASDP file to screen sample VCF files from whole-genome sequencing for ASDPs (which are likely to be false-positive variant calls).

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

##Postprocess VCF files from Whole-Genome Sequencing (WGS)
As described in the main manuscript, we can now use the ASDP file generated above (__allASDPs.SNV.50_10.valid.vcf.gz__) to postprocess
a WGS file to mark up called variants that correspond to ASPDs. The following command performs this analysis and outputs a file (__<annot>.vcf.gz__)
that contains the annotations.

```
java -jar asdpex-cli/target/asdpex-cli-0.2.jar \
  annotate -a allASDPs.SNV.50_10.valid.vcf.gz -d data/ -v <infile>.vcf.gz \
  -o <annot>.vcf.gz
```
