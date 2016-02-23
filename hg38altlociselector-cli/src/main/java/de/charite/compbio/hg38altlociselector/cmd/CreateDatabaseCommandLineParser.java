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
        options.addOption("h", "help", false, "show this help");
        options.addOption("d", "data-dir", true, "target folder for downloaded files, defaults to \"data\"");
        options.addOption("s", "sqlite", true, "path to the final SQLite database");
        parser = new DefaultParser();
    }

    public Hg38altLociSeletorOptions parse(String[] args) throws ParseException, HelpRequestedException {
        CommandLine cmd = parser.parse(options, args);

        // Fill the resulting Options.
        Hg38altLociSeletorOptions result = new Hg38altLociSeletorOptions();
        result.command = Hg38altLociSeletorOptions.Command.CREATE_FASTA;

        if (cmd.hasOption("help")) {
            printHelp();
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("data-dir"))
            result.setDataPath(cmd.getOptionValue("data-dir"));

        if (cmd.hasOption("sqlite"))
            result.setSqlitePath(cmd.getOptionValue("data-dir"));

        return result;
    }

    private void printHelp() {
        System.err.println("Here will somewhen be the help for the database generation");
    }

}
