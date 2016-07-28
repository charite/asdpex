/**
 * 
 */
package de.charite.compbio.asdpex.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.asdpex.io.parser.AccessionInfoParser;
import de.charite.compbio.asdpex.io.parser.AltScaffoldPlacementParser;
import de.charite.compbio.asdpex.io.parser.RegionInfoParser;

/**
 * Builder for the alternative loci in the providet genome / dataset.
 * 
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AlternativeLociBuilder {
	private final String altAccessionsPath;
	private final String altScaffoldPlacementPath;
	private final String genomicRegionsDefinitionsPath;
	private final String chrAccessionsPath;

	/**
	 * 
	 * @param altAccessionsPath
	 * @param altScaffoldPlacementPath
	 * @param genomicRegionsDefinitionsPath
	 */
	public AlternativeLociBuilder(String altAccessionsPath, String altScaffoldPlacementPath,
			String genomicRegionsDefinitionsPath, String chrAccessionsPath) {
		this.altAccessionsPath = altAccessionsPath;
		this.altScaffoldPlacementPath = altScaffoldPlacementPath;
		this.genomicRegionsDefinitionsPath = genomicRegionsDefinitionsPath;
		this.chrAccessionsPath = chrAccessionsPath;
	}

	public ImmutableList<AlternativeLocus> build() {
		ImmutableList.Builder<AlternativeLocus> builder = new ImmutableList.Builder<AlternativeLocus>();
		System.out.println("[INFO] Read alt. loci data:");
		System.out.print("[INFO] Read alt_loci accessions ... ");
		AccessionInfoParser aiParser = new AccessionInfoParser(altAccessionsPath);
		ImmutableMap<String, AccessionInfo> aiMap = aiParser.parse();
		System.out.println("found " + aiMap.size() + " alt_loci");

		System.out.print("[INFO] Read alt_loci placement ... ");
		AltScaffoldPlacementParser asParser = new AltScaffoldPlacementParser(altScaffoldPlacementPath);
		ImmutableMap<String, AltScaffoldPlacementInfo> asMap = asParser.parse();
		System.out.println("found placement for " + asMap.size() + " alt_loci");

		System.out.print("[INFO] Read region definitions ... ");
		RegionInfoParser regParser = new RegionInfoParser(genomicRegionsDefinitionsPath, chrAccessionsPath);
		ImmutableMap<String, RegionInfo> regMap = regParser.parse();
		System.out.println("found " + regMap.size() + " regions definitions");

		System.out.println("[INFO] Process the alt. loci data:");
		System.out.println("0%       50%       100%");
		System.out.println("|.........|.........|");
		int c = 1;
		int limit = 0;
		for (AltScaffoldPlacementInfo scaffold : asMap.values()) {
			if (100.0 * c++ / asMap.values().size() > limit) {
				limit += 5;
				System.out.print("*");
			}
			AccessionInfo currentAI = aiMap.get(scaffold.getAltScafAcc());
			RegionInfo currentReg = regMap.get(scaffold.getRegion());
			if (scaffold != null && currentAI != null && currentReg != null)
				builder.add(new AlternativeLocus(currentAI, currentReg, scaffold));
		}
		System.out.println("*");
		return builder.build();
	}

}
