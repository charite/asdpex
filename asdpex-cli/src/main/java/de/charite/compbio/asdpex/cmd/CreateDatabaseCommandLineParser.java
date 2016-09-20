/**
 * 
 */
package de.charite.compbio.asdpex.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.asdpex.Hg38altLociSeletorOptions;
import de.charite.compbio.asdpex.Hg38altLociSeletorOptions.Command;
import de.charite.compbio.asdpex.exceptions.HelpRequestedException;
import de.charite.compbio.asdpex.util.HelpFormatter;

/**
 * Helper class for parsing the commandline of the create-db command.
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
        options.addOption(
                Option.builder("d").longOpt("data-dir").desc("folder with the downloaded data files").hasArg().build());
        options.addOption(Option.builder("a").longOpt("asdp").desc("path to the ASDP VCF file").hasArg().build());
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
        Hg38altLociSeletorOptions asdpexOptions = new Hg38altLociSeletorOptions();
        asdpexOptions.command = Hg38altLociSeletorOptions.Command.CREATE_DB;

        if (cmd.hasOption("help")) {
            printHelp(asdpexOptions);
            throw new HelpRequestedException();
        }
        if (cmd.hasOption("sql")) {
            asdpexOptions.setSqlitePath(cmd.getOptionValue("sql"));
        } else {
            asdpexOptions.error = "Missing path to SQLite database: -s";
            printHelp(asdpexOptions);
        }
        if (cmd.hasOption("data-dir")) {
            asdpexOptions.setDataPath(cmd.getOptionValue("data-dir"));
        } else if (cmd.hasOption("asdp")) {
            asdpexOptions.setAltlociVcf(cmd.getOptionValue("asdp"));
        } else {
            asdpexOptions.error = "Missing path to data folder: -d\nor ASDP file: -a";
            printHelp(asdpexOptions);
        }
        return asdpexOptions;

    }

    private void printHelp(Options options2, Command cmd) {
        org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
        formatter.printHelp("java -jar hg38altlociselector.jar " + cmd, this.options, true);
        System.exit(HelpFormatter.Failure.MISSING_VCF.ordinal());

    }

    private void printHelp(Hg38altLociSeletorOptions options) {
        org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
        formatter.printHelp("java -jar hg38altlociselector.jar " + options.command.toString(), "options:", this.options,
                options.error, true);
        System.exit(HelpFormatter.Failure.MISSING_DATA_PATH.ordinal());
    }

}
