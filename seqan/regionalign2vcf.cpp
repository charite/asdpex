/*
 * regionalign2bed.cpp
 *
 *  Created on: 27.08.2015
 *      Author: mjaeger
 */

#include <iostream>
#include <fstream>
#include <string>
#include <math.h>

#include <seqan/arg_parse.h>
#include <seqan/sequence.h>
#include <seqan/stream.h>
#include <seqan/seq_io.h>
#include <seqan/align.h>
#include <seqan/score.h>
#include <seqan/seeds.h>

using namespace seqan;

typedef String<char> TSequence;                 // sequence type
typedef Align<TSequence, ArrayGaps> TAlign;      // align type
typedef Row<TAlign>::Type TRow;
typedef Iterator<TRow, Rooted>::Type TRowIterator;
typedef Seed<Simple> TSeed;

const int REGION=0;
const int ALTLOCI=1;
const int VCF=2;
const int SEED=3;
const int ALN=4;

const int SVMIN=50;

inline bool file_exists (const TSequence &name) {
    return ( access( toCString(name), F_OK ) != -1 );
}


int parseCMD(StringSet<String<char> > &files, bool &append, int argc, char const ** argv){

	// Define strings
	String<char> fastaRegion;
	String<char> fastaAltLoci;
	String<char> outputVcf;
	String<char> seedInfo;
	String<char> outputAln;
	int regionStart = 0;

    // Setup ArgumentParser.
    seqan::ArgumentParser parser("regionalign2bed");

    addOption(parser, seqan::ArgParseOption(
        "R", "region", "path to the region fastA file.",seqan::ArgParseArgument::STRING, "TEXT"));

    addOption(parser, seqan::ArgParseOption(
        "A", "altloci", "path to the fastA file with the extended alternative loci.",seqan::ArgParseArgument::STRING, "TEXT"));

    addOption(parser, seqan::ArgParseOption(
        "V", "vcf", "path to the vcf file with the found differences.",seqan::ArgParseArgument::STRING, "TEXT"));

    addOption(parser, seqan::ArgParseOption(
        "S", "seed", "path to the file with the seed informations.",seqan::ArgParseArgument::STRING, "TEXT"));

    addOption(parser, seqan::ArgParseOption(
        "N", "aln", "path to the file with the final alignment.",seqan::ArgParseArgument::STRING, "TEXT"));

    addOption(parser, seqan::ArgParseOption(
        "o", "offset", "start of the region in the reference",
        seqan::ArgParseArgument::INTEGER, "INT"));
    addOption(parser, seqan::ArgParseOption(
            "a", "append", "append results to existing file"));
    // Parse command line.
    seqan::ArgumentParser::ParseResult res = seqan::parse(parser, argc, argv);

    // If parsing was not successful then exit with code 1 if there were errors.
    // Otherwise, exit with code 0 (e.g. help was printed).
    if (res != seqan::ArgumentParser::PARSE_OK)
        return -1;

    // Extract option values and print them.
    getOptionValue(fastaRegion, parser, "region");
    getOptionValue(fastaAltLoci, parser, "altloci");
    getOptionValue(outputVcf, parser, "vcf");
    getOptionValue(seedInfo, parser, "seed");
    getOptionValue(outputAln, parser, "aln");
    getOptionValue(regionStart, parser, "offset");
    append = isSet(parser,"append");

//    std::cout << "region   \t" << fastaRegion << '\n'
//              << "altLoci \t" << fastaAltLoci << '\n'
//              << "bed     \t" << outputBed << '\n'
//              << "start     \t" << regionStart << '\n';


    // append strings to stringset
	appendValue(files, fastaRegion);
	appendValue(files, fastaAltLoci);
	appendValue(files, outputVcf);
	appendValue(files, seedInfo);
	appendValue(files, outputAln);

	return regionStart;
}

/**
 * read single fasta Entry from file
 */
int getFastaEntry(String<char> fileName, CharString &id, Dna5String &seq){
	SeqFileIn seqFileIn;
    if (!open(seqFileIn, toCString(fileName)))
    {
        std::cerr << "ERROR: could not open input file " << fileName << ".\n";
        return 1;
    }
    readRecord(id, seq, seqFileIn);
	return 0;
}

