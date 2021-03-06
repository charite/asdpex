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
 * Parser for the create-seed command arguments
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class CreateSeedCommandLineParser {

    /** options representation for the Apache commons command line parser */
    protected Options options;
    /** the Apache commons command line parser */
    protected CommandLineParser parser;

    /**
     * 
     */
    public CreateSeedCommandLineParser() {
        initializeParser();
    }

    private void initializeParser() {
        options = new Options();
        options.addOption("h", "help", false, "show this help");
        options.addOption("d", "data-dir", true, "folder with NCBI info files, defaults to \"data\"");
        options.addOption("a", "aln-dir", true,
                "folder with NCBI alignment gff files, defaults to \"data/alignments\"");
        options.addOption("s", "seed-dir", true,
                "folder where the seed info files will be stored, defaults to \"seed\"");
        parser = new DefaultParser();
    }

    public Hg38altLociSeletorOptions parse(String[] args) throws ParseException, HelpRequestedException {
        CommandLine cmd = parser.parse(options, args);

        // Fill the resulting Options.
        Hg38altLociSeletorOptions result = new Hg38altLociSeletorOptions();
        result.command = Hg38altLociSeletorOptions.Command.CREATE_SEED;

        if (cmd.hasOption("help")) {
            printHelp();
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("data-dir"))
            result.setDataPath(cmd.getOptionValue("data-dir"));
        if (cmd.hasOption("aln-dir"))
            result.setAlignmentPath(cmd.getOptionValue("aln-dir"));
        if (cmd.hasOption("seed-dir"))
            result.setSeedInfoPath(cmd.getOptionValue("seed-dir"));

        return result;
    }

    private void printHelp() {
        System.err.println("Here will somewhen be the help ...");
    }

}
