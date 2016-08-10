/**
 * 
 */
package de.charite.compbio.asdpex.cmd;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.asdpex.Hg38altLociSeletorOptions;
import de.charite.compbio.asdpex.exceptions.HelpRequestedException;
import de.charite.compbio.asdpex.util.HelpFormatter;

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
        options.addOption("o", "out-dir", true, "output folder for created fastA files, defaults to \"../fasta\"");
        options.addOption("s", "single", false, "split the extended alternative loci into single files");
        parser = new DefaultParser();
    }

    public Hg38altLociSeletorOptions parse(String[] args) throws ParseException, HelpRequestedException {
        CommandLine cmd = parser.parse(options, args);

        // Fill the resulting Options.
        Hg38altLociSeletorOptions asdpexOptions = new Hg38altLociSeletorOptions();
        asdpexOptions.command = Hg38altLociSeletorOptions.Command.CREATE_FASTA;

        if (cmd.hasOption("help")) {
            printHelp(asdpexOptions);
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("data-dir"))
            asdpexOptions.setDataPath(cmd.getOptionValue("data-dir"));
        if (!new File(asdpexOptions.getDataPath()).exists()) {
            printHelp(asdpexOptions, "Failure: 'data-dir' does not exists: " + asdpexOptions.getDataPath());
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("out-dir"))
            asdpexOptions.setFastaOutputPath(cmd.getOptionValue("out-dir"));

        if (cmd.hasOption("single"))
            asdpexOptions.singleAltLociFile = true;

        return asdpexOptions;
    }

    private void printHelp(Hg38altLociSeletorOptions options) {
        this.printHelp(options, null);
    }

    private void printHelp(Hg38altLociSeletorOptions options, String error) {
        System.err.println(
                "The 'create-fa' command will create fastA files for the regions and the corresponding alternate loci for the alignments.");

        if (error != null)
            System.err.println("\n\t" + error + "\n");
        org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
        formatter.printHelp("java -jar asdpex.jar " + options.command.toString(), "options:", this.options,
                options.error, true);
        // HelpFormatter.printUsage(options.command.toString(), "wie das alles
        // funzt", options);
        System.exit(HelpFormatter.Failure.MISSING_DATA_PATH.ordinal());
    }
}
