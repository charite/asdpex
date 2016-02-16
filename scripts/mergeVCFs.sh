#!/bin/bash
#
# concatenate the VCFs from the prediction tool into a single VCF file
#
# command:
#      bash mergeVCFs.sh <path to VCF folder> <outfile.vcf>

#OUT="../all_ASDPs.vcf"
OUT=$2

for i in $(ls ${1}/*.vcf)
do
  if [ -z $HEADER ]
  then
    grep "^#" $i > $OUT
    HEADER=1
  fi
  grep -v "^#" $i >> $OUT
done

# extract SNVs and small InDels
BASE=$(basename $OUT .vcf)
DIR=$(dirname $OUT)
sortBed -header -i $OUT | grep -v "SVLEN=" > $DIR/${BASE}.SNV.vcf
