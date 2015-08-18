package de.charite.compbio.hg38altlociselector;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.SubstitutionMatrixHelper;
import org.biojava.nbio.alignment.template.SequencePair;
import org.biojava.nbio.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;

import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	String filename = "/home/mjaeger/server/data6/home/ngsknecht/Resource/bwa.kit/hs38DH.fa";
//		final ReferenceSequenceFile refFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(new File(filename));
//		// ReferenceSequence ref = refFile.getSubsequenceAt("chr8", 141766834, 141800272);
//		// ReferenceSequence alt = refFile.getSequence("chr8_KI270820v1_alt");
//		ReferenceSequence ref = refFile.getSubsequenceAt("chr8", 141766834, 141766834 + 200);
//		ReferenceSequence alt = refFile.getSubsequenceAt("chr8_KI270820v1_alt", 1, 200);
//
//		// System.out.println("Reference:");
//		// try {
//		// System.out.println("\"" + new String(ref.getBases(), "UTF-8") + "\"");
//		// System.out.println("\"" + new String(alt.getBases(), "UTF-8") + "\"");
//		// } catch (UnsupportedEncodingException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//		// System.out.println("Alt_loci:");
//		// System.exit(0);
//		DNASequence target = null;
//		DNASequence alternate = null;
//		try {
//			target = new DNASequence(new String(ref.getBases(), "UTF-8"), AmbiguityDNACompoundSet.getDNACompoundSet());
//			alternate = new DNASequence(new String(alt.getBases(), "UTF-8"),
//					AmbiguityDNACompoundSet.getDNACompoundSet());
//		} catch (UnsupportedEncodingException | CompoundNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		SubstitutionMatrix<NucleotideCompound> matrix = SubstitutionMatrixHelper.getNuc4_4();
//
//		SimpleGapPenalty gapP = new SimpleGapPenalty();
//		gapP.setOpenPenalty((short) 5);
//		gapP.setExtensionPenalty((short) 2);
//
//		SequencePair<DNASequence, NucleotideCompound> psa = Alignments.getPairwiseAlignment(alternate, target,
//				PairwiseSequenceAlignerType.GLOBAL_LINEAR_SPACE, gapP, matrix);
//		System.out.println("PSA:");
//		System.out.println(psa);
    }
}
