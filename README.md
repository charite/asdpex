# ASDPex

## Introduction

The application ASDPex and the scripts in this repository can be used to improve the alignments of the alternate loci provided by GRC, to search for alignable scaffold-discrepant positions (ASDPs) in the alignments, and to use the resulting ASDP file to screen sample VCF files from whole-genome sequencing for ASDPs (which are likely to be false-positive variant calls).

Prerequisites:

In addition to Java 8, you will need to install the tabix package. If you are on a debian-based system, enter

```
sudo apt-get install tabix
```

## Getting started

```
git clone https://github.com/charite/asdpex.git
cd asdpex
```


## Download data
cd to the 'scripts' directory, make the "downloadData.sh" executable, and execute the following command to download all the data we will need.

```
cd scripts
chmod +x downloadData.sh
./downloadData all
```
After you have downloaded the data, you will need to index the Genome using samtools (the script will produce a message with the command you need, and if samtools is not in your path adjust the command accordingly).

## Build the executables

First you should compile the aligner, which is written in C using the SeqAn library.
```
cd seqan
make
cd ..
```
This command should result in an executable programm called regionalign2vcf, which is later on needed.

We use the maven build system to compile the code. First cd back to the main folder.
```
cd ..
mvn package
```
If everything goes well, you will see a message including the words BUILD SUCCESS.

## asdpex
The jar file asdpex-cli-0.1.jar contains the main code used in this project. YOu can view the main commands with the following command.
```
java -jar asdpex-cli/target/asdpex-cli-0.1.jar
Program: de.charite.compbio.asdpex (functional annotation of VCF files)
Version: 0.0.1
Contact: Marten Jäger <marten.jaeger@charite.de>
         Peter N. Robinson <peter.robinson@jax.org>

Usage: java -jar asdpex.jar <command> [options]

Command: download    download transcript database  (not yet implemented)
         annotate    functional annotation of VCF files
         create-fa   construct fasta files for the alignments
         create-seed construct seed files for the alignments from the NCBI alignments
         align       construct fasta and seed files and do the alignments

Example: java -jar asdpex.jar download GRCh38
         java -jar asdpex.jar create-fa -o data/

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
java -jar asdpex-cli/target/asdpex-cli-0.1.jar \
  align -d data/ -s seqan/regionalign2bed -o alignresults
```

Right now the variants are saved in a separate file per alternate loci and even for the alignment blocks in the scaffolds. We merge the VCF files into a single file __allASDPs.vcf.gz__ and filter for SNVs. This and the following scripts
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
java -jar asdpex-cli/target/asdpex-cli-0.1.jar \
  annotate -a allASDPs.SNV.50_10.valid.vcf.gz -d data/ -v <infile>.vcf.gz \
  -o <annot>.vcf.gz
```
