/**
 * 
 */
package de.charite.compbio.hg38altlociselector;

import java.io.File;

import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand;
import de.charite.compbio.hg38altlociselector.cmd.CreateFastaCommandLineParser;
import de.charite.compbio.hg38altlociselector.data.AccessionInfo;
import de.charite.compbio.hg38altlociselector.data.AltScaffoldPlacementInfo;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.io.parser.AccessionInfoParser;
import de.charite.compbio.hg38altlociselector.io.parser.AltScaffoldPlacementParser;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class CreateFastaCommand extends AltLociSelectorCommand {

	/**
	 * @param args
	 * @throws HelpRequestedException
	 * @throws CommandLineParsingException
	 */
	public CreateFastaCommand(String[] args) throws CommandLineParsingException, HelpRequestedException {
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
			return new CreateFastaCommandLineParser().parse(args);
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
		System.out.println("[INFO] Creating fasta files");
		if (options == null)
			System.err.println("[ERROR] option = null");
		System.out.println("[INFO] Read alt_loci accessions");
		AccessionInfoParser aiParser = new AccessionInfoParser(options.altAccessionsPath);
		ImmutableMap<String, AccessionInfo> aiMap = aiParser.parse();
		System.out.println("[INFO] found " + aiMap.size() + " alt_loci");

		System.out.println("[INFO] Read alt_loci placement");
		AltScaffoldPlacementParser asParser = new AltScaffoldPlacementParser(options.altScaffoldPlacementPath);
		ImmutableMap<String, AltScaffoldPlacementInfo> asMap = asParser.parse();
		System.out.println("[INFO] found placement for " + asMap.size() + " alt_loci");

		final ReferenceSequenceFile refFile = ReferenceSequenceFileFactory
				.getReferenceSequenceFile(new File(options.referencePath));

		for (AltScaffoldPlacementInfo scaffold : asMap.values()) {
			System.out.println(scaffold);
			break;
		}
	}

}
