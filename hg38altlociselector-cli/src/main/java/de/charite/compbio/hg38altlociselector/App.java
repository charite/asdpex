package de.charite.compbio.hg38altlociselector;

import java.net.URL;

/**
 * Hello world!
 *
 */
public class App {
	private String getResource() {
		URL url = this.getClass().getResource("/data/test.txt");
		String filename = url.getFile();
		System.out.println(filename);
		return "bla";
	}

	public static void main(String[] args) {
		String filename = "/home/mjaeger/server/data6/home/ngsknecht/Resource/bwa.kit/hs38DH.fa";
		// App app = new App();
		// System.out.println(app.getResource());
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
}
