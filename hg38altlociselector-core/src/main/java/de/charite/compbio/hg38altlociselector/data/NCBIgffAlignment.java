/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

import com.google.common.collect.ImmutableList;

/**
 * slim Representation of the NCBI alignment. 
 * @author mjaeger
 *
 */
public class NCBIgffAlignment {
	private final String refId;
	private final String altId;
	private final int refStart;
	private final int refStop;
	private final int altStart;
	private final int altStop;
	private final boolean altStrand;
	private final ImmutableList<NCBIgffAlignmentElement> elements;
	
	public NCBIgffAlignment(String refId,String altId,int refStart,int refStop,int altStart,int altStop, boolean altStrand,ImmutableList<NCBIgffAlignmentElement> elements){
		this.refId = refId;
		this.altId = altId;
		this.refStart = refStart;
		this.refStop = refStop;
		this.altStart = altStart;
		this.altStop = altStop;
		this.altStrand = altStrand;
		this.elements = elements;
	}

	public String getRefId() {
		return refId;
	}

	public String getAltId() {
		return altId;
	}

	public int getRefStart() {
		return refStart;
	}

	public int getRefStop() {
		return refStop;
	}

	public int getAltStart() {
		return altStart;
	}

	public int getAltStop() {
		return altStop;
	}

	public boolean isAltStrand() {
		return altStrand;
	}

	public ImmutableList<NCBIgffAlignmentElement> getElements() {
		return elements;
	}
	

}
