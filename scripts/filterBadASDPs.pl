#!/usr/bin/perl -w

use strict;
use warnings;
use Getopt::Std;
use Getopt::Long;
use IO::Zlib;

my $infile;
my $outfilevalide;
my $outfileskipped;
my $help;
my $windowsize = 10;
my $mismatches = 2;
my $collapse;

my @positions = ();
my @variants = ();
my @flag = ();

my $CHR = 0;
my $POS = 1;
my $REF = 3;
my $ALT = 4;
my $currentChromosome = "-1";

my $valid = 0;
my $skipped = 0;
my $combined = 0;

GetOptions(	'vcfin|i=s'	=> \$infile,
		'window|w=i'		=> \$windowsize,
		'mismatch|m=i'		=> \$mismatches,
		'collapse|c'		=> \$collapse,
		'help|h'	=> \$help);

if($help){printUsage()}
if(!$infile){printUsage()}


my $prefix = substr($infile,0,index($infile,".vcf"));
my $parameters = "${windowsize}_${mismatches}";
$outfilevalide = "$prefix.$parameters.valid.vcf";
$outfileskipped = "$prefix.$parameters.skipped.vcf";

#print STDERR "$outfilevalide\n";
#print STDERR "$outfileskipped\n";
#exit;

open(OUT,">",$outfilevalide)|| die $!;
open(OUT2,">",$outfileskipped) || die $!;
tie *IN, 'IO::Zlib', $infile, "rb";
my @fields;
my $line;
while (<IN>) {
	if($_ =~ /^#/){
		print OUT $_;
		print OUT2 $_;
	}else{
		@fields = split(/\t/,$_);
		# clean array if this is a new chromosome - write buffered variants for this chromosome
		if($fields[$CHR] ne $currentChromosome){
			printVariants();
			@positions = ();
			@variants = ();
			@flag = ();
			$currentChromosome = $fields[$CHR];
		}
		#print "variants: ".scalar(@variants)."\n";
		#print "positions: ".scalar(@positions)."\n";
		#print "flags: ".scalar(@flag)."\n";
		while(scalar(@positions) > 0 && ($fields[$POS] - $positions[0] > $windowsize) ){
			if($flag[0] == 0){
				print OUT $variants[0];
				$valid++;
			}else{ 
				print OUT2 $variants[0];
				$skipped++;
			}
			shift(@positions);
			shift(@variants);
			shift(@flag);
		}
		addVariant($_);
		#flag to filter if more than #mismatches are in the array
		if(scalar(@positions)> $mismatches){
			foreach(@flag){
				$_ = 1;
			}
		}
	}
};
# finally write the buffered variants
printVariants();

close(IN);
close(OUT);
close(OUT2);

#print STDERR "skipped: $skipped\n";
#print STDERR "valid: $valid\n";
#print STDERR "combined: $combined\n";

print STDERR "window\tmismatch\ttotal\tskipped\tvalid\tcollapsed\n";
printf STDERR "%d\t%d\t%d\t%d\t%d\t%d\n",$windowsize,$mismatches,$skipped+$valid+$combined,$skipped,$valid,$combined;

### FUNCTIONS

##
# add variants to list and combine if flag is set
##
sub addVariant{
	my @fieldsCurrent = split(/\t/,$_);
	my @infoCurrent = split(/;/,$fieldsCurrent[7]);
	if($collapse){
		if(scalar(@variants) == 0){
			push(@positions, $fields[$POS]);
			push(@variants,$_);
			push(@flag,0);			
		}else{
			my $prevVariant = pop(@variants);
			my @fieldsPrevious = split(/\t/,$prevVariant);
			my @infoPrevious = split(/;/,$fieldsPrevious[7]);
			#print ($fieldsPrevious[$POS] + length($fieldsPrevious[$REF])." - ".$fieldsCurrent[$POS]."\n");
			if($infoCurrent[0] eq $infoPrevious[0] && $fieldsPrevious[$POS] + length($fieldsPrevious[$REF]) == $fieldsCurrent[$POS] &&  length($fieldsPrevious[$REF]) ==  length($fieldsPrevious[$ALT]) && length($fieldsCurrent[$REF]) ==  length($fieldsCurrent[$ALT]) ){
				$fieldsPrevious[$REF] = "$fieldsPrevious[$REF]$fieldsCurrent[$REF]";
				$fieldsPrevious[$ALT] = "$fieldsPrevious[$ALT]$fieldsCurrent[$ALT]";
				push(@variants,join("\t",@fieldsPrevious));
				#print STDERR "\ncombine:\n";
				#print STDERR "$prevVariant" ;
				#print STDERR "$_";
				#print STDERR join("\t",@fieldsPrevious);
				$combined++;
			}else{
				push(@variants,$prevVariant);
				push(@variants,$_);
				push(@positions,$fieldsCurrent[$POS]);
				push(@flag,0);
			}
			
		}
	}else{
		push(@positions, $fieldsCurrent[$POS]);
		push(@variants,$_);
		push(@flag,0);
	}
}

sub printVariants{
	for (my $i=0; $i < scalar(@variants); $i++){
		if($flag[$i] == 0){ 
			print OUT $variants[$i];
			$valid++;
		}else{ 
			print OUT2 $variants[$i];
			$skipped++;
		}
	}
}

sub printUsage{
	print while(<DATA>);
	exit;
}



__DATA__


	filterBadASDPs.pl

SYNOPSIS

	perl filterBadASDPs.pl -i inputfile.vcf

DESCRIPTION

	This little script will 

	filterBadASDPs.pl [OPTIONS]

OPTIONS:
	-i 	--vcfin		<path to VCF file>
	-w 	--window		windowsize
	-m	--mismatch	allowed mismatches
	-h	--help		show this help
