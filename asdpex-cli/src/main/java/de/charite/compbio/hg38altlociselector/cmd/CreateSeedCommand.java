/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.asdpex.data.AccessionInfo;
import de.charite.compbio.asdpex.data.AlternativeLociBuilder;
import de.charite.compbio.asdpex.data.AlternativeLocus;
import de.charite.compbio.asdpex.data.NCBIgffAlignmentElement;
import de.charite.compbio.asdpex.data.NCBIgffAlignmentElementType;
import de.charite.compbio.asdpex.exceptions.AltLociSelectorException;
import de.charite.compbio.asdpex.io.parser.NCBIgffAlignmentParser;
import de.charite.compbio.asdpex.util.IOUtil;
import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class CreateSeedCommand extends AltLociSelectorCommand {

    public CreateSeedCommand(String[] args) throws CommandLineParsingException, HelpRequestedException {
        super(args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand#parseCommandLine(java.lang.String[])
     */
    @Override
    protected Hg38altLociSeletorOptions parseCommandLine(String[] args)
            throws CommandLineParsingException, HelpRequestedException {
        try {
            return new CreateSeedCommandLineParser().parse(args);
        } catch (ParseException e) {
            throw new CommandLineParsingException("Could not parse the command line.", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand#run()
     */
    @Override
    public void run() throws AltLociSelectorException {
        System.out.println("[INFO] Creating seed files");
        if (options == null)
            System.err.println("[ERROR] option = null");
        ImmutableList<AlternativeLocus> loci = new AlternativeLociBuilder(options.getAltAccessionsPath(),
                options.getAltScaffoldPlacementPath(), options.getGenomicRegionsDefinitionsPath(),
                options.getChrAccessionsPath()).build();
        // alts_accessions
        System.out.println("[INFO] processing alt. loci");
        System.out.println("0%       50%       100%");
        System.out.println("|.........|.........|");
        int c = 1;
        int limit = 0;
        for (AlternativeLocus locus : loci) {
            if (100.0 * c++ / loci.size() > limit) {
                limit += 5;
                System.out.print("*");
            }

            String gff = locus.getPlacementInfo().getAltScafAcc() + "_" + locus.getPlacementInfo().getParentAcc()
                    + ".gff";
            ImmutableList<NCBIgffAlignmentElement> matches = null;
            if (new File(options.getAlignmentPath(), gff).exists()) {
                matches = new NCBIgffAlignmentParser(new File(options.getAlignmentPath(), gff)).parse().get(0)
                        .getElements();
            } else {
                System.err.println("File is missing: " + gff);
                continue;
            }

            try {
                createMatchesFile(options.getSeedInfoPath(),
                        createFastaIdentifier(locus.getAccessionInfo()) + "_extended.tab", matches,
                        (locus.getPlacementInfo().getParentStart() - locus.getRegionInfo().getStart()),
                        (locus.getRegionInfo().getStop() - locus.getPlacementInfo().getParentStop()));
            } catch (IOException e) {
                System.err.println("[ERROR] failed to create seed info file for sample: "
                        + locus.getPlacementInfo().getAltScafAcc());
                e.printStackTrace();
            }
        }

        System.out.println();
    }

    private void createMatchesFile(String path, String filename, ImmutableList<NCBIgffAlignmentElement> matches,
            int offset, int tail) throws IOException {

        File file = new File(path, filename);
        final BufferedWriter out;
        if (file.exists()) {
            System.out.println("[WARN] file already exists: " + file.getCanonicalPath());
        }
        file.getParentFile().mkdirs();
        out = IOUtil.getBufferedFileWriter(file);
        boolean first = true;
        int c = 0;

        for (NCBIgffAlignmentElement match : matches) {
            if (match.getType() != NCBIgffAlignmentElementType.MATCH)
                continue;
            c++;
            // extend the first seed to the begin of the region
            if (first) {
                // System.out.println(String.format("%d\t%d\t%d\n", 0, 0, match.getLength() + offset));
                out.write(String.format("%d\t%d\t%d\n", 0, 0, match.getLength() + offset));
                first = false;
                continue;
            }
            // System.out.println(c + "\t" + matches.size());
            if (c == matches.size()) {
                out.write(String.format("%d\t%d\t%d\n", match.getRef_start() + offset, match.getAlt_start() + offset,
                        match.getLength() + tail));
                // System.out.println(String.format("%d\t%d\t%d\n", match.getRef_start() + offset,
                // match.getAlt_start() + offset, match.getLength() + tail));
                continue;
            }
            out.write(String.format("%d\t%d\t%d\n", match.getRef_start() + offset, match.getAlt_start() + offset,
                    match.getLength()));
        }
        IOUtil.close(out);
    }

    /**
     * Creates the Fasta identifier from accessionInfo file row in the format they are used in the reference fasta
     * files: chr<1-22|X|Y|M>_<GenBank Accession.version with '.'->'v'>_alt<br>
     * e.g.: chr21_GL383580v2_alt
     * 
     * @param info
     * @return
     */
    private String createFastaIdentifier(AccessionInfo info) {
        StringBuilder identifier = new StringBuilder();
        identifier.append("chr").append(info.getChromosome()).append("_")
                .append(info.getGenbankAccessionVersion().replace('.', 'v')).append("_alt");
        return identifier.toString();
    }

}
