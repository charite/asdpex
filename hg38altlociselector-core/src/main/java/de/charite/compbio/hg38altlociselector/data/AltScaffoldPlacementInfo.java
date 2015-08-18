/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

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

	}
}
