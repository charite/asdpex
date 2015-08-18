/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

import java.io.Serializable;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AccessionInfo implements Serializable {

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
	 * @return the chromosome
	 */
	public String getChromosome() {
		return chromosome;
	}

	/**
	 * @return the refseqAccessionVersion
	 */
	public String getRefseqAccessionVersion() {
		return refseqAccessionVersion;
	}

	/**
	 * @return the refseqGi
	 */
	public int getRefseqGi() {
		return refseqGi;
	}

	/**
	 * @return the genbankAccessionVersion
	 */
	public String getGenbankAccessionVersion() {
		return genbankAccessionVersion;
	}

	/**
	 * @return the genbankGi
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
	 * Nester builder for the {@link AccessionInfo}s.
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

		/**
		 * @param chr
		 *            the chromosome
		 * @return
		 */
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

}
