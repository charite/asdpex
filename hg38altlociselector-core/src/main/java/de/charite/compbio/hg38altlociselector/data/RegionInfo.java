/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

import java.io.Serializable;


/**
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class RegionInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String regionName;
	private final String chromosome;
	private final int start;
	private final int stop;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRegionName() {
		return regionName;
	}

	public String getChromosome() {
		return chromosome;
	}

	public int getStart() {
		return start;
	}

	public int getStop() {
		return stop;
	}

	private RegionInfo(RegionInfoBuilder builder){
		this.regionName = builder.regionName;
		this.chromosome = builder.chromosome;
		this.start = builder.start;
		this.stop = builder.stop;
	}
	
	/**
	 * Nester builder for the {@link RegionInfo}s.
	 * 
	 *
	 * @author Marten Jäger <marten.jaeger@charite.de>
	 *
	 */
	public static class RegionInfoBuilder{

		private int stop;
		private int start;
		private String chromosome;
		private String regionName;
		
		public RegionInfoBuilder chromosome(String chr) {
			this.chromosome = chr;
			return this;
		}
		public RegionInfoBuilder regionName(String name) {
			this.regionName = name;
			return this;
		}
		public RegionInfoBuilder start(int value) {
			this.start = value;
			return this;
		}
		public RegionInfoBuilder stop(int value) {
			this.stop = value;
			return this;
		}
		public RegionInfo build() {
			return new RegionInfo(this);
		}		
	}

}
