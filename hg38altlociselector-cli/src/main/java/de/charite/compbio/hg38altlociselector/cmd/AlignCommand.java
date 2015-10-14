/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.data.AccessionInfo;
import de.charite.compbio.hg38altlociselector.data.AltScaffoldPlacementInfo;
import de.charite.compbio.hg38altlociselector.data.AlternativeLociBuilder;
import de.charite.compbio.hg38altlociselector.data.AlternativeLocus;
import de.charite.compbio.hg38altlociselector.data.RegionInfo;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.io.parser.AccessionInfoParser;
import de.charite.compbio.hg38altlociselector.io.parser.AltScaffoldPlacementParser;
import de.charite.compbio.hg38altlociselector.io.parser.RegionInfoParser;
import de.charite.compbio.hg38altlociselector.util.IOUtil;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;
import htsjdk.samtools.util.SequenceUtil;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AlignCommand extends AltLociSelectorCommand {

	/**
	 * @param args
	 * @throws HelpRequestedException
	 * @throws CommandLineParsingException
	 */
	public AlignCommand(String[] args) throws CommandLineParsingException, HelpRequestedException {
		super(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand#parseCommandLine(java.lang.String[])
	 */
	@Override
	protected Hg38altLociSeletorOptions parseCommandLine(String[] args)
			throws CommandLineParsingException, HelpRequestedException {
		try {
			return new AlignCommandLineParser().parse(args);
		} catch (ParseException e) {
			throw new CommandLineParsingException("Could not parse the command line.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand#run()
	 */
	@Override
	public void run() throws AltLociSelectorException {
		System.out.println("[INFO] Creating seed files");
		if (options == null)
			System.err.println("[ERROR] option = null");
		// alt. loci info parsing
		ImmutableList<AlternativeLocus> loci = new AlternativeLociBuilder(options.altAccessionsPath, options.altScaffoldPlacementPath, options.genomicRegionsDefinitionsPath).build();
		// Ref fasta File
		final ReferenceSequenceFile refFile = ReferenceSequenceFileFactory
				.getReferenceSequenceFile(new File(options.referencePath));
		if(!refFile.isIndexed()){
			System.err.println("The FastA is not index - please index file first and run again.");
			System.exit(1);
		}
		// visualisation
		System.out.println("[INFO] processing alt. loci");
		System.out.println("0%       50%       100%");
		System.out.println("|.........|.........|");
		int c=1;
		int limit=0;
		for (AlternativeLocus locus : loci) {
			if(100.0*c++/loci.size() > limit){
				limit+=5;
				System.out.print("*");
			}
			// identifier used for fastA and seed file
			String identifier = createFastaIdentifier(locus.getAccessionInfo());
//			System.out.println(identifier);
			if(!identifier.equals("chr17_GL000258v2_alt"))
				continue;
			
			// alt_loci
			byte[] altLoci = extractSequence(refFile, identifier, locus.getPlacementInfo().getAltScafStart(), locus.getPlacementInfo().getAltScafStop(), locus.getPlacementInfo().isStrand());
			byte[] ref = extractSequence(refFile, "chr" + locus.getPlacementInfo().getParentName(), locus.getPlacementInfo().getParentStart(), locus.getPlacementInfo().getParentStop(), true); 
			
			int i=0;
			int counter=0;
			for(byte b : altLoci){
				if(b == 'N')
					counter++;
			}
			System.out.println(identifier+": "+counter);
			
			// write fasta files
			try {
				createFastaFile(options.tempFolder + "/" + identifier + "_altLoci.fa",identifier,altLoci, false);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				createFastaFile(options.tempFolder + "/" + identifier + "_ref.fa",identifier, ref,false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("*");
	}
	
	/**
	 * Extract the Sequence from the 
	 * @param refFile
	 * @param id
	 * @param start
	 * @param stop
	 * @param strand
	 * @return
	 */
	private byte[] extractSequence(ReferenceSequenceFile refFile, String id, int start, int stop, boolean strand){
		byte[] bases = refFile.getSubsequenceAt(id, start, stop).getBases();

		if (!strand)
			SequenceUtil.reverseComplement(bases);

		return bases;
	}

	/**
	 * Creates the Fasta identifier from accessionInfo file row in the format they are used in the reference fasta
	 * files: chr<1-22|X|Y|M>_<GenBank Accession.version with '.'->'v'>_alt<br>
	 * e.g.: chr21_GL383580v2_alt
	 * 
	 * @param info
	 * @return
	 */
	private String createFastaIdentifier(AccessionInfo info) {
		StringBuilder identifier = new StringBuilder();
		identifier.append("chr").append(info.getChromosome()).append("_")
				.append(info.getGenbankAccessionVersion().replace('.', 'v')).append("_alt");
		return identifier.toString();
	}

	/**
	 * 
	 * @param path
	 * @param name
	 * @param bases
	 * @param multiFasta
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void createFastaFile(String path, String name, byte[] bases, boolean multiFasta)
			throws UnsupportedEncodingException, IOException {
		File file = new File(path);

		final BufferedWriter out;
		if (file.exists()) {
			if (options.singleAltLociFile) {
				System.out.println("[INFO] file already exists. Skipping.");
				return;
			}
		}
		file.getParentFile().mkdirs();
		if (multiFasta)
			out = IOUtil.getBufferedFileWriter(file, true);
		else
			out = IOUtil.getBufferedFileWriter(file);
		out.write(">");
		out.write(name);
		out.write("\n");

		for (int i = 0; i < bases.length; ++i) {
			if (i > 0 && i % options.fastaLineLength == 0)
				out.write("\n");
			out.write(bases[i]);
		}

		out.write("\n");
		IOUtil.close(out);
	}

}
