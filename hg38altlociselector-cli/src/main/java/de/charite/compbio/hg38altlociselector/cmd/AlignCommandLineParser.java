/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.util.HelpFormatter;

/**
 * Helper class for parsing the commandline of the align command.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public final class AlignCommandLineParser {

    /** options representation for the Apache commons command line parser */
    protected Options options;
    /** the Apache commons command line parser */
    protected CommandLineParser parser;

    /**
     * 
     */
    public AlignCommandLineParser() {
        initializeParser();
    }

    private void initializeParser() {
        options = new Options();
        options.addOption("h", "help", false, "show this help");
        options.addOption("d", "data-dir", true,
                "target folder for downloaded files, defaults to \"data\" (mandatory)");
        options.addOption("o", "out-dir", true, "output folder for generated VCF file(s) \"results\" (mandatory)");
        options.addOption("s", "seqan", true, "path to the SeqAn aligner \"regionalign2bed\" (mandatory) ");
        options.addOption(null, "tmp", true,
                "temporary folder for seeds, fastA, etc. files, defaults to \"<data-dir>" + File.separator + "tmp\"");
        parser = new DefaultParser();
    }

    public Hg38altLociSeletorOptions parse(String[] args) throws ParseException, HelpRequestedException {
        CommandLine cmd = parser.parse(options, args);

        // Fill the resulting Options.
        Hg38altLociSeletorOptions result = new Hg38altLociSeletorOptions();
        result.command = Hg38altLociSeletorOptions.Command.ALIGN;

        if (cmd.hasOption("help")) {
            printHelp();
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("data-dir"))
            result.setDataPath(cmd.getOptionValue("data-dir"));
        else {
            printHelp(result);
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("out-dir"))
            result.setResultsFolder(cmd.getOptionValue("out-dir"));
        else {
            printHelp(result);
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("seqan"))
            result.setSeqanAlign(cmd.getOptionValue("seqan"));
        else {
            printHelp(result);
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("single"))
            result.singleAltLociFile = true;

        return result;
    }

    private void printHelp() {
        System.err.println("The 'align' command will do most of ");
    }

    private void printHelp(Hg38altLociSeletorOptions options) {
        StringBuilder sb = new StringBuilder();
        org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
        formatter.printHelp("java -jar hg38altlociselector.jar " + options.command.toString(), "options:", this.options,
                options.error, true);
        // HelpFormatter.printUsage(options.command.toString(), "wie das alles
        // funzt", options);
        System.exit(HelpFormatter.Failure.MISSING_DATA_PATH.ordinal());
    }

}
