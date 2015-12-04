/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.hg38altlociselector.io.parser.AccessionInfoParser;
import de.charite.compbio.hg38altlociselector.io.parser.AltScaffoldPlacementParser;
import de.charite.compbio.hg38altlociselector.io.parser.RegionInfoParser;

/**
 * Builds the {@link Region}s.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class RegionBuilder {
	private final String altAccessionsPath;
	private final String altScaffoldPlacementPath;
	private final String genomicRegionsDefinitionsPath;

	/**
	 * 
	 * @param altAccessionsPath
	 * @param altScaffoldPlacementPath
	 * @param genomicRegionsDefinitionsPath
	 */
	public RegionBuilder(String altAccessionsPath, String altScaffoldPlacementPath,
			String genomicRegionsDefinitionsPath) {
		this.altAccessionsPath = altAccessionsPath;
		this.altScaffoldPlacementPath = altScaffoldPlacementPath;
		this.genomicRegionsDefinitionsPath = genomicRegionsDefinitionsPath;
	}

	public ImmutableList<Region> build() {
		ImmutableList.Builder<Region> builder = new ImmutableList.Builder<Region>();
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
		RegionInfoParser regParser = new RegionInfoParser(genomicRegionsDefinitionsPath);
		ImmutableMap<String, RegionInfo> regMap = regParser.parse();
		System.out.println("found " + regMap.size() + " regions definitions");

		System.out.println("[INFO] Process the regions:");
		System.out.println("0%       50%       100%");
		System.out.println("|.........|.........|");
		int c = 1;
		int limit = 0;
		for (RegionInfo region : regMap.values()) {
			if (100.0 * c++ / regMap.values().size() > limit) {
				limit += 5;

				System.out.print("*");
			}
			ImmutableMap<String, MetaLocus> loci = extractLoci(region, asMap, aiMap);
			builder.add(new Region(region, loci));
		}
		System.out.println("*");
		return builder.build();
	}

	private ImmutableMap<String, MetaLocus> extractLoci(RegionInfo region,
			ImmutableMap<String, AltScaffoldPlacementInfo> asMap, ImmutableMap<String, AccessionInfo> aiMap) {
		ImmutableMap.Builder<String, MetaLocus> builder = new ImmutableMap.Builder<>();
		int c = 0;
		for (AltScaffoldPlacementInfo scaf : asMap.values()) {
			if (scaf.getRegion().equals(region.getRegionName())) {
				AccessionInfo info = aiMap.get(scaf.getAltScafAcc());
				if (scaf != null && info != null) {
					builder.put(info.createFastaIdentifier(), new MetaLocus(scaf, info, null));
					c++;
				}
			}
		}
		return c > 0 ? builder.build() : null;
		// return builder.build();
	}
}
