/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

import java.io.Serializable;

/**
 * Single alignment match class, storing the start for ref and alt and the length of the matching region
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class NCBIgffAlignmentMatch implements Serializable {
	/*
	 * default serial ID
	 */
	private static final long serialVersionUID = 1L;

	/* Start reference '0'-based */
	private final int ref_start;
	/* Start alt loci '0'-based */
	private final int alt_start;
	/* Length of the match */
	private final int length;

	/**
	 * default serial ID
	 * 
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Start reference '0'-based
	 * 
	 * @return the ref_start
	 */
	public int getRef_start() {
		return ref_start;
	}

	/**
	 * Start alt loci '0'-based
	 * 
	 * @return the alt_start
	 */
	public int getAlt_start() {
		return alt_start;
	}

	/**
	 * Length of the match
	 * 
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	private NCBIgffAlignmentMatch(NCBIgffAlignmentMatchBuilder builder) {
		this.ref_start = builder.ref_start;
		this.alt_start = builder.alt_start;
		this.length = builder.length;
	}

	/**
	 * Nested builder for the {@link NCBIgffAlignmentMatch}s.
	 * 
	 *
	 * @author Marten Jäger <marten.jaeger@charite.de>
	 *
	 */
	public static class NCBIgffAlignmentMatchBuilder {
		private int ref_start;
		private int alt_start;
		private int length;

		public NCBIgffAlignmentMatchBuilder refStart(int value) {
			this.ref_start = value;
			return this;
		}

		public NCBIgffAlignmentMatchBuilder altStart(int value) {
			this.alt_start = value;
			return this;
		}

		public NCBIgffAlignmentMatchBuilder length(int value) {
			this.length = value;
			return this;
		}

		public NCBIgffAlignmentMatch build() {
			return new NCBIgffAlignmentMatch(this);
		}
	}
}
