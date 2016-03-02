# hg38altLociSelector

The application hg38altLociSelector and the scripts in this repository can be used to improve the alignments of the alternate scafoolds provided by NCBI, to search for alignable scaffold-discrepant positions (ASPDs) in the alignments, and to use the resulting ASDP file to screen sample VCF files from whole-genome sequencing for ASPDs (which are likely to be false-positive variant calls).

Prerequisites:

In addition to Java 8, you will need to install the tabix package. If you are on a debian-based system, enter

```
sudo apt-get install tabix
```

Tutorial:

## Download data
cd to the 'scripts' directory, make the "downloadData.sh" executable, and execute the following command to download all the data we will need.

```
cd scripts
chmod +x downloadData.sh
./downloadData all
```
After you have downloaded the data, you will need to index the Genome using samtools (the script will produce a message with the command you need, and if samtools is not in your path adjust the command accordingly).

## Build the executables
We use the maven build system to compile the code. First cd back to the main folder.
```
cd ..
mvn package
```
If everything goes well, you will see a message including the words BUILD SUCCESS.

## hg38altlociselector
The jar file hg38altlociselector-cli-0.0.1-SNAPSHOT.jar contains the main code used in this project. YOu can view the main commands with the following command.
```
java -jar hg38altlociselector-cli/target/hg38altlociselector-cli-0.0.1-SNAPSHOT.jar
Program: de.charite.compbio.hg38altlociselector (functional annotation of VCF files)
Version: 0.0.1
Contact: Marten Jäger <marten.jaeger@charite.de>

Usage: java -jar hg38altlociselector.jar <command> [options]

Command: download    download transcript database  (not yet implemented)
         annotate    functional annotation of VCF files
         create-fa   construct fasta files for the alignments
         create-seed construct seed files for the alignments from the NCBI alignments
         align       construct fasta and seed files and do the alignments

Example: java -jar hg38altlociselector.jar download GRCh38
         java -jar hg38altlociselector.jar create-fa -o data/

```

## regionalign2vcf
The aligner was written using the SeqAn C++ library. Therfore the library has to be downloaded and the tool compiled. We have to change to the seqan folder and run the Makefile.
```
cd seqan
make
cd ..
```

## Alignment and variant detection
Now since we have all tools and data we run the alignment and look up the variants.
```
java -jar hg38altlociselector-cli/target/hg38altlociselector-cli-0.0.1-SNAPSHOT.jar \
  align -d data/ -s seqan/regionalign2bed -o alignresults
```

Right now the variants are saved in a separate file per alternative scaffold and even for the alignment blocks in the scaffolds. We merge the VCF files into a single file __allASDPs.vcf.gz__ and filter for SNVs. This and the following scripts
presume [BGZIP](https://github.com/samtools/htslib "htslib repository") and [TABIX](https://github.com/samtools/htslib "htslib repository") to be defined in the environment variable.
```
./scripts/mergeVCFs.sh alignresults/ allASDPs.vcf
```

The next step should be the filtration of the ASDPs. Even though the alignments are way better than the NCBI alignment, there are still non reliable regions.
```
perl scripts/filterBadASDPs.pl -i allASDPs.SNV.vcf.gz -w 50 -m 10 -c
```
This command will iterate aver all SNV ASDPs and merge them into MNV ASDPs. The above uses a window of 50bases size, allowing up to 10 ASDPs. Otherwise it will mark and skip the ASDPs in the window.

Finally we can annotate a VCF file with the information from the filtered ASDPs.
```
java -jar hg38altlociselector-cli/target/hg38altlociselector-cli-0.0.1-SNAPSHOT.jar \
  annotate -a allASDPs.SNV.50_10.valid.vcf.gz -d data/ -v <infile>.vcf.gz \
  -o <annot>.vcf.gz
```
