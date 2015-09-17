/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.io.File;

import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.data.AccessionInfo;
import de.charite.compbio.hg38altlociselector.data.AltScaffoldPlacementInfo;
import de.charite.compbio.hg38altlociselector.data.RegionInfo;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.io.parser.AccessionInfoParser;
import de.charite.compbio.hg38altlociselector.io.parser.AltScaffoldPlacementParser;
import de.charite.compbio.hg38altlociselector.io.parser.RegionInfoParser;

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
			System.out.println(gff);
			if (new File(options.alignmentPath, gff).exists())
				System.out.println("File exits.");
			else
				System.err.println("File is missing: " + gff);
		}
	}

}
