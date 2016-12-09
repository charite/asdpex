#!/usr/bin/perl -w

use strict;
use warnings;
use Getopt::Std;
use Getopt::Long;
use IO::Zlib;
use File::Basename;

my $infile;
my $outfolder;
my $outfilevalide;
my $outfileskipped;
my $help;
my $windowsize = 10;
my $mismatches = 2;
my $collapse;

my %positions;
my %variants;
my %flag;

my $CHR = 0;
my $POS = 1;
my $REF = 3;
my $ALT = 4;
my $INFO = 7;
my $currentChromosome = "-1";

my $valid = 0;
my $skipped = 0;
my $combined = 0;

GetOptions(  'vcfin|i=s'  => \$infile,
    'window|w=i'    => \$windowsize,
    'mismatch|m=i'    => \$mismatches,
    'collapse|c'    => \$collapse,
    'out|o=s'       => \$outfolder,
    'help|h'  => \$help);

if($help){printUsage()}
if(!$infile){printUsage()}

my $prefix = substr(basename($infile),0,index(basename($infile),".vcf"));
if($outfolder){
  if(! -d $outfolder){system("mkdir -p $outfolder")}
  $prefix = "$outfolder/$prefix";
}
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
my $currentAltLoci;
my $c=0;
while (<IN>) {
  if($_ =~ /^#/){ # print out header
    print OUT $_;
    print OUT2 $_;
  }else{
    @fields = split(/\t/,$_);
    # clean hash of arrays if this is a new chromosome - write buffered variants for this chromosome
    if($fields[$CHR] ne $currentChromosome){
      printVariants();
      %positions = ();
      %variants = ();
      %flag = ();
      $currentChromosome = $fields[$CHR];
    }

    my @infoCurrent = split(/;/,$fields[$INFO]);
    $currentAltLoci = $infoCurrent[0];

    # check windowsize and shift entries for already known altLoci
    if(exists $positions{$currentAltLoci}){
      while(scalar(@{$positions{$currentAltLoci}}) > 0 && ($fields[$POS] - $positions{$currentAltLoci}[0] > $windowsize) ){
        if($flag{$currentAltLoci}[0] == 0){
          print OUT $variants{$currentAltLoci}[0];
          $valid++;
        }else{ 
          print OUT2 $variants{$currentAltLoci}[0];
          $skipped++;
        }
        shift(@{$positions{$currentAltLoci}});
        shift(@{$variants{$currentAltLoci}});
        shift(@{$flag{$currentAltLoci}});
      }
    }
    addVariant($_);
    #flag to filter if more than #mismatches are in the array
    if(scalar(@{$positions{$currentAltLoci}})> $mismatches){
      foreach(@{$flag{$currentAltLoci}}){
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

#print STDERR "[INFO] bgzip & tabix\n";
system("sortBed -header -i $outfilevalide | bgzip -c > ${outfilevalide}.gz; rm $outfilevalide; tabix -f ${outfilevalide}.gz");
system("sortBed -header -i $outfileskipped | bgzip -c > ${outfileskipped}.gz; rm $outfileskipped; tabix -f ${outfileskipped}.gz");

#print STDERR "window\tmismatch\ttotal\tskipped\tvalid\tcollapsed\n";
printf STDERR "%d\t%d\t%d\t%d\t%d\t%d\n",$windowsize,$mismatches,$skipped+$valid+$combined,$skipped,$valid,$combined;

### FUNCTIONS

##
# add variants to list and combine if flag is set
##
sub addVariant{
  my @fieldsCurrent = split(/\t/,$_);
  my @infoCurrent = split(/;/,$fieldsCurrent[$INFO]);
  my $altLociCurrent = $infoCurrent[0];
  if($collapse){
    if(!exists $variants{$altLociCurrent} || scalar(@{$positions{$currentAltLoci}}) == 0){ # add new arrays for unknown altLoci
      $positions{$altLociCurrent} = [$fields[$POS]];
      $variants{$altLociCurrent}  = [$_];
      $flag{$altLociCurrent}      = [0];
    }else{
      my $prevVariant = pop(@{$variants{$altLociCurrent}});
      my @fieldsPrevious = split(/\t/,$prevVariant);
      my @infoPrevious = split(/;/,$fieldsPrevious[$INFO]);
      # combination
      if($infoCurrent[0] eq $infoPrevious[0] && $fieldsPrevious[$POS] + length($fieldsPrevious[$REF]) == $fieldsCurrent[$POS] &&  length($fieldsPrevious[$REF]) ==  length($fieldsPrevious[$ALT]) && length($fieldsCurrent[$REF]) ==  length($fieldsCurrent[$ALT]) ){
        $fieldsPrevious[$REF] = "$fieldsPrevious[$REF]$fieldsCurrent[$REF]";
        $fieldsPrevious[$ALT] = "$fieldsPrevious[$ALT]$fieldsCurrent[$ALT]";
        push(@{$variants{$altLociCurrent}},join("\t",@fieldsPrevious));
        $combined++;
      }else{ # no combination
        push(@{$variants{$altLociCurrent}},$prevVariant);
        push(@{$variants{$altLociCurrent}},$_);
        push(@{$positions{$altLociCurrent}},$fieldsCurrent[$POS]);
        push(@{$flag{$altLociCurrent}},0);
      }
      
    }
  }else{
    push(@{$positions{$altLociCurrent}}, $fieldsCurrent[$POS]);
    push(@{$variants{$altLociCurrent}},$_);
    push(@{$flag{$altLociCurrent}},0);
  }
}

sub printVariants{
  for my $key (keys %variants){
    for (my $i=0; $i < scalar(@{$variants{$key}}); $i++){
      if($flag{$key}[$i] == 0){ 
        print OUT $variants{$key}[$i];
        $valid++;
      }else{ 
        print OUT2 $variants{$key}[$i];
        $skipped++;
      }
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
  -i   --vcfin    <path to VCF file>
  -w   --window  windowsize
  -m  --mismatch  allowed mismatches
  -h  --help    show this help
  -c  --collapse  collapse neighboring variants
  -o  --out    <path to outfolder> (optional)
