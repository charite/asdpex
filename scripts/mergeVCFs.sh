#!/usr/bin/bash
#
# concatenate the VCFs from the prediction tool into a single VCF file

OUT="../all_ASDPs.vcf"
for i in $(ls ../tmp/*.vcf)
do
  if [ -z $HEADER ]
  then
    grep "^#" $i > $OUT
    HEADER=1
  fi
  grep -v "^#" $i >> $OUT
done

# extract SNVs and small InDels
sortBed -header -i ../all_ASDPs.vcf | grep -v "SVLEN=" > ../all_ASDPs.SNV.vcf
