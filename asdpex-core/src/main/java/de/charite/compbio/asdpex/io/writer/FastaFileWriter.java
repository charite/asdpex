/**
 * 
 */
package de.charite.compbio.asdpex.io.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import de.charite.compbio.asdpex.util.IOUtil;

/**
 * Writer for fastA files.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class FastaFileWriter {

    /**
     * default length for the used by the FastaFileWriter.
     */
    public static final int fastaLineLength = 70;

    /**
     * Create the fastA file.
     * 
     * @param path
     *            Path to the fastA file to be created
     * @param name
     *            Name or identifier in the fastA header
     * @param bases
     *            Sequence representation in {@link Byte}
     * @param length
     *            line length in the fastA file
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void createFastaFile(String path, String name, byte[] bases)
            throws UnsupportedEncodingException, IOException {
        createFastaFile(path, name, bases, false, fastaLineLength);
    }

    /**
     * Create the fastA file.
     * 
     * @param path
     *            Path to the fastA file to be created
     * @param name
     *            Name or identifier in the fastA header
     * @param bases
     *            Sequence representation in {@link Byte}
     * @param length
     *            line length in the fastA file
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void createFastaFile(String path, String name, byte[] bases, int length)
            throws UnsupportedEncodingException, IOException {
        createFastaFile(path, name, bases, false, length);
    }

    /**
     * Create the fastA file.
     * 
     * @param path
     *            Path to the fastA file to be created
     * @param name
     *            Name or identifier in the fastA header
     * @param bases
     *            Sequence representation in {@link Byte}
     * @param multiFasta
     *            append to an existing fastA file / multifastA
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void createFastaFile(String path, String name, byte[] bases, boolean multiFasta)
            throws UnsupportedEncodingException, IOException {
        createFastaFile(path, name, bases, multiFasta, fastaLineLength);
    }

    /**
     * Create the fastA file.
     * 
     * @param path
     *            Path to the fastA file to be created
     * @param name
     *            Name or identifier in the fastA header
     * @param bases
     *            Sequence representation in {@link Byte}
     * @param multiFasta
     *            append to an existing fastA file / multifastA
     * @param length
     *            line length in the fastA file
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void createFastaFile(String path, String name, byte[] bases, boolean multiFasta, int length)
            throws UnsupportedEncodingException, IOException {
        File file = new File(path);

        final BufferedWriter out;
        if (file.exists()) {
            if (!multiFasta) {
                System.out.println("[INFO] file already exists. Skipping. " + name);
                return;
            }
        }
        if (length < 1) {
            System.out.println("[WARN] fastA line length to small (" + length + ") set to default value: "
                    + FastaFileWriter.fastaLineLength);
        }
        file.getParentFile().mkdirs();
        if (multiFasta)
            out = IOUtil.getBufferedFileWriter(file, true);
        else
            out = IOUtil.getBufferedFileWriter(file);
        out.write(">");
        out.write(name);
        out.write("\n");

        for (int i = 0; i < bases.length; ++i) {
            if (i > 0 && i % length == 0)
                out.write("\n");
            out.write(bases[i]);
        }

        out.write("\n");
        IOUtil.close(out);
    }

}
