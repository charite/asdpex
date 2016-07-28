/**
 * 
 */
package de.charite.compbio.asdpex.data;

import java.io.Serializable;
import java.util.ArrayList;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Object storing the result output after calculating the Intersect of two {@link VariantContext} Lists.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class PairwiseVariantContextIntersect implements Serializable, Comparable<PairwiseVariantContextIntersect> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // private final CloseableIterator<VariantContext> set1;
    /** SNVs in the first set */
    private final ArrayList<VariantContext> set1SNVs;
    /** SV in the first set */
    private final ArrayList<VariantContext> set1SVs;
    // private final int onlySet1SNVs;

    // private final CloseableIterator<VariantContext> set2;
    /** SNVs in the second set */
    private final ArrayList<VariantContext> set2SNVs;
    /** SV in the second set */
    private final ArrayList<VariantContext> set2SVs;
    // private final int onlySet2SNVs;

    /** overlapping SNVs between the first and second set */
    private final ArrayList<VariantContext> intersectSNVs;
    /** number of overlapping SVs between the first and the second set */
    private final int intersectSVs;

    /** array with flags of the same size of set1 with true = intersect , false otherwise */
    private boolean[] set1flagged;

    private PairwiseVariantContextIntersect(PairwiseVariantContextIntersectBuilder builder) {
        // this.set1 = builder.set1;
        this.set1SNVs = builder.set1SNVs;
        this.set1SVs = builder.set1SVs;
        // this.onlySet1SNVs = builder.onlySet1SNVs;

        // this.set2 = builder.set2;
        this.set2SNVs = builder.set2SNVs;
        this.set2SVs = builder.set2SVs;
        // this.onlySet2SNVs = builder.onlySet2SNVs;

        this.intersectSNVs = builder.intersectSNVs;
        this.intersectSVs = builder.intersectSVs;

        this.set1flagged = builder.set1flagged;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SET1:\n SNV: ").append(this.set1SNVs.size()).append("\n SV: ").append(this.set1SVs.size())
                .append("\n");
        sb.append("SET2:\n SNV: ").append(this.set2SNVs.size()).append("\n SV: ").append(this.set2SVs.size())
                .append("\n");
        sb.append("INTERSECT:\n SNV: ").append(this.intersectSNVs.size()).append("\n SV: ").append(this.intersectSVs)
                .append("\n");

        return sb.toString();
    }

    /**
     * Calculates the left over SNVs which are no ASDPs. This is done by sum up all SNVs from the first and second set
     * and remove the overlapping SNVs.
     * 
     * @return the not explainable SNVs e.g. all SNVs which are no ASDPs
     */
    public int getFinalSVNs() {
        return this.set1SNVs.size() + this.set2SNVs.size() - (2 * this.intersectSNVs.size());
    }

    // /**
    // * @return the set1
    // */
    // public CloseableIterator<VariantContext> getSet1() {
    // return set1;
    // }

    /**
     * @return the set1SNVs
     */
    public ArrayList<VariantContext> getSet1SNVs() {
        return set1SNVs;
    }

    /**
     * @return the set1SVs
     */
    public ArrayList<VariantContext> getSet1SVs() {
        return set1SVs;
    }

    // /**
    // * @return the onlySet1SNVs
    // */
    // public int getOnlySet1SNVs() {
    // return set1SNVs - intersectSNVs;
    // }

    // /**
    // * @return the set2
    // */
    // public CloseableIterator<VariantContext> getSet2() {
    // return set2;
    // }

    /**
     * @return the set2SNVs
     */
    public ArrayList<VariantContext> getSet2SNVs() {
        return set2SNVs;
    }

    /**
     * @return the set2SVs
     */
    public ArrayList<VariantContext> getSet2SVs() {
        return set2SVs;
    }

    // /**
    // * @return the onlySet2SNVs
    // */
    // public int getOnlySet2SNVs() {
    // return set2SNVs - intersectSNVs;
    // }

    /**
     * @return the intersectSNVs
     */
    public ArrayList<VariantContext> getIntersectSNVs() {
        return intersectSNVs;
    }

    /**
     * @return the intersectSVs
     */
    public int getIntersectSVs() {
        return intersectSVs;
    }

    /**
     * Returns an boolean array with the size of set1 and intersects flagged as true;
     * 
     * @return
     */
    public boolean[] getSet1flagged() {
        return set1flagged;
    }

    public static class PairwiseVariantContextIntersectBuilder {
        // private CloseableIterator<VariantContext> set1;
        private ArrayList<VariantContext> set1SNVs;
        private ArrayList<VariantContext> set1SVs;
        private int onlySet1SNVs;

        // private CloseableIterator<VariantContext> set2;
        private ArrayList<VariantContext> set2SNVs;
        private ArrayList<VariantContext> set2SVs;
        private int onlySet2SNVs;

        private ArrayList<VariantContext> intersectSNVs;
        private int intersectSVs;
        /* array with flags of the same size of set1 with true = intersect , false otherwise */
        private boolean[] set1flagged;

        // public PairwiseVariantContextIntersectBuilder
        // set1(CloseableIterator<VariantContext> set) {
        // this.set1 = set;
        // return this;
        // }
        //
        // public PairwiseVariantContextIntersectBuilder
        // set2(CloseableIterator<VariantContext> set) {
        // this.set2 = set;
        // return this;
        // }

        public PairwiseVariantContextIntersectBuilder set1SNVs(ArrayList<VariantContext> set1snv) {
            this.set1SNVs = set1snv;
            return this;
        }

        public PairwiseVariantContextIntersectBuilder set1SVs(ArrayList<VariantContext> set1sv) {
            this.set1SVs = set1sv;
            return this;
        }

        public PairwiseVariantContextIntersectBuilder onlySet1SNVs(int value) {
            this.onlySet1SNVs = value;
            return this;
        }

        public PairwiseVariantContextIntersectBuilder set2SNVs(ArrayList<VariantContext> set2snv) {
            this.set2SNVs = set2snv;
            return this;
        }

        public PairwiseVariantContextIntersectBuilder set2SVs(ArrayList<VariantContext> set2sv) {
            this.set2SVs = set2sv;
            return this;
        }

        public PairwiseVariantContextIntersectBuilder onlySet2SNVs(int value) {
            this.onlySet2SNVs = value;
            return this;
        }

        public PairwiseVariantContextIntersectBuilder intersectSNVs(ArrayList<VariantContext> intersectSNV) {
            this.intersectSNVs = intersectSNV;
            return this;
        }

        public PairwiseVariantContextIntersectBuilder intersectSVs(int value) {
            this.intersectSVs = value;
            return this;
        }

        public PairwiseVariantContextIntersectBuilder set1flagged(boolean[] flags) {
            this.set1flagged = flags;
            return this;
        }

        public PairwiseVariantContextIntersect build() {
            return new PairwiseVariantContextIntersect(this);
        }
    }

    /**
     * 
     */
    @Override
    public int compareTo(PairwiseVariantContextIntersect o) {
        int a = this.set1SNVs.size() + this.set2SNVs.size() - (2 * this.intersectSNVs.size());
        int b = o.set1SNVs.size() + o.set2SNVs.size() - (2 * o.intersectSNVs.size());

        if (a < b)
            return -1;
        else if (a > b)
            return 1;

        return 0;
    }
}
