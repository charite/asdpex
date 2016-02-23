package de.charite.compbio.hg38altlociselector;

import de.charite.compbio.hg38altlociselector.cmd.AlignCommand;
import de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand;
import de.charite.compbio.hg38altlociselector.cmd.AnnotateVCFCommand;
import de.charite.compbio.hg38altlociselector.cmd.CreateDatabaseCommand;
import de.charite.compbio.hg38altlociselector.cmd.CreateFastaCommand;
import de.charite.compbio.hg38altlociselector.cmd.CreateSeedCommand;
import de.charite.compbio.hg38altlociselector.cmd.DownloadCommand;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.util.HelpFormatter;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {

        if (args.length == 0) {
            // No arguments, print top level help and exit.
            printTopLevelHelp();
            System.exit(1);
        }

        // Create the corresponding command.
        AltLociSelectorCommand cmd = null;
        try {
            if (args[0].equals("align"))
                cmd = new AlignCommand(args);
            else if (args[0].equals("annotate"))
                cmd = new AnnotateVCFCommand(args);
            else if (args[0].equals("create-db"))
                cmd = new CreateDatabaseCommand(args);
            else if (args[0].equals("create-fa"))
                cmd = new CreateFastaCommand(args);
            else if (args[0].equals("create-seed"))
                cmd = new CreateSeedCommand(args);
            else if (args[0].equals("download"))
                cmd = new DownloadCommand(args);
            else
                System.err.println("unrecognized command " + args[0]);
        } catch (CommandLineParsingException e) {
            System.err.println("problem with parsing command line options: " + e.getMessage());
        } catch (HelpRequestedException e) {
            return; // no error, user wanted help
        }

        // Stop if no command could be created.
        if (cmd == null)
            System.exit(1);

        // Execute the command.
        try {
            cmd.run();
        } catch (AltLociSelectorException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }

    }

    private static void printTopLevelHelp() {
        HelpFormatter.printHeader();

        StringBuilder sb = new StringBuilder();
        sb.append("Command: align       construct fasta and seed files and do the alignments").append("\n");
        sb.append("         annotate    functional annotation of VCF files").append("\n");
        sb.append("         create-db   creates a SQLite database used for the program").append("\n");
        sb.append("         create-fa   construct fasta files for the alignments").append("\n");
        sb.append("         create-seed construct seed files for the alignments from the NCBI alignments").append("\n");
        sb.append("         download    download transcript database  (not yet implemented)").append("\n").append("\n");
        sb.append("Example: java -jar hg38altlociselector.jar download GRCh38").append("\n");
        sb.append("         java -jar hg38altlociselector.jar create-fa -o data/").append("\n").append("\n");
        HelpFormatter.printUsage(sb.toString());
    }
}
