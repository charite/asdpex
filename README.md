# hg38altLociSelector

Tutorial:

## Download data
cd to the 'scripts' directory, make the "downloadData.sh" exectable, and execute the following command to download all the data we will need.

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