/**
 * read several Fasta entries from a single file and store them in 'ids' and 'seqs'
 */
int getFastaEntries(String<char> fileName, String<CharString> &ids, String<Dna5String> &seqs){
	SeqFileIn seqFileIn;
    if (!open(seqFileIn, toCString(fileName)))
    {
        std::cerr << "ERROR: could not open input file " << fileName << ".\n";
        return 1;
    }
    readRecords(ids, seqs, seqFileIn);
	return 0;
}

/**
 * Find the differences (mismatches, indels) and write them to the vcf file ('vcfFilename'), append variants if file already exists
 */
int findDifferences(TAlign &align, TSequence chr, TSequence ref, TSequence sample, int offset, TSequence vcfFilename, bool append){
	std::ofstream vcffile;
	// HEADER
	if(! append || ! file_exists(vcfFilename) ){
		std::cout << "[INFO] create new file: " << vcfFilename << std::endl;
		vcffile.open(toCString(vcfFilename));
		vcffile << "##fileformat=VCFv4.1" << std::endl;
		vcffile << "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">" << std::endl;
		vcffile << "##INFO=<ID=SVLEN,Number=.,Type=Integer,Description=\"Difference in length between REF and ALT alleles\">" << std::endl;
		vcffile << "##INFO=<ID=SVTYPE,Number=1,Type=String,Description=\"Type of structural variant\">" << std::endl;
		vcffile << "##INFO=<ID=END,Number=1,Type=Integer,Description=\"End position of the structural variant\">" << std::endl;
		vcffile << "##INFO=<ID=RE,Number=1,Type=String,Description=\"Region\">" << std::endl;
		vcffile << "##INFO=<ID=AL,Number=1,Type=String,Description=\"Alternate Locus\">" << std::endl;
		vcffile << "##INFO=<ID=RP,Number=1,Type=Integer,Description=\"position in region\">" << std::endl;
		vcffile << "##ALT=<ID=DEL,Description=\"Deletion\">" << std::endl;
		vcffile << "##ALT=<ID=INS,Description=\"Insertion\">" << std::endl;
		vcffile << "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tSAMPLE" << std::endl;
	}else{
		std::cout << "[INFO] append to file: " << vcfFilename << std::endl;
		vcffile.open(toCString(vcfFilename), std::ios::app);
	}

    TRowIterator itRef = begin(row(align,0));
    TRowIterator itAlt = begin(row(align,1));
    TRowIterator itRevEnd = end(row(align,0));
    int counter=0;

    for (; itRef != itRevEnd; ++itRef, ++itAlt){
    	int posRef = toSourcePosition(row(align,REGION), position(itRef));
		int posAlt = toSourcePosition(row(align,ALTLOCI), position(itAlt));
//    	std::cout << toSourcePosition(row(align,0), position(itRef)) << "," << toSourcePosition(row(align, 1), position(itAlt)) << std::endl;
    	if(isGap(itRef)){ // INSERTION
    		int gaplength = countGaps(itRef);
//    		std::cout << gaplength << std::endl;
//    		Infix<String<char> >::Type inf = infix(source(row(align,1)),posAlt-1,posAlt-1+gaplength);
    		if(gaplength > SVMIN) // large INSERTION
    			vcffile << chr << "\t" << posRef + offset << "\t.\t" << source(row(align,REGION))[posRef-1] << "\t" << "<INS>" << "\t40\tPASS\tAL=" << sample << ";RE=" << ref << ";SVTYPE=INS;SVLEN=" << gaplength <<";END=" << posRef + offset << ";RP=" << posRef << "\tGT\t0|1" << std::endl;
    		else // small
    			vcffile << chr << "\t" << posRef + offset << "\t.\t" << source(row(align,REGION))[posRef-1] << "\t" << infix(source(row(align,ALTLOCI)),posAlt-1,posAlt+gaplength) << "\t40\tPASS\tAL=" << sample << ";RE=" << ref << ";RP=" << posRef << "\tGT\t0|1" << std::endl;
    		itRef += gaplength;
    		itAlt += gaplength;
    		counter++;
    	}else if (isGap(itAlt)){ // DELETION
    		int gaplength = countGaps(itAlt);
//    		std::cout << gaplength << std::endl;
//    		Infix<String<char> >::Type inf = infix(source(row(align,1)),posAlt-1,posAlt-1+gaplength);
    		if(gaplength > SVMIN) // large
    			vcffile << chr << "\t" << posRef + offset << "\t.\t" << source(row(align,REGION))[posRef-1] << "\t" << "<DEL>" << "\t40\tPASS\tAL=" << sample << ";RE=" << ref << ";SVTYPE=DEL;SVLEN=-" << gaplength <<";END=" << posRef + gaplength + offset << ";RP=" << posRef << "\tGT\t0|1" << std::endl;
    		else // small
    			vcffile << chr << "\t" << posRef + offset << "\t.\t" << infix(source(row(align,REGION)),posRef-1,posRef+gaplength) << "\t" << source(row(align,ALTLOCI))[posAlt-1] << "\t40\tPASS\tAL=" << sample << ";RE=" << ref << ";RP=" << posRef << "\tGT\t0|1" << std::endl;
//    			vcffile << chr << "\t" << posRef+1 + offset << "\t.\t" << infix(source(row(align,REGION)),posRef,posRef+gaplength) << "\t." << "\t40\tPASS\tRE=" << ref << ";\tGT\t0|1" << std::endl;
    		itRef += gaplength;
    		itAlt += gaplength;
    		counter++;
    		//    		std::cout << "Gap:\t" << countGaps(itAlt) << std::endl;
    	}else if( *itRef != *itAlt ){ // POINTMUTATION
    		vcffile << chr << "\t" << posRef + 1 + offset << "\t.\t" << source(row(align,REGION))[posRef] << "\t" << source(row(align,ALTLOCI))[posAlt] << "\t40\tPASS\tAL=" << sample << ";RE=" << ref << ";RP=" << posRef << "\tGT\t0|1" << std::endl;
    		counter++;
//    		std::cout << "MM: " << *itRef << "\t" << *itAlt << std::endl;
    	}
    }

    vcffile.close();
	return counter;
}

