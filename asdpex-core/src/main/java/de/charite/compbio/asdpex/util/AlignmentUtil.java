/**
 * 
 */
package de.charite.compbio.asdpex.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.asdpex.data.NCBIgffAlignment;
import de.charite.compbio.asdpex.data.NCBIgffAlignmentElement;
import de.charite.compbio.asdpex.data.NCBIgffAlignmentElement.NCBIgffAlignmentElementBuilder;
import de.charite.compbio.asdpex.data.NCBIgffAlignmentElementType;

/**
 * Util to perform several task on the {@link NCBIgffAlignment}s.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AlignmentUtil {
    /** default lower limit to split large InDels */
    private final static int LIMIT = 5000;

    public static ArrayList<NCBIgffAlignment> splitupAlignment(NCBIgffAlignment alignment, boolean splitIndels,
            boolean splitN) {
        ArrayList<NCBIgffAlignment> alignments = null;
        // InDels
        if (splitIndels)
            alignments = AlignmentUtil.splitupAlignmentAtLargeIndels(alignment);
        else {
            alignments = new ArrayList<>();
            alignments.add(alignment);
        }
        // 'N's
        if (splitN) {
            ArrayList<NCBIgffAlignment> alignments2 = new ArrayList<>();
            for (NCBIgffAlignment ncbIgffAlignment : alignments) {
                // alignments2.addAll(AlignmentUtil.splitupAlignmentAtNstrech(ncbIgffAlignment, tuples))
            }
            return alignments2;
        } else
            return alignments;
    }

    /**
     * Split up the alignment at these curious large Insert/Deletions. Creates a {@link List} of
     * {@link NCBIgffAlignment}s with at least one (the input {@link NCBIgffAlignment}) object.<br>
     * The size limit for the split decision is by default set to {@link AlignmentUtil#LIMIT}.
     * 
     * @param alignment
     * @return A new List of {@link NCBIgffAlignment}s
     */
    public static ArrayList<NCBIgffAlignment> splitupAlignmentAtLargeIndels(NCBIgffAlignment... alignments) {
        ArrayList<NCBIgffAlignment> splitAlignments = new ArrayList<>();
        for (NCBIgffAlignment ncbIgffAlignment : alignments) {
            splitAlignments.addAll(AlignmentUtil.splitupAlignmentAtLargeIndels(ncbIgffAlignment, AlignmentUtil.LIMIT));
        }
        return splitAlignments;
    }

    /**
     * Split up the alignment at these curious large Insert/Deletions. Creates a {@link List} of
     * {@link NCBIgffAlignment}s with at least one (the input {@link NCBIgffAlignment}) object.<br>
     * The size limit for the split decision is by default set to {@link AlignmentUtil#LIMIT}.
     * 
     * @param alignment
     * @return A new List of {@link NCBIgffAlignment}s
     */
    public static ArrayList<NCBIgffAlignment> splitupAlignmentAtLargeIndels(NCBIgffAlignment alignment) {
        return AlignmentUtil.splitupAlignmentAtLargeIndels(alignment, AlignmentUtil.LIMIT);
    }

    /**
     * Split up the alignment at these curious large Insert/Deletions. Creates a {@link List} of
     * {@link NCBIgffAlignment}s with at least one (the input {@link NCBIgffAlignment}) object.<br>
     * The size limit for the split decision is by default set to {@link AlignmentUtil#LIMIT}.
     * 
     * @param alignment
     *            the {@link NCBIgffAlignment} to be split up
     * @param limit
     *            lower threshold for InDels sizes to be split or not
     * @return A new List of {@link NCBIgffAlignment}s
     */
    public static ArrayList<NCBIgffAlignment> splitupAlignmentAtLargeIndels(NCBIgffAlignment alignment, int limit) {
        ArrayList<NCBIgffAlignment> alignments = new ArrayList<>();

        int start = 0;
        int stop = start;
        int obacht = 0;
        // ------- TEST ---------
        // NCBIgffAlignmentElement previos = null;
        // int obacht2 = 0;
        // ------- END ---------

        for (NCBIgffAlignmentElement element : alignment.getElements()) {
            // ------- TEST ---------
            // if (element.getType() == NCBIgffAlignmentElementType.INSERTION) {
            // obacht2++;
            // previos = element;
            // } else if (element.getType() == NCBIgffAlignmentElementType.DELETION) {
            // obacht2++;
            // } else {
            // obacht2 = 0;
            // previos = null;
            // }
            // if (obacht2 > 1) {
            // System.out.println("Ref: " + alignment.getRefId() + "\talt: " + alignment.getAltId());
            // System.out.println("previous: " + previos);
            // System.out.println("current: " + element);
            // }

            // ------- END ---------

            if (element.getType() == NCBIgffAlignmentElementType.INSERTION && element.getLength() >= limit)
                obacht++;
            else if (element.getType() == NCBIgffAlignmentElementType.DELETION && element.getLength() >= limit)
                obacht++;
            else
                obacht = 0;

            if (obacht > 1) {
                // System.out.println("---> SPLIT");
                alignments.add(getSubAlignment(alignment, start, stop - 1));
                start = stop + 1;
            }
            stop++;
        }
        if (start < stop)
            alignments.add(getSubAlignment(alignment, start, stop));
        else
            alignments.add(alignment);
        // System.out.println("[INFO] ");
        return alignments;
    }

    /**
     * Split the alignment into blocks at 'N'-Stretches.
     * 
     * @param alignment
     *            the {@link NCBIgffAlignment} object to be processed
     * 
     * @param tuples
     *            sorted list with non-overlapping
     * @return
     */
    public static ArrayList<NCBIgffAlignment> splitupAlignmentAtNstrech(NCBIgffAlignment alignment,
            ArrayList<Tuple> tuples) {
        ArrayList<NCBIgffAlignment> alignments = new ArrayList<>();
        if (tuples.size() < 1) {
            alignments.add(alignment);
            return alignments;
        }

        System.out.println("!!! size elements: " + alignment.getElements().size());
        ImmutableList.Builder<NCBIgffAlignmentElement> myElementsBuilder = new ImmutableList.Builder<NCBIgffAlignmentElement>();
        ImmutableList<NCBIgffAlignmentElement> myElements;
        int refLength = 0;
        int altLength = 0;
        int curRefOffset = refLength;
        int curAltOffset = altLength;
        int tupleIdx = 0;
        for (NCBIgffAlignmentElement element : alignment.getElements()) {
            // element before 'N'-stretch
            if (element.getAlt_start() + element.getLength() <= tuples.get(tupleIdx).start) {
                myElementsBuilder.add(element); // TODO update starts!!!
                continue;
            }

            // small 'N'-stretches
            if (tuples.get(tupleIdx).end - tuples.get(tupleIdx).start < 10) {
                myElementsBuilder.add(element); // TODO update starts!!!
                continue;
            }

            switch (element.getType()) {
            case MATCH:
                if (element.getAlt_start() <= tuples.get(tupleIdx).start) {
                    System.err.println("Split inside Match");
                    // if (element.getAlt_start() + element.getLength() >=
                    // tuples.get(tupleIdx).end) {
                    System.err.println("\tin element: " + element);
                    System.err.println("\t--> 'N'-Stretch range: " + tuples.get(tupleIdx).start + " - "
                            + tuples.get(tupleIdx).end);
                    // }
                    tupleIdx++;
                }
                refLength += element.getLength();
                altLength += element.getLength();
                break;
            case INSERTION:
                if (element.getAlt_start() <= tuples.get(tupleIdx).start) {
                    System.err.println("Split inside Insertion");
                    // if (element.getAlt_start() > tuples.get(tupleIdx).start)
                    // {
                    System.err.println("\tin element: " + element);
                    System.err.println("\t--> 'N'-Stretch range: " + tuples.get(tupleIdx).start + " - "
                            + tuples.get(tupleIdx).end);
                    // }
                    myElements = myElementsBuilder.build();
                    alignments.add(new NCBIgffAlignment(alignment.getRefId(), alignment.getAltId(),
                            alignment.getRefStart(), alignment.getRefStart() + curRefOffset, alignment.isRefStrand(),
                            alignment.getAltStart(), alignment.getAltStart() + curAltOffset, alignment.isAltStrand(),
                            myElements));
                    curRefOffset = refLength;
                    curAltOffset = altLength + element.getLength();
                    tupleIdx++;
                }
                altLength += element.getLength();
                break;
            case DELETION:
                if (element.getAlt_start() <= tuples.get(tupleIdx).start) {
                    System.err.println("Split inside deletion");
                    // if (element.getAlt_start() > tuples.get(tupleIdx).start)
                    // {
                    System.err.println("\tin element: " + element);
                    System.err.println("\t--> 'N'-Stretch range: " + tuples.get(tupleIdx).start + " - "
                            + tuples.get(tupleIdx).end);
                    // }
                    tupleIdx++;
                }
                refLength += element.getLength();
                break;

            default:
                System.out.println("Was denn hier los?!");
                break;
            }
            if (tupleIdx >= tuples.size())
                break;
        }
        return alignments;
    }

    /**
     * Inner private class, which only contains a tuple of integers to store the start and stop of sequence blocks.
     * 
     *
     * @author Marten Jäger <marten.jaeger@charite.de>
     *
     */
    private class Tuple {
        public Tuple(int start, int end) {
            this.start = start;
            this.end = end;
        }

        int start;
        int end;
    }

    /**
     * Extracts the subalignment from the {@link NCBIgffAlignment} object and returns a new {@link NCBIgffAlignment}
     * object.
     * 
     * @param alignment
     *            parent {@link NCBIgffAlignment} object from where the subalignment should be extracted
     * @param startElem
     *            the first {@link NCBIgffAlignmentElement} contained in the subalignment (0-based)
     * @param endElem
     *            the last {@link NCBIgffAlignmentElement} contained in the subalignment (excl.)
     * @return a new {@link NCBIgffAlignment} object containing the {@link NCBIgffAlignmentElement}s defined by
     *         startElem and endElem and updated coordinates for reference and alternative.
     */
    private static NCBIgffAlignment getSubAlignment(NCBIgffAlignment alignment, int startElem, int endElem) {

        int refStart = alignment.getRefStart();
        int refStop = alignment.getRefStart() - 1; // 1-based --> remove 1 from cumulative length
        int altStart = alignment.getAltStart();
        int altStop = alignment.getAltStart() - 1; // 1-based --> remove 1 from cumulative length

        NCBIgffAlignmentElement element;
        for (int i = 0; i < endElem; i++) {
            element = alignment.getElements().get(i);
            // update refStart and altStart for elements before the startElem
            if (i < startElem) {
                switch (element.getType()) {
                case INSERTION:
                    altStart += element.getLength();
                    break;
                case DELETION:
                    refStart += element.getLength();
                    break;
                case MATCH:
                    refStart += element.getLength();
                    altStart += element.getLength();
                    break;

                default:
                    break;
                }
            }

            // update the refStop and altStop
            switch (element.getType()) {
            case INSERTION:
                altStop += element.getLength();
                break;
            case DELETION:
                refStop += element.getLength();
                break;
            case MATCH:
                refStop += element.getLength();
                altStop += element.getLength();
                break;

            default:
                break;
            }

        }

        ImmutableList<NCBIgffAlignmentElement> subListElements = updateElementsSublist(
                alignment.getElements().subList(startElem, endElem));
        return new NCBIgffAlignment(alignment.getRefId(), alignment.getAltId(), refStart, refStop,
                alignment.isRefStrand(), altStart, altStop, alignment.isAltStrand(), subListElements);
    }

    /**
     * Update the {@link NCBIgffAlignmentElement}s in the subList, so the first element starts again with '0' for ref
     * and alt and the remaining elements are updated by subtracting the former start form each ref and alt
     * correspondingly.
     * 
     * @param subList
     * @return
     */
    private static ImmutableList<NCBIgffAlignmentElement> updateElementsSublist(
            ImmutableList<NCBIgffAlignmentElement> subList) {
        if (subList.size() < 1)
            return subList;
        ImmutableList.Builder<NCBIgffAlignmentElement> elements = new ImmutableList.Builder<NCBIgffAlignmentElement>();

        int firstElement_ref_start = subList.get(0).getRef_start();
        int firstElement_alt_start = subList.get(0).getAlt_start();
        NCBIgffAlignmentElementBuilder builder;

        for (NCBIgffAlignmentElement element : subList) {
            builder = new NCBIgffAlignmentElementBuilder();
            builder.type(element.getType());
            builder.refStart(element.getRef_start() - firstElement_ref_start);
            builder.altStart(element.getAlt_start() - firstElement_alt_start);
            builder.length(element.getLength());
            elements.add(builder.build());
        }

        return elements.build();
    }

}
