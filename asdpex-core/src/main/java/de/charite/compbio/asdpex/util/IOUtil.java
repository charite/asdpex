/**
 * 
 */
package de.charite.compbio.asdpex.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Simple static class to handle IO resources
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class IOUtil {

    private static IOUtil instance = null;

    /**
     * Hidden constructor.
     */
    private IOUtil() {
    }

    /**
     * Check if the Singleton is already instantiated and return instance.
     * 
     * @return {@link IOUtil} singleton
     */
    public static IOUtil getInstance() {
        if (instance == null) {
            instance = new IOUtil();
        }
        return instance;
    }

    /**
     * Finally close the {@link Reader} etc.
     * 
     * @param c
     */
    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // ignore or log
            }
        }
    }

    /**
     * Open a file handle from a gzip-compressed or uncompressed file
     *
     * @param path
     *            Path to the file to be opened
     * @param isGzip
     *            whether or not the file is gzip-compressed
     * @return Corresponding BufferedReader file handle.
     * @throws IOException
     *             on I/O errors
     */
    public static BufferedReader getBufferedReaderFromFileName(File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);
        BufferedReader br;
        if (file.getName().endsWith(".gz"))
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(fin)));
        else
            br = new BufferedReader(new InputStreamReader(new DataInputStream(fin)));
        return br;
    }

    /**
     * Opens a file handle to a new file. Handles gzip-compression depending on the file name extension
     * 
     * @param file
     *            the file to write
     * @param append
     *            append to the end of the file
     * @return the {@link BufferedWriter} object for the given file.
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static BufferedWriter getBufferedFileWriter(File file, boolean append)
            throws UnsupportedEncodingException, IOException {
        FileOutputStream output = new FileOutputStream(file, append);
        if (file.getName().endsWith(".gz"))
            return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(output), "UTF-8"));
        else
            return new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
    }

    /**
     * Opens a file handle to a new file. Handles gzip-compression depending on the file name extension
     * 
     * @param file
     *            the file to write
     * @return the {@link BufferedWriter} object for the given file.
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static BufferedWriter getBufferedFileWriter(File file) throws UnsupportedEncodingException, IOException {
        return getBufferedFileWriter(file, false);
    }

}
