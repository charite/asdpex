/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * A Region is a specific area on the reference genome for which at least one alternative locus is known.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class Region implements Serializable {
	private final RegionInfo regionInfo;
	private final ImmutableMap<String, MetaLocus> loci;

	public Region(RegionInfo regionInfo, ImmutableMap<String, MetaLocus> loci) {
		this.regionInfo = regionInfo;
		this.loci = loci;
	}

	/**
	 * Return the {@link RegionInfo}.
	 * 
	 * @return the regionInfo
	 */
	public RegionInfo getRegionInfo() {
		return regionInfo;
	}

	/**
	 * Return a {@link Map} with loci. The keys are the 'alt_scaf_acc' from NCBI alt_scaffold_placement file.
	 * 
	 * @return the loci
	 */
	public ImmutableMap<String, MetaLocus> getLoci() {
		return loci;
	}

}
