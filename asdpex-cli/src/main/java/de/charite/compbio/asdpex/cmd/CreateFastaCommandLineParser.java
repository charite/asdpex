/**
 * 
 */
package de.charite.compbio.asdpex.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.asdpex.Hg38altLociSeletorOptions;
import de.charite.compbio.asdpex.exceptions.HelpRequestedException;

/**
 * Helper class for parsing the commandline of the create-fasta command.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public final class CreateFastaCommandLineParser {

    /** options representation for the Apache commons command line parser */
    protected Options options;
    /** the Apache commons command line parser */
    protected CommandLineParser parser;

    /**
     * 
     */
    public CreateFastaCommandLineParser() {
        initializeParser();
    }

    private void initializeParser() {
        options = new Options();
        options.addOption("h", "help", false, "show this help");
        options.addOption("d", "data-dir", true, "target folder for downloaded files, defaults to \"data\"");
        options.addOption("s", "single", false, "split the extended alternative loci into single files");
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

        if (cmd.hasOption("single"))
            result.singleAltLociFile = true;

        return result;
    }

    private void printHelp() {
        System.err.println("Here will somewhen be the help ...");
    }

}
