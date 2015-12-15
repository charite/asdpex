/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

import java.io.Serializable;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Object storing the result output after calculating the Intersect of two {@link VariantContext} Lists.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class PairwiseVariantContextIntersect implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private final CloseableIterator<VariantContext> set1;
	private final int set1SNVs;
	private final int set1SVs;
	// private final int onlySet1SNVs;

	// private final CloseableIterator<VariantContext> set2;
	private final int set2SNVs;
	private final int set2SVs;
	// private final int onlySet2SNVs;

	private final int intersectSNVs;
	private final int intersectSVs;

	private PairwiseVariantContextIntersect(PairwiseVariantContextIntersectBuilder builder) {
		// this.set1 = builder.set1;
		this.set1SNVs = builder.set1SNVs;
		this.set1SVs = builder.set1SVs;
		// this.onlySet1SNVs = builder.onlySet1SNVs;

		// this.set2 = builder.set2;
		this.set2SNVs = builder.set2SNVs;
		this.set2SVs = builder.set2SVs;
		// this.onlySet2SNVs = builder.onlySet2SNVs;

		this.intersectSNVs = builder.intersectSNVs;
		this.intersectSVs = builder.intersectSVs;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SET1:\n SNV: ").append(this.set1SNVs).append("\n SV: ").append(this.set1SVs).append("\n");
		sb.append("SET2:\n SNV: ").append(this.set2SNVs).append("\n SV: ").append(this.set2SVs).append("\n");
		sb.append("INTERSECT:\n SNV: ").append(this.intersectSNVs).append("\n SV: ").append(this.intersectSVs)
				.append("\n");

		return sb.toString();
	}

	// /**
	// * @return the set1
	// */
	// public CloseableIterator<VariantContext> getSet1() {
	// return set1;
	// }

	/**
	 * @return the set1SNVs
	 */
	public int getSet1SNVs() {
		return set1SNVs;
	}

	/**
	 * @return the set1SVs
	 */
	public int getSet1SVs() {
		return set1SVs;
	}

	/**
	 * @return the onlySet1SNVs
	 */
	public int getOnlySet1SNVs() {
		return set1SNVs - intersectSNVs;
	}

	// /**
	// * @return the set2
	// */
	// public CloseableIterator<VariantContext> getSet2() {
	// return set2;
	// }

	/**
	 * @return the set2SNVs
	 */
	public int getSet2SNVs() {
		return set2SNVs;
	}

	/**
	 * @return the set2SVs
	 */
	public int getSet2SVs() {
		return set2SVs;
	}

	/**
	 * @return the onlySet2SNVs
	 */
	public int getOnlySet2SNVs() {
		return set2SNVs - intersectSNVs;
	}

	/**
	 * @return the intersectSNVs
	 */
	public int getIntersectSNVs() {
		return intersectSNVs;
	}

	/**
	 * @return the intersectSVs
	 */
	public int getIntersectSVs() {
		return intersectSVs;
	}

	public static class PairwiseVariantContextIntersectBuilder {
		// private CloseableIterator<VariantContext> set1;
		private int set1SNVs;
		private int set1SVs;
		private int onlySet1SNVs;

		// private CloseableIterator<VariantContext> set2;
		private int set2SNVs;
		private int set2SVs;
		private int onlySet2SNVs;

		private int intersectSNVs;
		private int intersectSVs;

		// public PairwiseVariantContextIntersectBuilder set1(CloseableIterator<VariantContext> set) {
		// this.set1 = set;
		// return this;
		// }
		//
		// public PairwiseVariantContextIntersectBuilder set2(CloseableIterator<VariantContext> set) {
		// this.set2 = set;
		// return this;
		// }

		public PairwiseVariantContextIntersectBuilder set1SNVs(int value) {
			this.set1SNVs = value;
			return this;
		}

		public PairwiseVariantContextIntersectBuilder set1SVs(int value) {
			this.set1SVs = value;
			return this;
		}

		public PairwiseVariantContextIntersectBuilder onlySet1SNVs(int value) {
			this.onlySet1SNVs = value;
			return this;
		}

		public PairwiseVariantContextIntersectBuilder set2SNVs(int value) {
			this.set2SNVs = value;
			return this;
		}

		public PairwiseVariantContextIntersectBuilder set2SVs(int value) {
			this.set2SVs = value;
			return this;
		}

		public PairwiseVariantContextIntersectBuilder onlySet2SNVs(int value) {
			this.onlySet2SNVs = value;
			return this;
		}

		public PairwiseVariantContextIntersectBuilder intersectSNVs(int value) {
			this.intersectSNVs = value;
			return this;
		}

		public PairwiseVariantContextIntersectBuilder intersectSVs(int value) {
			this.intersectSVs = value;
			return this;
		}

		public PairwiseVariantContextIntersect build() {
			return new PairwiseVariantContextIntersect(this);
		}
	}
}
