#!/usr/bin/perl

use strict;
use warnings;
use Getopt::Std;
use Getopt::Long;
use IO::Zlib;

my $infile;
my $outfile;
my $help;

GetOptions(	'vcfin|i=s'	=> \$infile,
#		'vcfout|o=s'	=> \$outfile,
		'help|h'	=> \$help);

if($help){printUsage()}
if(!$infile){printUsage()}
#if(!$outfile){printUsage()}


#my $infile="/media/mjaeger/USB-Stick/vcf/1008.HS38.aln.normalized.vcf.gz";

#my $outfile="/home/mjaeger/git/hg38altLociSelector/vcf/1008.HS38.aln.normalized.fixed.vcf.gz";

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

close(IN);
#close(OUT);

sub printUsage{
	print while(<DATA>);
	exit;
}


__DATA__


	fixVCFs.pl

SYNOPSIS

	perl fixVCFs.pl -i inputfile.vcf

DESCRIPTION

	This will fix the output from Freebayes which makes it incompatible with HTSJDK/HTSLIB.

	fixVCFs.pl [OPTIONS]

OPTIONS:
	-i	--vcfin		<path to VCF file>
	-o	--vcfout		<path to fixed VCF file>
	-h	--help
