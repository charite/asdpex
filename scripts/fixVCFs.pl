#!/usr/bin/perl

use strict;
use IO::Zlib;

my $infile="/media/mjaeger/USB-Stick/vcf/1008.HS38.aln.normalized.vcf.gz";
my $outfile="/home/mjaeger/git/hg38altLociSelector/vcf/1008.HS38.aln.normalized.fixed.vcf.gz";

tie *IN, 'IO::Zlib', $infile, "rb";
#tie *OUT, 'IO::Zlib', $outfile, "wb";
my @fields;
my $line;
while (<IN>) {
	if($_ =~ /^#/){print $_;}
	else{
		@fields = split(/\t/,$_);
        $fields[3] =~ tr/ACGTNacgtn/N/c;
        $line = join("\t",@fields);
	    print $line;
     }
};
