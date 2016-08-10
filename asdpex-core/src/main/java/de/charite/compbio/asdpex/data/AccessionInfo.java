/**
 * 
 */
package de.charite.compbio.asdpex.data;

import java.io.Serializable;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AccessionInfo implements Serializable, Comparable<AccessionInfo> {

    /**
     * default serial
     */
    private static final long serialVersionUID = 1L;
    private final String chromosome;
    private final String refseqAccessionVersion;
    private final int refseqGi;
    private final String genbankAccessionVersion;
    private final int genbankGi;

    /**
     * Return the chromosome (e.g. 1,2,3,...X).
     * 
     * @return the chromosome Chromosome identifier
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * Return the RefSeq accession version (e.g. NC_000001.11).
     * 
     * @return the refseqAccessionVersion RefSeq accession version
     */
    public String getRefseqAccessionVersion() {
        return refseqAccessionVersion;
    }

    /**
     * Return the RefSeq GI number (e.g. 568815597)
     * 
     * @return the refseqGi RefSeq GI number
     */
    public int getRefseqGi() {
        return refseqGi;
    }

    /**
     * Return the GenBank accession version (e.g. CM000663.2).
     * 
     * @return the genbankAccessionVersion GenBank accession version
     */
    public String getGenbankAccessionVersion() {
        return genbankAccessionVersion;
    }

    /**
     * Return the GenBank GI number (e.g. 568336023).
     * 
     * @return the genbankGi GenBank GI number
     */
    public int getGenbankGi() {
        return genbankGi;
    }

    private AccessionInfo(AccessionInfoBuilder builder) {
        this.chromosome = builder.chromosome;
        this.refseqAccessionVersion = builder.refseqAccessionVersion;
        this.refseqGi = builder.refseqGi;
        this.genbankAccessionVersion = builder.genbankAccessionVersion;
        this.genbankGi = builder.genbankGi;
    }

    /**
     * Creates the Fasta identifier from accessionInfo file row in the format they are used in the reference fasta
     * files: chr<1-22|X|Y|M>_<GenBank Accession.version with '.'->'v'>_alt<br>
     * e.g.: chr21_GL383580v2_alt
     * 
     * @param info
     * @return
     */
    public String createFastaIdentifier() {
        StringBuilder identifier = new StringBuilder();
        identifier.append("chr").append(this.getChromosome()).append("_")
                .append(this.getGenbankAccessionVersion().replace('.', 'v')).append("_alt");
        return identifier.toString();
    }

    /**
     * Nested builder for the {@link AccessionInfo}s.
     * 
     *
     * @author Marten Jäger <marten.jaeger@charite.de>
     *
     */
    public static class AccessionInfoBuilder {
        private String chromosome;
        private String refseqAccessionVersion;
        private int refseqGi;
        private String genbankAccessionVersion;
        private int genbankGi;

        public AccessionInfoBuilder chromosome(String chr) {
            this.chromosome = chr;
            return this;
        }

        public AccessionInfoBuilder refseqAccessionVersion(String version) {
            this.refseqAccessionVersion = version;
            return this;
        }

        public AccessionInfoBuilder refseqGi(int value) {
            this.refseqGi = value;
            return this;
        }

        public AccessionInfoBuilder genbankAccessionVersion(String version) {
            this.genbankAccessionVersion = version;
            return this;
        }

        public AccessionInfoBuilder genbankGi(int value) {
            this.genbankGi = value;
            return this;
        }

        public AccessionInfo build() {
            return new AccessionInfo(this);
        }

    }

    @Override
    public int compareTo(AccessionInfo o) {
        if (this.chromosome.matches("\\d+") && o.chromosome.matches("\\d+")) {
            return new Integer(this.chromosome) - new Integer(o.chromosome);
        }
        // else, compare normally.
        return this.chromosome.compareTo(o.chromosome);
    }

}
