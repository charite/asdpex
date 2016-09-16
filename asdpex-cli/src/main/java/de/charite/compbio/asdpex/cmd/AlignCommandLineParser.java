/**
 * 
 */
package de.charite.compbio.asdpex.cmd;

import java.io.File;

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
        options.addOption(Option.builder("h").longOpt("help").desc("show this help").build());
        options.addOption(
                Option.builder("n").longOpt("non").desc("do not split the alignments at 'N' stretches").build());
        options.addOption(
                Option.builder("i").longOpt("noindel").desc("do not split the alignments at large InDels").build());
        options.addOption(Option.builder("t").longOpt("tmp").desc(
                "temporary folder for seeds, fastA, etc. files, defaults to \"<data-dir>" + File.separator + "tmp\"")
                .hasArg().build());
        // required
        options.addOption(Option.builder("o").longOpt("out-dir")
                .desc("output folder for generated VCF file(s) \"results\" (mandatory)").hasArg().required().build());
        options.addOption(Option.builder("d").longOpt("data-dir")
                .desc("target folder for downloaded files, defaults to \"data\" (mandatory)").hasArg().required()
                .build());
        options.addOption(Option.builder("q").longOpt("sql").desc("path to the final SQLite database").hasArg()
                .required().build());
        options.addOption(Option.builder("s").longOpt("seqan")
                .desc("path to the SeqAn aligner \"regionalign2vcf\" (mandatory)").hasArg().required().build());
        parser = new DefaultParser();
    }

    public Hg38altLociSeletorOptions parse(String[] args) throws ParseException, HelpRequestedException {
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp(options, Hg38altLociSeletorOptions.Command.ALIGN);
        }

        // Fill the resulting Options.
        Hg38altLociSeletorOptions asdpexOptions = new Hg38altLociSeletorOptions();
        asdpexOptions.command = Hg38altLociSeletorOptions.Command.ALIGN;

        if (cmd.hasOption("help")) {
            printHelp(asdpexOptions);
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("non"))
            asdpexOptions.setAlignmentSplitNs(false);

        if (cmd.hasOption("noindel"))
            asdpexOptions.setAlignmentSplitIndels(false);

        if (cmd.hasOption("out-dir"))
            asdpexOptions.setResultsFolder(cmd.getOptionValue("out-dir"));
        else {
            printHelp(asdpexOptions);
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("seqan"))
            asdpexOptions.setSeqanAlign(cmd.getOptionValue("seqan"));
        else {
            printHelp(asdpexOptions);
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("sql")) {
            asdpexOptions.setSqlitePath(cmd.getOptionValue("sql"));
        } else {
            asdpexOptions.error = "Missing path to SQLite database: -s";
            printHelp(asdpexOptions);
        }

        if (cmd.hasOption("data-dir"))
            asdpexOptions.setDataPath(cmd.getOptionValue("data-dir"));
        else {
            printHelp(asdpexOptions);
            throw new HelpRequestedException();
        }

        return asdpexOptions;
    }

    private void printHelp(Options options2, Command cmd) {
        org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
        formatter.printHelp("java -jar hg38altlociselector.jar " + cmd, this.options, true);
        System.exit(HelpFormatter.Failure.MISSING_PATH.ordinal());

    }

    private void printHelp(Hg38altLociSeletorOptions options) {
        System.err.println(
                "The 'align' command will do all the stuff and create a complete ready for analysis dataset. Will take forever ;)");
        org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
        formatter.printHelp("java -jar asdpex.jar " + options.command.toString(), "options:", this.options,
                options.error, true);
        // HelpFormatter.printUsage(options.command.toString(), "wie das alles
        // funzt", options);
        System.exit(HelpFormatter.Failure.MISSING_DATA_PATH.ordinal());
    }

}
