/**
 * 
 */
package de.charite.compbio.asdpex.data;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * A Region is a specific area on the reference genome for which at least one
 * alternative locus is known.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class Region implements Serializable, Comparable<Region> {
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
     * Return a {@link Map} with loci. The keys are the 'alt_scaf_acc' from NCBI
     * alt_scaffold_placement file.
     * 
     * @return the loci
     */
    public ImmutableMap<String, MetaLocus> getLoci() {
        return loci;
    }

    @Override
    public int compareTo(Region o) {
        // check chromosome lexical ordering
        if (this.regionInfo.getChromosomeInfo().compareTo(o.getRegionInfo().getChromosomeInfo()) < 0)
            return -1;
        else {
            if (this.regionInfo.getChromosomeInfo().compareTo(o.regionInfo.getChromosomeInfo()) > 0)
                return 1;
            else { // check start smaller
                if (this.regionInfo.getStart() < o.regionInfo.getStart())
                    return -1;
                else {
                    if (this.regionInfo.getStart() > o.regionInfo.getStart())
                        return 1;
                    else { // check Stop smaller if same start
                        if (this.regionInfo.getStop() < o.regionInfo.getStop())
                            return -1;
                        else {
                            if (this.regionInfo.getStop() > o.regionInfo.getStop())
                                return 1;
                            else
                                return 0;
                        }

                    }
                }
            }
        }
    }

}
