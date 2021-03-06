/**
 * 
 */
package de.charite.compbio.asdpex.io.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.asdpex.data.AltScaffoldPlacementInfo;
import de.charite.compbio.asdpex.data.AltScaffoldPlacementInfo.AltScaffoldPlacementInfoBuilder;
import de.charite.compbio.asdpex.exceptions.AccessionInfoParseException;
import de.charite.compbio.asdpex.util.IOUtil;

/**
 * Parser for the NCBI alt_scaffold_placement.txt files.
 * 
 * @author Marten Jäger (marten.jaeger@charite.de)
 *
 */
public class AltScaffoldPlacementParser {

    private File file;
    /**
     * Number of tab-separated fields in then NCBI alt_scaffold_placement.txt file
     */
    private static final int NFIELDS = 15;

    /**
     * Dummy to prevent from using
     */
    @SuppressWarnings("unused")
    private AltScaffoldPlacementParser() {
    }

    /**
     * 
     * @param filepath
     *            path to the file with the scaffold placements.
     * 
     */
    public AltScaffoldPlacementParser(String filepath) {
        this.file = new File(filepath);
    }

    /**
     * Parse/read in the <em>*alt_scaffold_placement.txt</em> file and build a {@link ImmutableMap} of
     * {@link AltScaffoldPlacementInfo}s and the corresponding RefSeq accession version ids (e.g. NT_187515.1) as keys.
     * 
     * 
     * @return
     */
    public ImmutableMap<String, AltScaffoldPlacementInfo> parse() {
        ImmutableMap.Builder<String, AltScaffoldPlacementInfo> result = new ImmutableMap.Builder<String, AltScaffoldPlacementInfo>();
        BufferedReader reader = null;
        String line;
        try {
            reader = IOUtil.getBufferedReaderFromFileName(this.file);
            while ((line = reader.readLine()) != null) {
                try {
                    if (line.startsWith("#"))
                        continue;
                    AltScaffoldPlacementInfoBuilder siBuilder = createBuilderFromLine(line);
                    AltScaffoldPlacementInfo info = siBuilder.build();
                    result.put(info.getAltScafAcc(), info);
                } catch (AccessionInfoParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // LOGGER.error("failed to read line from file: " + this.file.getAbsolutePath());
            e.printStackTrace();
        }
        IOUtil.close(reader);
        return result.build();
    }

    private AltScaffoldPlacementInfoBuilder createBuilderFromLine(String line) throws AccessionInfoParseException {
        AltScaffoldPlacementInfoBuilder builder = new AltScaffoldPlacementInfoBuilder();
        String[] fields = line.split("\t");
        if (fields.length != AltScaffoldPlacementParser.NFIELDS) {
            String error = String.format(
                    "Malformed line in NCBI alt_scaffold_placement.txt file:\n%s\nExpected %d fields but there were %d",
                    line, NFIELDS, fields.length);
            throw new AccessionInfoParseException(error);
        }
        builder.altAssemblyName(fields[0]);
        builder.primAssemblyName(fields[1]);
        builder.altScafName(fields[2]);
        builder.altScafAcc(fields[3]);
        builder.parentType(fields[4]);
        builder.parentName(fields[5]);
        builder.parentAcc(fields[6]);
        builder.region(fields[7]);
        builder.strand(fields[8].equals("-") ? false : true);
        // Why not check for the 'b'? Right now the alignments for all entries with a 'b' are on the '+' strand and
        // therefore it's simply KISS prinzip

        try {
            builder.altScafStart(Integer.parseInt(fields[9]));
        } catch (NumberFormatException e) {
            throw new AccessionInfoParseException(
                    "Failed to parse Integer from alt_scaf_start field entry: " + fields[9]);
        }
        try {
            builder.altScafStop(Integer.parseInt(fields[10]));
        } catch (NumberFormatException e) {
            throw new AccessionInfoParseException(
                    "Failed to parse Integer from alt_scaf_stop field entry: " + fields[10]);
        }
        try {
            builder.parentStart(Integer.parseInt(fields[11]));
        } catch (NumberFormatException e) {
            throw new AccessionInfoParseException(
                    "Failed to parse Integer from parent_start field entry: " + fields[11]);
        }
        try {
            builder.parentStop(Integer.parseInt(fields[12]));
        } catch (NumberFormatException e) {
            throw new AccessionInfoParseException(
                    "Failed to parse Integer from parent_stop field entry: " + fields[12]);
        }
        try {
            builder.altStartTail(Integer.parseInt(fields[13]));
        } catch (NumberFormatException e) {
            throw new AccessionInfoParseException(
                    "Failed to parse Integer from alt_start_tail field entry: " + fields[13]);
        }
        try {
            builder.altStopTail(Integer.parseInt(fields[14]));
        } catch (NumberFormatException e) {
            throw new AccessionInfoParseException(
                    "Failed to parse Integer from alt_stop_tail field entry: " + fields[14]);
        }

        return builder;
    }

}
