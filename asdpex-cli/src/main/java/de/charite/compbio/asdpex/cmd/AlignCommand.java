/**
 * 
 */
package de.charite.compbio.asdpex.cmd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.asdpex.Hg38altLociSeletorOptions;
import de.charite.compbio.asdpex.data.AltScaffoldPlacementInfo;
import de.charite.compbio.asdpex.data.NCBIgffAlignment;
import de.charite.compbio.asdpex.data.NCBIgffAlignmentElement;
import de.charite.compbio.asdpex.data.NCBIgffAlignmentElementType;
import de.charite.compbio.asdpex.db.DatabaseManger;
import de.charite.compbio.asdpex.exceptions.AltLociSelectorException;
import de.charite.compbio.asdpex.exceptions.CommandLineParsingException;
import de.charite.compbio.asdpex.exceptions.HelpRequestedException;
import de.charite.compbio.asdpex.io.parser.NCBIgffAlignmentParser;
import de.charite.compbio.asdpex.io.writer.FastaFileWriter;
import de.charite.compbio.asdpex.util.AlignmentUtil;
import de.charite.compbio.asdpex.util.IOUtil;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;
import htsjdk.samtools.util.SequenceUtil;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AlignCommand extends AltLociSelectorCommand {

    /**
     * @param args
     * @throws HelpRequestedException
     * @throws CommandLineParsingException
     */
    public AlignCommand(String[] args) throws CommandLineParsingException, HelpRequestedException {
        super(args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand# parseCommandLine(java.lang.String[])
     */
    @Override
    protected Hg38altLociSeletorOptions parseCommandLine(String[] args)
            throws CommandLineParsingException, HelpRequestedException {
        try {
            return new AlignCommandLineParser().parse(args);
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
        // TODO not simple kill app but show again the help if terminated by missing file/path

        // check the reference file
        final ReferenceSequenceFile refFile = ReferenceSequenceFileFactory
                .getReferenceSequenceFile(new File(options.getReferencePath()));
        if (!refFile.isIndexed()) {
            System.err.println("[ERROR] The FastA is not index - please index file first and run again.");
            System.exit(1);
        } else
            System.out.println("[INFO] Reference fastA file checked: " + options.getReferencePath());

        // check that the SeqAn aligner does exists
        if (!new File(options.getSeqanAlign()).exists()) {
            System.err.println("[ERROR] The SeqAn aligner could not be found at position: " + options.getSeqanAlign());
            System.exit(0);
        } else
            System.out.println("[INFO] SeqAn aligner checked: " + options.getSeqanAlign());

        // Database access
        DatabaseManger dbman = new DatabaseManger(options.getSqlitePath());

        // load placement of the scaffolds
        ImmutableList<AltScaffoldPlacementInfo> placements = null;
        try {
            placements = dbman.getAltScaffoldPlacementInfos();
            System.out.println("[INFO] loaded " + placements.size() + " alternate loci placements");
        } catch (SQLException e1) {
            System.err.println(
                    "[ERROR] Failed to load the alt_scaffold_placements from database: " + options.getSqlitePath());
            e1.printStackTrace();
        }

        // visualisation
        System.out.println("\t[INFO] processing alt. loci");
        System.out.println("\t0%       50%       100%");
        System.out.print("\t|.........|.........|\n\t");
        int c = 1;
        int limit = 0;
        for (AltScaffoldPlacementInfo placement : placements) {
            // progress
            if (100.0 * c++ / placements.size() > limit) {
                limit += 5;
                System.out.print("*");
            }

            // 1. check GFF: file exists and get alignments
            ArrayList<NCBIgffAlignment> alignments;
            try {
                ImmutableList<NCBIgffAlignment> originalAlignments = getAlignments(placement, dbman);
                // System.out.println("[INFO] no. of original alignments: " + originalAlignments.size());
                alignments = AlignmentUtil.splitupAlignmentAtLargeIndels(
                        originalAlignments.toArray(new NCBIgffAlignment[originalAlignments.size()]));
                // System.out.println("[INFO] no. of split alignments: " + alignments.size());
            } catch (FileNotFoundException e1) {
                System.out.println("[WARN] skipping - missing GFF file");
                e1.printStackTrace();
                continue;
            }
            // System.out.println("n alignments: " + alignments.size());

            // 2. FastA
            // 2.1 generate identifier used for alt locus fastA and seed file
            String identifierAltLocusFasta;
            try {
                identifierAltLocusFasta = dbman.getFastaIdentifierAltLocus(placement.getAltScafAcc());
            } catch (SQLException e) {
                System.out.println("[WARN] skipping - failed to generate the fastA identifier for alternate locus: "
                        + placement.getAltScafAcc());
                continue;
            }

            // 2.2 generate identifier used for ref fastA
            String identifierRefFasta;
            try {
                identifierRefFasta = dbman.getFastaIdentifierReference(placement.getAltScafAcc());
            } catch (SQLException e) {
                System.out.println("[WARN] skipping - failed to generate the fastA identifier for reference: "
                        + placement.getAltScafAcc());
                continue;
            }

            // 3. iterate over the NCBIAlignments
            int block = 1;
            for (NCBIgffAlignment alignment : alignments) {
                // ALT LOCI
                byte[] altLoci = extractSequence(refFile, identifierAltLocusFasta, alignment.getAltStart(),
                        alignment.getAltStop(), alignment.isAltStrand());
                // REF
                byte[] ref = extractSequence(refFile, identifierRefFasta, alignment.getRefStart(),
                        alignment.getRefStop(), alignment.isRefStrand());
                System.out.println("identifierALtLocusFasta: " + identifierAltLocusFasta);
                System.out.println("identifierRefFasta: " + identifierRefFasta);
                writeFilesToDisc(identifierAltLocusFasta, placement.getRegion(), block, altLoci, ref, alignment);

                try {
                    runAlignment(identifierAltLocusFasta, block, altLoci, ref, alignment.getRefStart() - 1);
                } catch (IOException | InterruptedException e) {
                    System.err.println("Failed to run align command. That's strange ...");
                    e.printStackTrace();
                }

                block++;
            }

            // System.exit(0);
        }
        System.out.println("*");
    }

    /**
     * Parse the {@link NCBIgffAlignment}s from the file for the specific alternate loci.<br>
     * A {@link FileNotFoundException} is thrown if the GFF file could not be found at the specified location.
     * 
     * @param placement
     *            {@link AltScaffoldPlacementInfo} for the alternate loci
     * @param dbman
     *            {@link DatabaseManger} to get the additional infos
     * @return {@link List} of {@link NCBIgffAlignment}s
     * @throws FileNotFoundException
     */
    private ImmutableList<NCBIgffAlignment> getAlignments(AltScaffoldPlacementInfo placement, DatabaseManger dbman)
            throws FileNotFoundException {
        // identifier for the GFF file
        String identifierGFF;
        try {
            identifierGFF = dbman.getGffIdentifier(placement.getAltScafAcc());
        } catch (SQLException e) {
            System.out.println("[WARN] skipping - failed to generate the GFF identifier for alternate locus: "
                    + placement.getAltScafAcc());
            return null;
        }

        File gff = new File(options.getAlignmentPath(), identifierGFF + ".gff");
        if (gff.exists()) {
            return new NCBIgffAlignmentParser(gff).parse();
        } else {
            // return null;
            throw new FileNotFoundException("Missing file: " + gff);
        }
    }

    /**
     * 
     * @param identifier
     * @param block
     * @param altLoci
     * @param ref
     * @param offset
     * @throws IOException
     * @throws InterruptedException
     */
    private void runAlignment(String identifier, int block, byte[] altLoci, byte[] ref, int offset)
            throws IOException, InterruptedException {

        StringBuilder cmd = new StringBuilder();
        cmd.append(options.getSeqanAlign()).append(" -R ")
                .append(options.getTempFolder() + "/" + identifier + "_ref_" + block + ".fa").append(" -A ")
                .append(options.getTempFolder() + "/" + identifier + "_altLoci_" + block + ".fa").append(" -S ")
                .append(options.getTempFolder() + "/" + identifier + "_" + block + ".tab").append(" -V ")
                .append(options.getResultsFolder() + "/" + identifier + ".vcf").append(" -N ")
                .append(options.getTempFolder() + "/aln/" + identifier + "_" + block + ".aln").append(" -o ")
                .append(offset);
        if (block > 1)
            cmd.append(" -a");
        // check output folders exist
        System.out.println("[INFO] create outputfolder for 'aln' and 'vcf' files");
        IOUtil.checkOutFolder(new File(options.getResultsFolder()));
        IOUtil.checkOutFolder(new File(options.getTempFolder(), "aln"));

        Process p = Runtime.getRuntime().exec(cmd.toString());
        p.waitFor();
        OutputStream stdin = p.getOutputStream();
        InputStream stderr = p.getErrorStream();
        InputStream stdout = p.getInputStream();

        String line;
        // clean up if any output in stdout
        BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stdout));
        while ((line = brCleanUp.readLine()) != null) {
            System.out.println("[Stdout] " + line);
        }
        brCleanUp.close();

        // clean up if any output in stderr
        brCleanUp = new BufferedReader(new InputStreamReader(stderr));
        while ((line = brCleanUp.readLine()) != null) {
            System.out.println("[Stderr] " + line);
        }
        brCleanUp.close();
        System.out.println(p.exitValue());
    }

    @SuppressWarnings("unused")
    private ArrayList<Tuple> filterTupleByLength(ArrayList<Tuple> list, int minLength) {
        ArrayList<Tuple> result = new ArrayList<>();
        for (Tuple tuple : list) {
            if (tuple.end - tuple.start > minLength)
                result.add(tuple);
        }
        return result;
    }

    private boolean writeFilesToDisc(String idALtLoci, String idRef, int block, byte[] altLoci, byte[] ref,
            NCBIgffAlignment alignment) {
        // FASTA FILES
        // alt loci
        try {
            FastaFileWriter.createFastaFile(new File(options.getTempFolder(), idALtLoci + "_altLoci_" + block + ".fa"),
                    idALtLoci, altLoci, false);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // ref
        try {
            FastaFileWriter.createFastaFile(new File(options.getTempFolder(), idALtLoci + "_ref_" + block + ".fa"),
                    idRef, ref, false);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // SEED FILES
        try {
            createMatchesFile(options.getTempFolder(), idALtLoci + "_" + block + ".tab", alignment.getElements(), 0, 0);
        } catch (IOException e) {
            System.err.println("[ERROR] failed to create seed info file for sample: " + idALtLoci);
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Extract list
     * 
     * @param seq
     * @return
     */
    private ArrayList<Tuple> getNonNblocks(byte[] seq) {
        ArrayList<Tuple> list = new ArrayList<>();
        int start = 0;
        int stop = start;
        for (byte b : seq) {
            if (b == 'N') {
                if (start != stop)
                    list.add(new Tuple(start, stop));
                start = stop + 1;
            }
            stop++;
        }
        list.add(new Tuple(start, stop));
        return list;
    }

    /**
     * Extract list of {@link Tuple}s with 'N' positions.<br>
     * The positions are '0'-based and the start is incl., the stop excl.
     * 
     * @param seq
     *            the input sequence with alphabet [ACGTN]
     * @return List of {@link Tuple}s with 'N'-blocks
     */
    @SuppressWarnings("unused")
    private ArrayList<Tuple> getNblocks(byte[] seq) {
        ArrayList<Tuple> list = new ArrayList<>();
        int start = 0;
        int stop = start;
        for (byte b : seq) {

            if (b != 'N') {
                if (start < stop) {
                    list.add(new Tuple(start, stop));
                }
                start = stop + 1;
            }
            stop++;
        }
        if (start < stop) {
            list.add(new Tuple(start, stop));
        }
        return list;

    }

    /**
     * Extract the Sequence from the
     * 
     * @param refFile
     *            the {@link ReferenceSequenceFile} object
     * @param id
     *            is the associated fasta identifier
     * @param start
     *            inclusive, 1-based start of region.
     * @param stop
     *            inclusive, 1-based stop of region.
     * @param strand
     *            of the sequence.
     * @return
     */
    private byte[] extractSequence(ReferenceSequenceFile refFile, String id, int start, int stop, boolean strand) {
        byte[] bases = refFile.getSubsequenceAt(id, start, stop).getBases();

        if (!strand)
            SequenceUtil.reverseComplement(bases);

        return bases;
    }

    /**
     * 
     * @param path
     * @param filename
     * @param elements
     * @param offset
     * @param tail
     * @throws IOException
     */
    private void createMatchesFile(String path, String filename, ImmutableList<NCBIgffAlignmentElement> elements,
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

        for (NCBIgffAlignmentElement match : elements) {
            if (match.getType() != NCBIgffAlignmentElementType.MATCH)
                continue;
            c++;
            // extend the first seed to the begin of the region
            if (first) {
                // System.out.println(String.format("%d\t%d\t%d\n", 0, 0,
                // match.getLength() + offset));
                out.write(String.format("%d\t%d\t%d\n", 0, 0, match.getLength() + offset));
                first = false;
                continue;
            }
            // System.out.println(c + "\t" + matches.size());
            if (c == elements.size()) {
                out.write(String.format("%d\t%d\t%d\n", match.getRef_start() + offset, match.getAlt_start() + offset,
                        match.getLength() + tail));
                // System.out.println(String.format("%d\t%d\t%d\n",
                // match.getRef_start() + offset,
                // match.getAlt_start() + offset, match.getLength() + tail));
                continue;
            }
            out.write(String.format("%d\t%d\t%d\n", match.getRef_start() + offset, match.getAlt_start() + offset,
                    match.getLength()));
        }
        IOUtil.close(out);
    }

    /**
     * Inner private class, which only contains a tuple of integers to store the start and stop of sequence blocks.
     * 
     *
     * @author Marten Jäger <marten.jaeger@charite.de>
     *
     */
    private class Tuple {

        int start;
        int end;

        public Tuple(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public String toString() {
            return String.format("start: %d\tstop: %d\tlength: %d", start, end, end - start);
        }
    }

}
