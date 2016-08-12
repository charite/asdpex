/**
 * 
 */
package de.charite.compbio.asdpex.util;

/**
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class HelpFormatter {

    public enum Failure {
        MISSING_VCF, MISSING_DATA_PATH, MISSING_PATH
    }

    public static final String VERSION = "0.1";

    public static final void printHeader() {
        System.err.println("Program: de.charite.compbio.asdpex (functional annotation of VCF files)");
        System.err.print("Version: ");
        System.err.println(VERSION);
        System.err.println("Contact: Marten Jäger <marten.jaeger@charite.de>");
        System.err.println("         Peter N. Robinson <peter.robinson@jax.org>");
        System.err.println("");
        System.err.println("Usage: java -jar asdpex.jar <command> [options]\n");
    }

    public static void printUsage(String cmd, String msg, String error) {
        if (cmd != null)
            System.err.println("Usage:  java -jar asdpex.jar " + cmd + " [options]");
        System.err.println(msg);

        if (error != null)
            System.err.println("[ERROR] " + error);
    }

    public static void printUsage(String cmd, String msg) {
        printUsage(null, msg, null);
    }

    public static void printUsage(String msg) {
        printUsage(null, msg);
    }

}
