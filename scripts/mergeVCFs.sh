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
sortBed -header -i $OUT | bgzip -c >${OUT}.gz
tabix ${OUT}.gz
rm $OUT

# extract SNVs and small InDels
BASE=$(basename $OUT.gz .vcf.gz)
DIR=$(dirname $OUT.gz)
zgrep -v "SVLEN=" ${OUT}.gz | bgzip -c > $DIR/${BASE}.SNV.vcf.gz
tabix $DIR/${BASE}.SNV.vcf.gz
