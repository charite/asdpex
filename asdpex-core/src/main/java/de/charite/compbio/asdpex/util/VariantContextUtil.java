/**
 * 
 */
package de.charite.compbio.asdpex.util;

import java.util.ArrayList;
import java.util.Collections;

import de.charite.compbio.asdpex.data.PairwiseVariantContextIntersect;
import de.charite.compbio.asdpex.data.PairwiseVariantContextIntersect.PairwiseVariantContextIntersectBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContext.Type;

/**
 * Collection of tools to perform on {@link VariantContext} collections.
 * 
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class VariantContextUtil {

    /**
     * Compares the entries in set1 to these in set2 and generates a {@link PairwiseVariantContextIntersect} object with
     * the results. The sets have to be naturally sorted.
     * 
     * @param set1
     * @param set2
     * @return
     */
    public static PairwiseVariantContextIntersect intersectVariantContext(ArrayList<VariantContext> set1,
            ArrayList<VariantContext> set2) {
        if (set1 == null || set2 == null)
            return null;
        PairwiseVariantContextIntersectBuilder builder = new PairwiseVariantContextIntersectBuilder();
        // System.out.println("Compare Variants");
        // System.out.println("set1.size: " + set1.size());
        // System.out.println("set2.size: " + set2.size());

        ArrayList<VariantContext> set1SV = new ArrayList<>();
        ArrayList<VariantContext> set1SNV = new ArrayList<>();
        ArrayList<VariantContext> set2SV = new ArrayList<>();
        ArrayList<VariantContext> set2SNV = new ArrayList<>();
        ArrayList<VariantContext> intersectSNV = new ArrayList<>();
        boolean[] set1IntersectFlag = new boolean[set1.size()];

        // calc SVs
        for (VariantContext variantContext : set2) {
            if (variantContext.getType() == Type.SYMBOLIC)
                set2SV.add(variantContext);
            else
                set2SNV.add(variantContext);
        }
        int offset = 0;
        boolean intersect;
        int index = 0;
        for (VariantContext vc1 : set1) {
            if (vc1.getType() == Type.SYMBOLIC) {
                set1SV.add(vc1);
                continue;
            }
            set1SNV.add(vc1);
            intersect = false;
            VariantContext vc2;
            for (int i = offset; i < set2.size(); i++) {
                vc2 = set2.get(i);
                if (vc2.getType() == Type.SYMBOLIC)
                    continue;
                String contig1 = vc1.getContig().startsWith("chr") ? vc1.getContig() : "chr" + vc1.getContig();
                String contig2 = vc2.getContig().startsWith("chr") ? vc2.getContig() : "chr" + vc1.getContig();
                if (contig1.equals(contig2) && vc1.getStart() == vc2.getStart()
                        && vc1.hasAllele(vc2.getAlleles().get(0))) {
                    intersectSNV.add(vc1);
                    intersect = true;
                    set1IntersectFlag[index] = true;
                    offset = i;
                    break;
                }
                if (intersect)
                    break;

                // if (vc1.getStart() > vc2.getStart()) {
                // break;
                // }
            }
            index++;
        }
        builder.set1SNVs(set1SNV);
        builder.set1SVs(set1SV);
        builder.set2SNVs(set2SNV);
        builder.set2SVs(set2SV);
        builder.intersectSNVs(intersectSNV);
        builder.onlySet1SNVs(set1.size() - set1SV.size() - intersectSNV.size());
        builder.onlySet2SNVs(set2.size() - set2SV.size() - intersectSNV.size());
        builder.set1flagged(set1IntersectFlag);

        return builder.build();
    }

    /**
     * Iterate over the list of alternative Scaffolds and check if there are some with a high probability to be present.
     * 
     * @return
     */
    public static ArrayList<Integer> getMostProbableAlternativeScaffolds(
            ArrayList<PairwiseVariantContextIntersect> intersectList) {

        ArrayList<Integer> result = new ArrayList<>();
        // Integer scaffoldA = null;
        // Integer scaffoldB = null;

        // sort
        int count = 0;
        Collections.sort(intersectList);
        // only the first two alt scaffold to be looked up
        int myMax = intersectList.size() > 2 ? 2 : intersectList.size();
        PairwiseVariantContextIntersect currentElement;
        for (int i = 0; i < myMax; i++) {
            currentElement = intersectList.get(i);
            if (currentElement.getFinalSVNs() < currentElement.getSet1SNVs().size()) {
                // check if the intersect is mostly homozygous
                int homo = 0;
                for (VariantContext vc : currentElement.getIntersectSNVs()) {
                    // System.out.println(vc.getAlternateAlleles().size());
                    if (vc.getAlternateAlleles().size() > 1)
                        continue;
                    else {
                        if (vc.getGenotype(0).isHom())
                            homo++;
                    }
                }
                if ((double) homo / currentElement.getIntersectSNVs().size() > 0.9) {
                    result.add(i);
                    result.add(i);
                } else {
                    result.add(i);
                }
            }

            if (result.size() >= 2)
                break;
        }

        return result;
    }

}
