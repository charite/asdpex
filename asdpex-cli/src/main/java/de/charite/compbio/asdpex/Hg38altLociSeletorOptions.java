/**
 * 
 */
package de.charite.compbio.asdpex;

import java.io.File;

/**
 * Configuration for the Hg38altLociSeletor program.
 * 
 * Most of the parameters are only used by one or several but not all commands.
 * 
 * TODO init all pathes with: System.out.println("Working Directory = " + System.getProperty("user.dir"));
 * 
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public final class Hg38altLociSeletorOptions {

    public final static String VERSION = "0.2";

    public static final String VCFALTLOCISTRING = "ALTLOCI";

    public static final String VCFALTLOCIGENOTYPE = "ALTGENOTYPE";

    public static final String VCFASDP = "ASDP";

    /** directory to use for the downloads and the serialized file */
    private String dataPath = "";

    private String alignmentPath = "alignments";

    private String resultsFolder = "results";

    private String fastaOutputPath = "../fasta";

    private String seedInfoPath = "../seed";

    private String seqanAlign = "regionalign2vcf";

    private String tempFolder = "tmp";

    public Command command;
    public String error;

    private String chrAccessionsPath = "chr_accessions_GRCh38.p2";
    private String altAccessionsPath = "alts_accessions_GRCh38.p2";
    private String altScaffoldPlacementPath = "all_alt_scaffold_placement.txt";
    private String referencePath = "genome" + File.separator + "GRCh38.fa";
    private String genomicRegionsDefinitionsPath = "genomic_regions_definitions.txt";

    // +++++++++++++ boolean parameters given by program call +++++++++++++++++++//

    /** return alternate Loci fastA as single file or in separate files (default) */
    private boolean singleAltLociFile = false;

    /** split alignments at large indels = possible inversions etc. */
    private boolean alignmentSplitIndels = true;

    /** split alignments at stretches of multiple 'N's */
    private boolean alignmentSplitNs = true;

    // ++++++++++++++ predefined numbers used as default values +++++++++++++++++++//

    /** Line length of output fasta files. */
    private int fastaLineLength = 70;

    // +++++++++++++ file paths used by the program +++++++++++++++++++//

    /** path to the input VCF file for the annotation */
    private String inputVcf;
    /** path to the alt-loci VCF file(s) */
    private String altlociVcf;
    /** path to the annotated output VCF file */
    private String outputVcf;
    /** path to the SQLite file storing programs data */
    private String sqlitePath;

    /**
     * The command that is to be executed.
     */
    public enum Command {
        DOWNLOAD, ANNOTATE_VCF, CREATE_FASTA, CREATE_SEED, ALIGN, CREATE_DB;

        public String toString() {
            switch (this) {
            case DOWNLOAD:
                return "download";
            case ANNOTATE_VCF:
                return "annotate";
            case CREATE_FASTA:
                return "create-fa";
            case CREATE_SEED:
                return "create-seed";
            case ALIGN:
                return "align";
            case CREATE_DB:
                return "create-db";
            default:
                return "unknown";
            }
        }
    }

    /**
     * @return the dataPath
     */
    public String getDataPath() {
        return dataPath;
    }

    /**
     * @param dataPath
     *            the dataPath to set
     */
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    /**
     * @return the alignmentPath
     */
    public String getAlignmentPath() {
        return dataPath + File.separator + alignmentPath;
    }

    /**
     * @param alignmentPath
     *            the alignmentPath to set
     */
    public void setAlignmentPath(String alignmentPath) {
        this.alignmentPath = alignmentPath;
    }

    /**
     * @return the resultsFolder
     */
    public String getResultsFolder() {
        return resultsFolder;
    }

    /**
     * @param resultsFolder
     *            the resultsFolder to set
     */
    public void setResultsFolder(String resultsFolder) {
        this.resultsFolder = resultsFolder;
    }

    /**
     * @return the fastaOutputPath
     */
    public String getFastaOutputPath() {
        return fastaOutputPath;
    }

    /**
     * @param fastaOutputPath
     *            the fastaOutputPath to set
     */
    public void setFastaOutputPath(String fastaOutputPath) {
        this.fastaOutputPath = fastaOutputPath;
    }

    /**
     * @return the seedInfoPath
     */
    public String getSeedInfoPath() {
        return seedInfoPath;
    }

    /**
     * @param seedInfoPath
     *            the seedInfoPath to set
     */
    public void setSeedInfoPath(String seedInfoPath) {
        this.seedInfoPath = seedInfoPath;
    }

    /**
     * @return the seqanALign
     */
    public String getSeqanAlign() {
        return seqanAlign;
    }

    /**
     * @param seqanALign
     *            the seqanALign to set
     */
    public void setSeqanAlign(String seqanAlign) {
        this.seqanAlign = seqanAlign;
    }

    /**
     * @return the tempFolder
     */
    public String getTempFolder() {
        return dataPath + File.separator + tempFolder;
    }

    // /**
    // * @param tempFolder
    // * the tempFolder to set
    // */
    // public void setTempFolder(String tempFolder) {
    // this.tempFolder = tempFolder;
    // }

    /**
     * @return the chrAccessionsPath
     */
    public String getChrAccessionsPath() {
        return dataPath + File.separator + chrAccessionsPath;
    }

    // /**
    // * @param chrAccessionsPath
    // * the chrAccessionsPath to set
    // */
    // public void setChrAccessionsPath(String chrAccessionsPath) {
    // this.chrAccessionsPath = chrAccessionsPath;
    // }

    /**
     * @return the altAccessionsPath
     */
    public String getAltAccessionsPath() {
        return dataPath + File.separator + altAccessionsPath;
    }

    // /**
    // * @param altAccessionsPath
    // * the altAccessionsPath to set
    // */
    // public void setAltAccessionsPath(String altAccessionsPath) {
    // this.altAccessionsPath = altAccessionsPath;
    // }

    /**
     * @return the altScaffoldPlacementPath
     */
    public String getAltScaffoldPlacementPath() {
        return dataPath + File.separator + altScaffoldPlacementPath;
    }

    // /**
    // * @param altScaffoldPlacementPath
    // * the altScaffoldPlacementPath to set
    // */
    // public void setAltScaffoldPlacementPath(String altScaffoldPlacementPath) {
    // this.altScaffoldPlacementPath = altScaffoldPlacementPath;
    // }

    /**
     * @return the referencePath
     */
    public String getReferencePath() {
        return dataPath + File.separator + referencePath;
    }

    // /**
    // * @param referencePath
    // * the referencePath to set
    // */
    // public void setReferencePath(String referencePath) {
    // this.referencePath = referencePath;
    // }

    /**
     * @return the genomicRegionsDefinitionsPath
     */
    public String getGenomicRegionsDefinitionsPath() {
        return dataPath + File.separator + genomicRegionsDefinitionsPath;
    }

    // /**
    // * @param genomicRegionsDefinitionsPath
    // * the genomicRegionsDefinitionsPath to set
    // */
    // public void setGenomicRegionsDefinitionsPath(String genomicRegionsDefinitionsPath) {
    // this.genomicRegionsDefinitionsPath = genomicRegionsDefinitionsPath;
    // }

    /**
     * Sets the path to the SQLite database file.
     * 
     * @param sqlitePath
     *            path to the SQLite file
     */
    public void setSqlitePath(String sqlitePath) {
        this.sqlitePath = sqlitePath;
    }

    /**
     * Returns the path to the SQLite database file.
     * 
     * @return path to SQLite file
     */
    public String getSqlitePath() {
        return (this.sqlitePath);
    }

    /**
     * @return the singleAltLociFile
     */
    public boolean isSingleAltLociFile() {
        return singleAltLociFile;
    }

    /**
     * @param singleAltLociFile
     *            the singleAltLociFile to set
     */
    public void setSingleAltLociFile(boolean singleAltLociFile) {
        this.singleAltLociFile = singleAltLociFile;
    }

    /**
     * @return the inputVcf
     */
    public String getInputVcf() {
        return inputVcf;
    }

    /**
     * @param inputVcf
     *            the inputVcf to set
     */
    public void setInputVcf(String inputVcf) {
        this.inputVcf = inputVcf;
    }

    /**
     * @return the altlociVcf
     */
    public String getAltlociVcf() {
        return altlociVcf;
    }

    /**
     * @param altlociVcf
     *            the altlociVcf to set
     */
    public void setAltlociVcf(String altlociVcf) {
        this.altlociVcf = altlociVcf;
    }

    /**
     * @return the outputVcf
     */
    public String getOutputVcf() {
        return outputVcf;
    }

    /**
     * @param outputVcf
     *            the outputVcf to set
     */
    public void setOutputVcf(String outputVcf) {
        this.outputVcf = outputVcf;
    }

    /**
     * @return the alignmentSplitIndels
     */
    public boolean isAlignmentSplitIndels() {
        return alignmentSplitIndels;
    }

    /**
     * Set if alignments should be split at large InDels.
     * 
     * @param alignmentSplitIndels
     *            <code>true</code>=split, <code>false</code>=keep
     */
    public void setAlignmentSplitIndels(boolean alignmentSplitIndels) {
        this.alignmentSplitIndels = alignmentSplitIndels;
    }

    /**
     * 
     * @return the alignmentSplitNs
     */
    public boolean isAlignmentSplitNs() {
        return alignmentSplitNs;
    }

    /**
     * Set if alignments should be split at stretches of multiple 'N's.
     * 
     * @param alignmentSplitNs
     *            <code>true</code>=split, <code>false</code>=keep
     */
    public void setAlignmentSplitNs(boolean alignmentSplitNs) {
        this.alignmentSplitNs = alignmentSplitNs;
    }

    /**
     * @return the fastaLineLength
     */
    public int getFastaLineLength() {
        return fastaLineLength;
    }

    /**
     * @param fastaLineLength
     *            the fastaLineLength to set
     */
    public void setFastaLineLength(int fastaLineLength) {
        this.fastaLineLength = fastaLineLength;
    }

}