/**
 * Find the diferences (mismatches, insdels) and write them to the
 */
int findDifferences(TAlign &align, TSequence chr, TSequence ref, TSequence sample, int offset){
	return findDifferences(align, chr, ref, sample, offset, ref +=".vcf", false);
}


/**
 * feed the seedINfo table
 * skip seed if it's smaller than 'SVMIN' and cut 5% from both ends but not more than 'SVMIN'
 */
int parseSeeds(String<char> fileName, String<TSeed> &seedInfo, Dna5String &ref_seq, Dna5String &alt_seq){
	std::cout << "[INFO] Parse seeds from: " << fileName << std::endl;
    // Create streamobject
	std::ifstream infile;
    infile.open(toCString(fileName));
    int ref, alt, length;
    int all = 0;
    int valid = 0;
    while (infile >> ref >> alt >> length)
    {
    	all++;
//    	std::cout << "ref: " << ref << "\talt: " << alt << "\tlength: " << length << std::endl;
//   	std::cout << "length- adapt: " << floor(length * 0.9) << std::endl;

    	// skip seeds to small
    	if(floor(length * 0.9) < SVMIN){
    		continue;
    	}
    	valid++;
    	int trimlength = floor(length * 0.05);
    	trimlength = trimlength > SVMIN ?  SVMIN : trimlength; // maximal SVMIN
    	ref += trimlength;
    	alt += trimlength;
    	length -= (2 * trimlength);
//    	std::cout << "ref: " << ref << "\talt: " << alt << "\tlength: " << length << std::endl;
    	TSeed seed(ref, alt, length);
    	// with extended seeds ...
//        Score<int, Simple> scoringScheme(1, -1, -1);
//        std::cout << "Extend: " << ref << " / " << alt << " / " << length << std::endl;
//        extendSeed(seed, ref_seq, alt_seq, EXTEND_BOTH, scoringScheme, 3,GappedXDrop());
        appendValue(seedInfo, seed);

    }
    infile.close();
    std::cout << "Found: " << all << " seeds with " << valid << " valid of size " << SVMIN << " or larger." << std::endl;
	return 0;
}

