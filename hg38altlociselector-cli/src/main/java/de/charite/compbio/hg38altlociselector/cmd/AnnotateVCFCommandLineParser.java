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

        options.addOption(
                Option.builder("v").longOpt("vcf").desc("VCF file to be annotated").hasArg().required().build());
        options.addOption(Option.builder("a").longOpt("alt").desc("VCF file with alt loci information").hasArg()
                .required().build());
        options.addOption(Option.builder("o").longOpt("out").desc("VCF output file with the annotations").hasArg()
                .required().build());
        options.addOption(Option.builder("d").longOpt("data").desc("folder with the downloaded data files").hasArg()
                .required().build());

        options.addOption(Option.builder("h").longOpt("help").desc("show this help").hasArg().build());
        options.addOption(Option.builder("r").longOpt("ref").desc("reference fasta file with index").hasArg().build());
        options.addOption(Option.builder(null).longOpt("tmp").desc("folder with the temporary files").hasArg().build());

        parser = new DefaultParser();
    }

    public Hg38altLociSeletorOptions parse(String[] args) throws ParseException, HelpRequestedException {
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp(options, Hg38altLociSeletorOptions.Command.ANNOTATE_VCF);
        }

        // Fill the resulting Options.
        Hg38altLociSeletorOptions result = new Hg38altLociSeletorOptions();
        result.command = Hg38altLociSeletorOptions.Command.ANNOTATE_VCF;
        if (cmd.hasOption("help")) {
            printHelp(result);
            throw new HelpRequestedException();
        }

        if (cmd.hasOption("vcf"))
            result.setInputVcf(cmd.getOptionValue("vcf"));
        else {
            result.error = "Missing sample VCF file: -v";
            printHelp(result);
        }

        if (cmd.hasOption("alt"))
            result.setAltlociVcf(cmd.getOptionValue("alt"));
        else {
            result.error = "Missing alt loci VCF file: -a";
            printHelp(result);
        }

        if (cmd.hasOption("out"))
            result.setOutputVcf(cmd.getOptionValue("out"));
        else {
            result.error = "Missing output VCF file: -o";
            printHelp(result);
        }

        if (cmd.hasOption("data"))
            result.setDataPath(cmd.getOptionValue("data"));
        else {
            result.error = "Missing path to data folder: -d";
            printHelp(result);
        }

        if (cmd.hasOption("ref"))
            result.setReferencePath(cmd.getOptionValue("ref"));
        // else {
        // result.error = "Missing indexed reference fasta file: -r";
        // printHelp(result);
        // }

        if (cmd.hasOption("tmp"))
            result.setTempFolder(cmd.getOptionValue("tmp"));
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
        // HelpFormatter.printUsage(options.command.toString(), "wie das alles
        // funzt", options);
        System.exit(HelpFormatter.Failure.MISSING_VCF.ordinal());
    }
}
