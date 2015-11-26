/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AnnotateVCFCommandLineParser {
	/** options representation for the Apache commons command line parser */
	protected Options options;
	/** the Apache commons command line parser */
	protected CommandLineParser parser;

	public AnnotateVCFCommandLineParser() {
		initializeParser();
	}

	private void initializeParser() {
		options = new Options();
		options.addOption("h", "help", false, "show this help");
		options.addOption("v", "vcf", true, "VCF file to be annotated");
		options.addOption("a", "alt", false, "vcf file with alt loci information");
		parser = new DefaultParser();
	}

	public Hg38altLociSeletorOptions parse(String[] args) throws ParseException, HelpRequestedException {
		CommandLine cmd = parser.parse(options, args);

		// Fill the resulting Options.
		Hg38altLociSeletorOptions result = new Hg38altLociSeletorOptions();
		result.command = Hg38altLociSeletorOptions.Command.ANNOTATE_VCF;

		if (cmd.hasOption("help")) {
			printHelp();
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("vcf"))
			result.inputVcf = cmd.getOptionValue("vcf");

		if (cmd.hasOption("alt"))
			result.altlociVcf = cmd.getOptionValue("alt");

		return result;
	}

	private void printHelp() {
		System.err.println("Here will somewhen be the help ...");
	}
}
