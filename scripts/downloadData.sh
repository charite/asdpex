#!/bin/bash

if [ ! $2 ]
then
  echo "missing data path - set default to ../data"
  ROOT=`dirname $0`
  DATA="$ROOT/../data"
else
  DATA=$2
fi
#ROOT=`dirname $0`
#DATA="$ROOT/../data"
RELEASE=GRCh38

url38="ftp://ftp.ncbi.nlm.nih.gov/genomes/all/GCA_000001405.15_GRCh38/seqs_for_alignment_pipelines.ucsc_ids/GCA_000001405.15_GRCh38_full_analysis_set.fna.gz"
NCBI="ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.107"

printHELP(){
	  echo "Usage: $0 <param> [<datapath>]"
	  echo "Analysis sets:"
	  echo "param   file                              description"
	  echo "grch38  grch38                            primary assembly of GRCh38 (incl. ALT contigs)"
	  echo "chr     chr_accessions_GRCh38.p2          provides the correspondence between the RefSeq and GenBank records for each chromosome in the assembly"
	  echo "alt     alts_accessions_GRCh38.p2         provides the correspondence between the RefSeq and GenBank records for each alt. scaffold in the assembly"
	  echo "alt     all_alt_scaffold_placement.txt    provides the genomic localization for each alt. scaffold"
	  echo "region  genomic_regions_definitions.txt   defining the regions on the primary assembly for which alternate loci or patch scaffolds are available"
	  echo "aln     GFF files                         precalculated alignments between reference region and alternate loci"
	  echo "all                                       download the complete dataset - all of the above"
	  echo ""
	  echo "Note: This script downloads the human reference genome for GRCh38 (hs38DH),"
	  echo "      the chromosome, genomic region and alternate loci definitions,"
	  echo "      and the precomputed alignments from NCBI."
	  exit 1;
}

downloadAlignments(){
  echo "[INFO] download alternate loci alignments"
  SCAFFOLDS=$DATA/all_alt_scaffold_placement.txt
  if [ ! -f $SCAFFOLDS ]; then
    echo "[ERROR] file $SCAFFOLDS is missing, please download first."
    printHELP
  fi
  mkdir -p $DATA/alignments
  for i in $(awk '{print $1"/alt_scaffolds/alignments/"$5"_"$8".gff"}' $SCAFFOLDS)
  do
    echo $i
    FILE=`basename $i`
    if [ ! -f $DATA/alignments/$FILE ]; then
      wget --progress=bar -O $DATA/alignments/$FILE $NCBI/chr_context_for_alt_loci/GRCh38.p2/$i
    fi
  done
}


downloadScaffolds(){
  echo "[INFO] download alternate loci placement"
  SCAFFOLDS=$DATA/all_alt_scaffold_placement.txt
  if [ ! -f $SCAFFOLDS ]; then
    mkdir -p $DATA/TMP
    for i in `seq 1 35`
    do
      echo "Process Loci $i:"
      wget --progress=bar -O $DATA/TMP/alt_scaffold_placement.txt $NCBI/chr_context_for_alt_loci/GRCh38.p2/ALT_REF_LOCI_${i}/alt_scaffolds/alt_scaffold_placement.txt
      if [ $i == 1 ]; then   
        head -n 1 $DATA/TMP/alt_scaffold_placement.txt >$SCAFFOLDS
      fi
      grep -v "^#" $DATA/TMP/alt_scaffold_placement.txt >>$SCAFFOLDS
    done
    rm -rf $DATA/TMP
  fi
}

downloadGenome(){
  echo "[INFO] download genome"
  if [ ! -d $DATA/genome ]; then
      mkdir $DATA/genome
  fi
  GENOME=$DATA/genome/$RELEASE.fa
  if [ ! -f $GENOME ]; then
      echo "Downloading $GENOME...."
      wget --progress=bar -O $GENOME.gz $url38 
      gzip -dc $GENOME.gz > $GENOME
  fi
  [ ! -f $GENOME.fai ] && echo -e "\nPlease run 'samtools faidx $GENOME'...\n"
 
}

downloadChrInfo(){
  echo "[INFO] download chromosome info"
  FILE=chr_accessions_GRCh38.p2
  if [ ! -f $DATA/$FILE ]; then
    wget --progress=bar -O $DATA/$FILE $NCBI/Assembled_chromosomes/$FILE
  fi
}

downloadScaffoldInfo(){
  echo "[INFO] download alternate loci info"
  FILE=alts_accessions_GRCh38.p2
  if [ ! -f $DATA/$FILE ]; then
    wget --progress=bar -O $DATA/$FILE $NCBI/Assembled_chromosomes/$FILE
  fi
}

downloadRegions(){
  echo "[INFO] download regions info"
  FILE=genomic_regions_definitions.txt
  if [ ! -f $DATA/$FILE ]; then
    wget --progress=bar -O $DATA/$FILE $NCBI/chr_context_for_alt_loci/GRCh38.p2/$FILE
  fi
}



## create DATA directory if it not yet exists
if [ -n "$2" ]; then
  echo "set datapath to $2"
fi
if [ ! -e $DATA ]; then
  mkdir -p $DATA
fi

if [ -z "$1" ]; then
  printHELP
elif [ $1 == "all" ]; then
	downloadGenome
  downloadChrInfo
  downloadScaffoldInfo
  downloadScaffolds  
  downloadRegions
  downloadAlignments
elif [ $1 == "grch38" ]; then
	downloadGenome
elif [ $1 == "chr" ]; then
	downloadChrInfo
elif [ $1 == "region" ]; then
	downloadRegions
elif [ $1 == "alt" ]; then
	downloadScaffoldInfo
  downloadScaffolds
elif [ $1 == "aln" ]; then
	downloadAlignments
else
	printHELP
fi
