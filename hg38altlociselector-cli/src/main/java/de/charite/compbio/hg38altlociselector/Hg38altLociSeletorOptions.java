/**
 * 
 */
package de.charite.compbio.hg38altlociselector;

import java.io.File;

/**
 * Configuration for the Hg38altLociSeletor program.
 * 
 * Most of the parameters are only used by one or several but not all commands.
 * 
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public final class Hg38altLociSeletorOptions {

    public final static String VERSION = "0.0.1";

    public static final String VCFALTLOCISTRING = "ALTLOCI";

    public static final String VCFALTLOCIGENOTYPE = "ALTGENOTYPE";

    /** directory to use for the downloads and the serialized file */
    private String dataPath = "data";

    private String alignmentPath = dataPath + File.separator + "alignments";

    private String resultsFolder = "../results";

    private String fastqOutputPath = "../fasta";

    private String seedInfoPath = "../seed";

    private String seqanALign = "../seqan/regionalign2bed";

    private String tempFolder = dataPath + File.separator + "tmp";

    public Command command;
    public String error;

    private String chrAccessionsPath = dataPath + "/chr_accessions_GRCh38.p2";
    private String altAccessionsPath = dataPath + "/alts_accessions_GRCh38.p2";
    private String altScaffoldPlacementPath = dataPath + "/all_alt_scaffold_placement.txt";
    private String referencePath = dataPath + "/genome/GRCh38.fa";
    private String genomicRegionsDefinitionsPath = dataPath + "/genomic_regions_definitions.txt";

    public boolean singleAltLociFile = false;

    /**
     * Line length of output fasta files.
     */
    public int fastaLineLength = 70;

    /** path to the input VCF file for the annotation */
    public String inputVcf;
    /** path to the alt-loci VCF file(s) */
    public String altlociVcf;
    /** path to the annotated output VCF file */
    public String outputVcf;

    /**
     * The command that is to be executed.
     */
    public enum Command {
        DOWNLOAD, ANNOTATE_VCF, CREATE_FASTA, CREATE_SEED, ALIGN;

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
        return alignmentPath;
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
     * @return the fastqOutputPath
     */
    public String getFastqOutputPath() {
        return fastqOutputPath;
    }

    /**
     * @param fastqOutputPath
     *            the fastqOutputPath to set
     */
    public void setFastqOutputPath(String fastqOutputPath) {
        this.fastqOutputPath = fastqOutputPath;
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
    public String getSeqanALign() {
        return seqanALign;
    }

    /**
     * @param seqanALign
     *            the seqanALign to set
     */
    public void setSeqanALign(String seqanALign) {
        this.seqanALign = seqanALign;
    }

    /**
     * @return the tempFolder
     */
    public String getTempFolder() {
        return tempFolder;
    }

    /**
     * @param tempFolder
     *            the tempFolder to set
     */
    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }

    /**
     * @return the chrAccessionsPath
     */
    public String getChrAccessionsPath() {
        return chrAccessionsPath;
    }

    /**
     * @param chrAccessionsPath
     *            the chrAccessionsPath to set
     */
    public void setChrAccessionsPath(String chrAccessionsPath) {
        this.chrAccessionsPath = chrAccessionsPath;
    }

    /**
     * @return the altAccessionsPath
     */
    public String getAltAccessionsPath() {
        return altAccessionsPath;
    }

    /**
     * @param altAccessionsPath
     *            the altAccessionsPath to set
     */
    public void setAltAccessionsPath(String altAccessionsPath) {
        this.altAccessionsPath = altAccessionsPath;
    }

    /**
     * @return the altScaffoldPlacementPath
     */
    public String getAltScaffoldPlacementPath() {
        return altScaffoldPlacementPath;
    }

    /**
     * @param altScaffoldPlacementPath
     *            the altScaffoldPlacementPath to set
     */
    public void setAltScaffoldPlacementPath(String altScaffoldPlacementPath) {
        this.altScaffoldPlacementPath = altScaffoldPlacementPath;
    }

    /**
     * @return the referencePath
     */
    public String getReferencePath() {
        return referencePath;
    }

    /**
     * @param referencePath
     *            the referencePath to set
     */
    public void setReferencePath(String referencePath) {
        this.referencePath = referencePath;
    }

    /**
     * @return the genomicRegionsDefinitionsPath
     */
    public String getGenomicRegionsDefinitionsPath() {
        return genomicRegionsDefinitionsPath;
    }

    /**
     * @param genomicRegionsDefinitionsPath
     *            the genomicRegionsDefinitionsPath to set
     */
    public void setGenomicRegionsDefinitionsPath(String genomicRegionsDefinitionsPath) {
        this.genomicRegionsDefinitionsPath = genomicRegionsDefinitionsPath;
    }

}
