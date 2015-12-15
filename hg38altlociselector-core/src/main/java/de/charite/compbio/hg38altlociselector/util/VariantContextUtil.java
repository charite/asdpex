/**
 * 
 */
package de.charite.compbio.hg38altlociselector.util;

import java.util.ArrayList;

import de.charite.compbio.hg38altlociselector.data.PairwiseVariantContextIntersect;
import de.charite.compbio.hg38altlociselector.data.PairwiseVariantContextIntersect.PairwiseVariantContextIntersectBuilder;
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
	 * the results.
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

		int set1SV = 0;
		int set2SV = 0;
		int intersectSNV = 0;

		// calc SVs
		for (VariantContext variantContext : set2) {
			if (variantContext.getType() == Type.SYMBOLIC)
				set2SV++;
		}
		for (VariantContext vc1 : set1) {
			if (vc1.getType() == Type.SYMBOLIC) {
				set1SV++;
				continue;
			}

			for (VariantContext vc2 : set2) {
				if (vc2.getType() == Type.SYMBOLIC)
					continue;
				if (vc1.getContig().equals(vc2.getContig()) && vc1.getStart() == vc2.getStart()
						&& vc2.hasAllele(vc1.getAlleles().get(0))) {
					// System.out.println(vc1);
					// System.out.println(vc2);
					intersectSNV++;
				}
				// for (Allele allele : vc2.getAlternateAlleles()) {
				// if(vc1.geta)System.out.println(allele.toString());
				// }
			}

		}
		builder.set1SNVs(set1.size() - set1SV);
		builder.set1SVs(set1SV);
		builder.set2SNVs(set2.size() - set2SV);
		builder.set2SVs(set2SV);
		builder.intersectSNVs(intersectSNV);
		builder.onlySet1SNVs(set1.size() - set1SV - intersectSNV);
		builder.onlySet2SNVs(set2.size() - set2SV - intersectSNV);

		return builder.build();
	}

}
