package de.charite.compbio.hg38altlociselector;

import de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand;
import de.charite.compbio.hg38altlociselector.cmd.AnnotateVCFCommand;
import de.charite.compbio.hg38altlociselector.cmd.CreateFastaCommand;
import de.charite.compbio.hg38altlociselector.cmd.CreateSeedCommand;
import de.charite.compbio.hg38altlociselector.cmd.DownloadCommand;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {

		if (args.length == 0) {
			// No arguments, print top level help and exit.
			printTopLevelHelp();
			System.exit(1);
		}

		// Create the corresponding command.
		AltLociSelectorCommand cmd = null;
		try {
			if (args[0].equals("download"))
				cmd = new DownloadCommand(args);
			else if (args[0].equals("create-fa"))
				cmd = new CreateFastaCommand(args);
			else if (args[0].equals("create-seed"))
				cmd = new CreateSeedCommand(args);
			else if (args[0].equals("annotate"))
				cmd = new AnnotateVCFCommand(args);
			else
				System.err.println("unrecognized command " + args[0]);
		} catch (CommandLineParsingException e) {
			System.err.println("problem with parsing command line options: " + e.getMessage());
		} catch (HelpRequestedException e) {
			return; // no error, user wanted help
		}

		// Stop if no command could be created.
		if (cmd == null)
			System.exit(1);

		// Execute the command.
		try {
			cmd.run();
		} catch (AltLociSelectorException e) {
			System.err.println("ERROR: " + e.getMessage());
			System.exit(1);
		}

		// String filename = "/home/mjaeger/server/data6/home/ngsknecht/Resource/bwa.kit/hs38DH.fa";
		// final ReferenceSequenceFile refFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(new
		// File(filename));
		// // ReferenceSequence ref = refFile.getSubsequenceAt("chr8", 141766834, 141800272);
		// // ReferenceSequence alt = refFile.getSequence("chr8_KI270820v1_alt");
		// ReferenceSequence ref = refFile.getSubsequenceAt("chr8", 141766834, 141766834 + 200);
		// ReferenceSequence alt = refFile.getSubsequenceAt("chr8_KI270820v1_alt", 1, 200);
		//
		// // System.out.println("Reference:");
		// // try {
		// // System.out.println("\"" + new String(ref.getBases(), "UTF-8") + "\"");
		// // System.out.println("\"" + new String(alt.getBases(), "UTF-8") + "\"");
		// // } catch (UnsupportedEncodingException e) {
		// // // TODO Auto-generated catch block
		// // e.printStackTrace();
		// // }
		// // System.out.println("Alt_loci:");
		// // System.exit(0);
		// DNASequence target = null;
		// DNASequence alternate = null;
		// try {
		// target = new DNASequence(new String(ref.getBases(), "UTF-8"), AmbiguityDNACompoundSet.getDNACompoundSet());
		// alternate = new DNASequence(new String(alt.getBases(), "UTF-8"),
		// AmbiguityDNACompoundSet.getDNACompoundSet());
		// } catch (UnsupportedEncodingException | CompoundNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// SubstitutionMatrix<NucleotideCompound> matrix = SubstitutionMatrixHelper.getNuc4_4();
		//
		// SimpleGapPenalty gapP = new SimpleGapPenalty();
		// gapP.setOpenPenalty((short) 5);
		// gapP.setExtensionPenalty((short) 2);
		//
		// SequencePair<DNASequence, NucleotideCompound> psa = Alignments.getPairwiseAlignment(alternate, target,
		// PairwiseSequenceAlignerType.GLOBAL_LINEAR_SPACE, gapP, matrix);
		// System.out.println("PSA:");
		// System.out.println(psa);
	}

	private static void printTopLevelHelp() {
		System.err.println("Program: de.charite.compbio.hg38altlociselector (functional annotation of VCF files)");
		System.err.println("Version: 0.0.1");
		System.err.println("Contact: Marten Jäger <marten.jaeger@charite.de>");
		System.err.println("");
		System.err.println("Usage: java -jar hg38altlociselector.jar <command> [options]");
		System.err.println("");
		System.err.println("Command: download      download transcript database (not yet implemented)");
		System.err.println("         annotate      functional annotation of VCF files");
		System.err.println("         create-fa     construct fasta files for the alignments");
		System.err.println("");
		System.err.println("Example: java -jar hg38altlociselector.jar download GRCh38");
		System.err.println("         java -jar hg38altlociselector.jar create-fa -o data/");
		System.err.println("");

	}
}
