/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

import java.io.Serializable;

import com.google.common.collect.ImmutableList;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * This is a MetaLocus ;) A combination of the {@link AltScaffoldPlacementInfo}, the {@link AccessionInfo} and the
 * corresponding Variants in contrast to the reference allele.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class MetaLocus implements Serializable {
	/**
	 * default serial
	 */
	private static final long serialVersionUID = 1L;
	private final AltScaffoldPlacementInfo placementInfo;
	private final AccessionInfo accessionInfo;
	private final ImmutableList<VariantContext> variants;

	public MetaLocus(AltScaffoldPlacementInfo placementInfo, AccessionInfo accessionInfo,
			ImmutableList<VariantContext> variants) {
		this.placementInfo = placementInfo;
		this.accessionInfo = accessionInfo;
		this.variants = variants;
	}

	/**
	 * @return the placementInfo
	 */
	public AltScaffoldPlacementInfo getPlacementInfo() {
		return placementInfo;
	}

	/**
	 * 
	 * @return the corresponding {@link AccessionInfo} from the alt_accessions_GRCH38.p2 file
	 */
	public AccessionInfo getAccessionInfo() {
		return accessionInfo;
	}

	/**
	 * Returns a List of {@link VariantContext}s derived from the comparison to the reference allele.
	 * 
	 * @return the variants
	 */
	public ImmutableList<VariantContext> getVariants() {
		return variants;
	}
}
