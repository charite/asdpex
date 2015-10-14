/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

import java.io.Serializable;

/**
 * Class to store all informations for an alternative loci.
 * 
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AlternativeLocus implements Serializable {
	/**
	 * default serial
	 */
	private static final long serialVersionUID = 1L;
	private final AccessionInfo accessionInfo;
	private final RegionInfo regionInfo;
	private final AltScaffoldPlacementInfo placementInfo; 
	
	protected AlternativeLocus(AccessionInfo accessionInfo, RegionInfo regionInfo, AltScaffoldPlacementInfo placementInfo){
		this.accessionInfo = accessionInfo;
		this.regionInfo = regionInfo;
		this.placementInfo = placementInfo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public AccessionInfo getAccessionInfo() {
		return accessionInfo;
	}

	public RegionInfo getRegionInfo() {
		return regionInfo;
	}

	public AltScaffoldPlacementInfo getPlacementInfo() {
		return placementInfo;
	}
}