int main(int argc, char const ** argv)
{
	// Parameter
	StringSet<String<char> > files;
	bool append;
	int regionStart = parseCMD(files, append, argc, argv);
	if(regionStart < 0)
		return 1;

	// read Fasta Files
	CharString region_id;
	Dna5String region_seq;
	String<CharString> altloci_ids;
	String<Dna5String> altloci_seqs;


	if(getFastaEntry(files[REGION],region_id,region_seq)!= 0)
		return 1;

	if(getFastaEntries(files[ALTLOCI],altloci_ids,altloci_seqs)!= 0)
		return 1;

	std::cout << std::endl << "region ID:\t" << region_id << std::endl;

	// the Alignment
	TAlign align;
	resize(rows(align), 2);
	assignSource(row(align, REGION), region_seq);

	Iterator<String<CharString> >::Type it_ids = begin(altloci_ids);
	Iterator<String<CharString> >::Type itEnd_ids = end(altloci_ids);
	Iterator<String<Dna5String> >::Type it_seqs = begin(altloci_seqs);

    for (; it_ids != itEnd_ids; goNext(it_ids),goNext(it_seqs))
    {
		std::cout << "alt-loci ID:\t" << getValue(it_ids) << std::endl;

		TSequence chrom;
//		Iterator<TSequence>::Type it = begin(getValue(it_ids));
//		Iterator<TSequence>::Type itEnd = end(getValue(it_ids));
//		for(;it != itEnd; goNext(it)){
//			if()
//			std::cout << it << std::endl;
//		}
		// extract the chromosome
		for(int i=0; i<length(value(it_ids));i++){
			if(value(it_ids)[i] == '_'){
				chrom = prefix(value(it_ids),i);
				break;
			}
		}

		// get the band
//		unsigned int bandwidth = abs(length(getValue(it_seqs)) - length(region_seq));
//		// and a little extra ;)
//		bandwidth *= 1.05;
//		std::cout << "chromosome: " << chrom << std::endl;

		assignSource(row(align, ALTLOCI), getValue(it_seqs));

//		std::cout << "bandwidth: " << bandwidth << std::endl;
//		std::cout << "calculate the alignment" << std::endl;
////		int score = globalAlignment(align, Score<int, Simple>(3, -2, 0, -10), Gotoh());
////		int score = globalAlignment(align, Score<int, Simple>(3, -2, 0, -10), -1*bandwidth, bandwidth , Gotoh());
//		int score = globalAlignment(align, Score<int, Simple>(5, -2, 0, -20), -1*bandwidth, bandwidth , Gotoh());
//		std::cout << "alignment score: " << score << std::endl;
////		std::cout << align << std::endl;


		typedef Seed<Simple> TSeed;
		String<TSeed> seedChain;
		parseSeeds(files[SEED],seedChain,region_seq,getValue(it_seqs));
//		int adapt = 17277*0.05;
//		std::cout << "adapt: " << adapt << std::endl;

		Score<int, Simple> scoringScheme(5, -2, 0, -20);
		//	    int score = bandedChainAlignment(align, seedChain, scoringScheme, adapt);

		// this one is used for the paper
		std::cout << "[INFO] Calculate the seeded and banded alignment." << std::endl;
		int score = bandedChainAlignment(align, seedChain, scoringScheme, 10);


//		std::cout << "[INFO] Calculate the global alignment." << std::endl;
//		int score = globalAlignment(align, scoringScheme, Hirschberg());

//		std::cout << "[INFO] Calculate the banded alignment." << std::endl;
//		int score = globalAlignment(align, Score<int, Simple>(0, -1, -1), -5000, 5000);


	    int c = findDifferences(align,chrom,region_id,value(it_ids),regionStart,files[VCF],append);

	    std::cout << "No. of variants found: " << c << std::endl;

	    std::cout << "[INFO] Writing alignment to file: " << files[ALN] << std::endl;
		std::ofstream alnfile;
//		TSequence alnfilename = "../results/aln/";
//		alnfilename += value(it_ids);
//		alnfilename += ".aln";
		TSequence alnfilename = files[ALN];
//		alnfile.open(toCString(alnfilename), std::ios::app);
		alnfile.open(toCString(alnfilename));
		alnfile << align << std::endl;
		alnfile.close();

    }
    return 0;
}
