/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.data.AccessionInfo;
import de.charite.compbio.hg38altlociselector.data.AltScaffoldPlacementInfo;
import de.charite.compbio.hg38altlociselector.data.NCBIgffAlignmentMatch;
import de.charite.compbio.hg38altlociselector.data.RegionInfo;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.io.parser.AccessionInfoParser;
import de.charite.compbio.hg38altlociselector.io.parser.AltScaffoldPlacementParser;
import de.charite.compbio.hg38altlociselector.io.parser.NCBIgffAlignmentParser;
import de.charite.compbio.hg38altlociselector.io.parser.RegionInfoParser;
import de.charite.compbio.hg38altlociselector.util.IOUtil;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class CreateSeedCommand extends AltLociSelectorCommand {

	public CreateSeedCommand(String[] args) throws CommandLineParsingException, HelpRequestedException {
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
			return new CreateSeedCommandLineParser().parse(args);
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
		// alts_accessions
		System.out.println("[INFO] Read alt_loci accessions");
		AccessionInfoParser aiParser = new AccessionInfoParser(options.altAccessionsPath);
		ImmutableMap<String, AccessionInfo> aiMap = aiParser.parse();
		System.out.println("[INFO] found " + aiMap.size() + " alt_loci");

		// scaffold_placement
		System.out.println("[INFO] Read alt_loci placement");
		AltScaffoldPlacementParser asParser = new AltScaffoldPlacementParser(options.altScaffoldPlacementPath);
		ImmutableMap<String, AltScaffoldPlacementInfo> asMap = asParser.parse();
		System.out.println("[INFO] found placement for " + asMap.size() + " alt_loci");

		// regions definitions
		System.out.println("[INFO] Read region definitions");
		RegionInfoParser regParser = new RegionInfoParser(options.genomicRegionsDefinitionsPath);
		ImmutableMap<String, RegionInfo> regMap = regParser.parse();
		System.out.println("[INFO] found " + regMap.size() + " regions definitions");

		for (AltScaffoldPlacementInfo scaffold : asMap.values()) {
			AccessionInfo currentAI = aiMap.get(scaffold.getAltScafAcc());
			RegionInfo currentReg = regMap.get(scaffold.getRegion());
			String gff = scaffold.getAltScafAcc() + "_" + scaffold.getParentAcc() + ".gff";
			// System.out.println(gff);
			ImmutableList<NCBIgffAlignmentMatch> matches = null;
			if (new File(options.alignmentPath, gff).exists()) {
				matches = new NCBIgffAlignmentParser(new File(options.alignmentPath, gff)).parse();
			} else {
				System.err.println("File is missing: " + gff);
				continue;
			}
			// System.out.println("Matches: " + matches.size());
			// build
			// System.out.println(
			// "difference start region <-> alt-loci:\t" + (scaffold.getParentStart() - currentReg.getStart()));
			// System.out.println("tail region <-> alt-loci:\t\t" + (currentReg.getStop() - scaffold.getParentStop()));
			// System.out.println((scaffold.getAltScafStop() + scaffold.getParentStart() - currentReg.getStart()) + "\t"
			// + (scaffold.getParentStop() - currentReg.getStart()));
			// System.out.println(createFastaIdentifier(currentAI));
			try {
				createMatchesFile(options.seedInfoPath, createFastaIdentifier(currentAI) + "_extended.tab", matches,
						(scaffold.getParentStart() - currentReg.getStart()),
						(currentReg.getStop() - scaffold.getParentStop()));
			} catch (IOException e) {
				System.err.println("[ERROR] failed to create seed info file for sample: " + scaffold.getAltScafAcc());
				e.printStackTrace();
			}
		}
	}

	private void createMatchesFile(String path, String filename, ImmutableList<NCBIgffAlignmentMatch> matches,
			int offset, int tail) throws IOException {

		File file = new File(path, filename);
		final BufferedWriter out;
		if (file.exists()) {
			System.out.println("[WARN] file already exists: " + file.getCanonicalPath());
		}
		file.getParentFile().mkdirs();
		out = IOUtil.getBufferedFileWriter(file);
		boolean first = true;
		int c = 0;

		for (NCBIgffAlignmentMatch match : matches) {
			c++;
			// extend the first seed to the begin of the region
			if (first) {
				// System.out.println(String.format("%d\t%d\t%d\n", 0, 0, match.getLength() + offset));
				out.write(String.format("%d\t%d\t%d\n", 0, 0, match.getLength() + offset));
				first = false;
				continue;
			}
			// System.out.println(c + "\t" + matches.size());
			if (c == matches.size()) {
				out.write(String.format("%d\t%d\t%d\n", match.getRef_start() + offset, match.getAlt_start() + offset,
						match.getLength() + tail));
				// System.out.println(String.format("%d\t%d\t%d\n", match.getRef_start() + offset,
				// match.getAlt_start() + offset, match.getLength() + tail));
				continue;
			}
			out.write(String.format("%d\t%d\t%d\n", match.getRef_start() + offset, match.getAlt_start() + offset,
					match.getLength()));
		}
		IOUtil.close(out);
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

}
