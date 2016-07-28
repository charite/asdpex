/**
 * 
 */
package de.charite.compbio.asdpex.data;

import java.io.Serializable;

/**
 * The alt_scaffold_placement.txt files contain the following infos: <br>
 * prim_asm_name <br>
 * alt_scaf_name <br>
 * alt_scaf_acc <br>
 * 
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AltScaffoldPlacementInfo implements Serializable {

	/**
	 * default serial
	 */
	private static final long serialVersionUID = 1L;
	private String altAssemblyName; // alt_asm_name
	private String primAssemblyName; // prim_asm_name
	private String altScafName; // alt scaffold name
	private String altScafAcc; // alt scaffold accession
	private String parentType; // parent_type
	private String parentName; // parent_name
	private String parentAcc; // parent_accn
	private String region; // region_name
	private boolean strand; // ori
	private int altScafStart; // alt_scaf_start 1-based
	private int altScafStop; // alt_scaf_stop 0-based
	private int parentStart; // parent_start 1-based
	private int parentStop; // parent_stop 0-based
	private int altStartTail; // alt_start_tail
	private int altStopTail; // alt_stop_tail

	/**
	 * @return the altAssemblyName
	 */
	public String getAltAssemblyName() {
		return altAssemblyName;
	}

	/**
	 * @param altAssemblyName
	 *            the altAssemblyName to set
	 */
	public void setAltAssemblyName(String altAssemblyName) {
		this.altAssemblyName = altAssemblyName;
	}

	/**
	 * @return the primAssemblyName
	 */
	public String getPrimAssemblyName() {
		return primAssemblyName;
	}

	/**
	 * @param primAssemblyName
	 *            the primAssemblyName to set
	 */
	public void setPrimAssemblyName(String primAssemblyName) {
		this.primAssemblyName = primAssemblyName;
	}

	/**
	 * @return the altScafName
	 */
	public String getAltScafName() {
		return altScafName;
	}

	/**
	 * @param altScafName
	 *            the altScafName to set
	 */
	public void setAltScafName(String altScafName) {
		this.altScafName = altScafName;
	}

	/**
	 * @return the altScafAcc
	 */
	public String getAltScafAcc() {
		return altScafAcc;
	}

	/**
	 * @param altScafAcc
	 *            the altScafAcc to set
	 */
	public void setAltScafAcc(String altScafAcc) {
		this.altScafAcc = altScafAcc;
	}

	/**
	 * @return the parentType
	 */
	public String getParentType() {
		return parentType;
	}

	/**
	 * @param parentType
	 *            the parentType to set
	 */
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	/**
	 * @return the parentName
	 */
	public String getParentName() {
		return parentName;
	}

	/**
	 * @param parentName
	 *            the parentName to set
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	/**
	 * @return the parentAcc
	 */
	public String getParentAcc() {
		return parentAcc;
	}

	/**
	 * @param parentAcc
	 *            the parentAcc to set
	 */
	public void setParentAcc(String parentAcc) {
		this.parentAcc = parentAcc;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return the strand
	 */
	public boolean isStrand() {
		return strand;
	}

	/**
	 * @param strand
	 *            the strand to set
	 */
	public void setStrand(boolean strand) {
		this.strand = strand;
	}

	/**
	 * @return the altScafStart
	 */
	public int getAltScafStart() {
		return altScafStart;
	}

	/**
	 * @param altScafStart
	 *            the altScafStart to set
	 */
	public void setAltScafStart(int altScafStart) {
		this.altScafStart = altScafStart;
	}

	/**
	 * @return the altScafStop
	 */
	public int getAltScafStop() {
		return altScafStop;
	}

	/**
	 * @param altScafStop
	 *            the altScafStop to set
	 */
	public void setAltScafStop(int altScafStop) {
		this.altScafStop = altScafStop;
	}

	/**
	 * @return the parentStart
	 */
	public int getParentStart() {
		return parentStart;
	}

	/**
	 * @param parentStart
	 *            the parentStart to set
	 */
	public void setParentStart(int parentStart) {
		this.parentStart = parentStart;
	}

	/**
	 * @return the parentStop
	 */
	public int getParentStop() {
		return parentStop;
	}

	/**
	 * @param parentStop
	 *            the parentStop to set
	 */
	public void setParentStop(int parentStop) {
		this.parentStop = parentStop;
	}

	/**
	 * @return the altStartTail
	 */
	public int getAltStartTail() {
		return altStartTail;
	}

	/**
	 * @param altStartTail
	 *            the altStartTail to set
	 */
	public void setAltStartTail(int altStartTail) {
		this.altStartTail = altStartTail;
	}

	/**
	 * @return the altStopTail
	 */
	public int getAltStopTail() {
		return altStopTail;
	}

	/**
	 * @param altStopTail
	 *            the altStopTail to set
	 */
	public void setAltStopTail(int altStopTail) {
		this.altStopTail = altStopTail;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String createGffIdentifier() {
		StringBuilder identifier = new StringBuilder();
		identifier.append(this.getAltScafAcc()).append("_").append(this.getParentAcc());
		return identifier.toString();
	}

	/**
	 * 
	 */
	private AltScaffoldPlacementInfo(AltScaffoldPlacementInfoBuilder builder) {
		this.altAssemblyName = builder.altAssemblyName;
		this.primAssemblyName = builder.primAssemblyName;
		this.altScafName = builder.altScafName;
		this.altScafAcc = builder.altScafAcc;
		this.parentType = builder.parentType;
		this.parentName = builder.parentName;
		this.parentAcc = builder.parentAcc;
		this.region = builder.region;
		this.strand = builder.strand;
		this.altScafStart = builder.altScafStart;
		this.altScafStop = builder.altScafStop;
		this.parentStart = builder.parentStart;
		this.parentStop = builder.parentStop;
		this.altStartTail = builder.altStartTail;
		this.altStopTail = builder.altStopTail;

	}

	public static class AltScaffoldPlacementInfoBuilder {
		private String altAssemblyName; // alt_asm_name
		private String primAssemblyName; // prim_asm_name
		private String altScafName; // alt scaffold name
		private String altScafAcc; // alt scaffold accession
		private String parentType; // parent_type
		private String parentName; // parent_name
		private String parentAcc; // parent_accn
		private String region; // region_name
		private boolean strand; // ori
		private int altScafStart; // alt_scaf_start 1-based
		private int altScafStop; // alt_scaf_stop 0-based
		private int parentStart; // parent_start 1-based
		private int parentStop; // parent_stop 0-based
		private int altStartTail; // alt_start_tail
		private int altStopTail; // alt_stop_tail

		public AltScaffoldPlacementInfoBuilder altAssemblyName(String str) {
			this.altAssemblyName = str;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder primAssemblyName(String str) {
			this.primAssemblyName = str;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder altScafName(String str) {
			this.altScafName = str;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder altScafAcc(String str) {
			this.altScafAcc = str;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder parentType(String str) {
			this.parentType = str;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder parentName(String str) {
			this.parentName = str;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder parentAcc(String str) {
			this.parentAcc = str;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder region(String str) {
			this.region = str;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder strand(boolean str) {
			this.strand = str;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder altScafStart(int val) {
			this.altScafStart = val;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder altScafStop(int val) {
			this.altScafStop = val;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder parentStart(int val) {
			this.parentStart = val;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder parentStop(int val) {
			this.parentStop = val;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder altStartTail(int val) {
			this.altStartTail = val;
			return this;
		}

		public AltScaffoldPlacementInfoBuilder altStopTail(int val) {
			this.altStopTail = val;
			return this;
		}

		public AltScaffoldPlacementInfo build() {
			return new AltScaffoldPlacementInfo(this);
		}

	}
}
