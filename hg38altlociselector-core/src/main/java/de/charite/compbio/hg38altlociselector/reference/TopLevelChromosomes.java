package de.charite.compbio.hg38altlociselector.reference;

import com.google.common.collect.ImmutableList;

public class TopLevelChromosomes {

    private static TopLevelChromosomes instance;

    private final ImmutableList<String> toplevel;

    /**
     * dummy
     * 
     */
    private TopLevelChromosomes() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add("1");
        builder.add("2");
        builder.add("3");
        builder.add("4");
        builder.add("5");
        builder.add("6");
        builder.add("7");
        builder.add("8");
        builder.add("9");
        builder.add("10");
        builder.add("11");
        builder.add("12");
        builder.add("13");
        builder.add("14");
        builder.add("15");
        builder.add("16");
        builder.add("17");
        builder.add("18");
        builder.add("19");
        builder.add("20");
        builder.add("21");
        builder.add("22");
        builder.add("X");
        builder.add("Y");
        builder.add("M");
        this.toplevel = builder.build();
    }

    public static TopLevelChromosomes getInstance() {
        if (TopLevelChromosomes.instance == null) {
            TopLevelChromosomes.instance = new TopLevelChromosomes();
        }
        return TopLevelChromosomes.instance;
    }

    public ImmutableList<String> getToplevel() {
        return toplevel;
    }

}
