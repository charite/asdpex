/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions.Command;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.util.HelpFormatter;

/**
 * Helper class for parsing the commandline of the create-fasta command.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public final class CreateDatabaseCommandLineParser {

	/** options representation for the Apache commons command line parser */
	protected Options options;
	/** the Apache commons command line parser */
	protected CommandLineParser parser;

	/**
	 * 
	 */
	public CreateDatabaseCommandLineParser() {
		initializeParser();
	}

	private void initializeParser() {
		options = new Options();
		options.addOption(Option.builder("h").longOpt("help").desc("show this help").hasArg().build());
		options.addOption(Option.builder("d").longOpt("data-dir").desc("folder with the downloaded data files").hasArg()
				.required().build());
		options.addOption(Option.builder("s").longOpt("sql").desc("path to the final SQLite database").hasArg()
				.required().build());
		parser = new DefaultParser();
	}

	public Hg38altLociSeletorOptions parse(String[] args) throws ParseException, HelpRequestedException {
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			printHelp(options, Hg38altLociSeletorOptions.Command.CREATE_DB);
		}

		// Fill the resulting Options.
		Hg38altLociSeletorOptions result = new Hg38altLociSeletorOptions();
		result.command = Hg38altLociSeletorOptions.Command.CREATE_DB;

		if (cmd.hasOption("help")) {
			printHelp(result);
			throw new HelpRequestedException();
		}

		if (cmd.hasOption("data-dir")) {
			result.setDataPath(cmd.getOptionValue("data-dir"));
		} else {
			result.error = "Missing path to data folder: -d";
			printHelp(result);
		}
		if (cmd.hasOption("sql")) {
			result.setSqlitePath(cmd.getOptionValue("sql"));
		} else {
			result.error = "Missing path to SQLite database: -s";
			printHelp(result);
		}
		return result;
	}

	private void printHelp(Options options2, Command cmd) {
		StringBuilder sb = new StringBuilder();
		org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
		formatter.printHelp("java -jar hg38altlociselector.jar " + cmd, this.options, true);
		System.exit(HelpFormatter.Failure.MISSING_VCF.ordinal());

	}

	private void printHelp(Hg38altLociSeletorOptions options) {
		StringBuilder sb = new StringBuilder();
		org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
		formatter.printHelp("java -jar hg38altlociselector.jar " + options.command.toString(), "options:", this.options,
				options.error, true);
		System.exit(HelpFormatter.Failure.MISSING_DATA_PATH.ordinal());
	}

}
