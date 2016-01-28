/**
 * 
 */
package de.charite.compbio.hg38altlociselector.util;

/**
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class HelpFormatter {

    public enum Failure {
        MISSING_VCF
    }

    public static final String VERSION = "0.0.1";

    public static final void printHeader() {
        System.err.println("Program: de.charite.compbio.hg38altlociselector (functional annotation of VCF files)");
        System.err.print("Version: ");
        System.err.println(VERSION);
        System.err.println("Contact: Marten Jäger <marten.jaeger@charite.de>");
        System.err.println("");
        System.err.println("Usage: java -jar hg38altlociselector.jar <command> [options]\n");
    }

    public static void printUsage(String cmd, String msg, String error) {
        if (cmd != null)
            System.err.println("Usage:  java -jar hg38altlociselector.jar " + cmd + " [options]");
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
