/**
 * 
 */
package de.charite.compbio.asdpex.data;

import com.google.common.collect.ImmutableList;

/**
 * slim Representation of the NCBI alignment.
 * 
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class NCBIgffAlignment {
    private final String refId;
    private final String altId;
    private final int refStart;
    private final int refStop;
    private final boolean refStrand;
    private final int altStart;
    private final int altStop;
    private final boolean altStrand;
    private final ImmutableList<NCBIgffAlignmentElement> elements;

    public NCBIgffAlignment(String refId, String altId, int refStart, int refStop, boolean refStrand, int altStart,
            int altStop, boolean altStrand, ImmutableList<NCBIgffAlignmentElement> elements) {
        this.refId = refId;
        this.altId = altId;
        this.refStart = refStart;
        this.refStop = refStop;
        this.refStrand = refStrand;
        this.altStart = altStart;
        this.altStop = altStop;
        this.altStrand = altStrand;
        this.elements = elements;
    }

    public String getRefId() {
        return refId;
    }

    public String getAltId() {
        return altId;
    }

    public int getRefStart() {
        return refStart;
    }

    public int getRefStop() {
        return refStop;
    }

    public boolean isRefStrand() {
        return refStrand;
    }

    public int getAltStart() {
        return altStart;
    }

    public int getAltStop() {
        return altStop;
    }

    public boolean isAltStrand() {
        return altStrand;
    }

    public ImmutableList<NCBIgffAlignmentElement> getElements() {
        return elements;
    }

    /**
     * 
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRefId()).append("\tRefSeq\tmatch\t").append(getRefStart()).append("\t").append(getRefStop())
                .append("\t0\t").append(isRefStrand() ? "+" : "-").append("\t.\tTarget=").append(getAltId())
                .append("\t").append(getAltStart()).append("\t").append(getAltStop()).append("\t")
                .append(isAltStrand() ? "+" : "-").append(";Gap=");
        for (NCBIgffAlignmentElement element : elements) {
            switch (element.getType()) {
            case MATCH:
                sb.append("M");
                break;
            case INSERTION:
                sb.append("I");
                break;
            case DELETION:
                sb.append("D");
                break;
            default:
                break;
            }
            sb.append(element.getLength()).append(" ");
        }
        return sb.toString();
    }

    // /**
    // * TODO move to AlignmentUtil<br>
    // * Extracts the subalignment from the {@link NCBIgffAlignment} object and returns a new {@link NCBIgffAlignment}
    // * object.
    // *
    // * @param alignment
    // * parent {@link NCBIgffAlignment} object from where the subalignment should be extracted
    // * @param startElem
    // * the first {@link NCBIgffAlignmentElement} contained in the subalignment (0-based)
    // * @param endElem
    // * the last {@link NCBIgffAlignmentElement} contained in the subalignment (excl.)
    // * @return a new {@link NCBIgffAlignment} object containing the {@link NCBIgffAlignmentElement}s defined by
    // * startElem and endElem and updated coordinates for reference and alternative.
    // */
    // public NCBIgffAlignment getSubAlignment(int startElem, int endElem) {
    //
    // int refStart = this.getRefStart();
    // int refStop = this.getRefStart() - 1; // 1-based --> remove 1 from cumulative length
    // int altStart = this.getAltStart();
    // int altStop = this.getAltStart() - 1; // 1-based --> remove 1 from cumulative length
    //
    // NCBIgffAlignmentElement element;
    // for (int i = 0; i < endElem; i++) {
    // element = this.getElements().get(i);
    // // update refStart and altStart for elements before the startElem
    // if (i < startElem) {
    // switch (element.getType()) {
    // case INSERTION:
    // altStart += element.getLength();
    // break;
    // case DELETION:
    // refStart += element.getLength();
    // break;
    // case MATCH:
    // refStart += element.getLength();
    // altStart += element.getLength();
    // break;
    //
    // default:
    // break;
    // }
    // }
    //
    // // update the refStop and altStop
    // switch (element.getType()) {
    // case INSERTION:
    // altStop += element.getLength();
    // break;
    // case DELETION:
    // refStop += element.getLength();
    // break;
    // case MATCH:
    // refStop += element.getLength();
    // altStop += element.getLength();
    // break;
    //
    // default:
    // break;
    // }
    //
    // }
    //
    // ImmutableList<NCBIgffAlignmentElement> subListElements = updateElementsSublist(
    // this.getElements().subList(startElem, endElem));
    // // System.out.println(
    // // "\textract elements from: " + startElem + " - " + endElem + " von " + this.getElements().size());
    // return new NCBIgffAlignment(this.getRefId(), this.getAltId(), refStart, refStop, this.isRefStrand(), altStart,
    // altStop, this.isAltStrand(), subListElements);
    // }
    //
    // /**
    // * TODO move to AlignmentUtil<br>
    // * Update the {@link NCBIgffAlignmentElement}s in the subList, so the first element starts again with '0' for ref
    // * and alt and the remaining elements are updated by subtracting the former start form each ref and alt
    // * correspondingly.
    // *
    // * @param subList
    // * @return
    // */
    // private ImmutableList<NCBIgffAlignmentElement> updateElementsSublist(
    // ImmutableList<NCBIgffAlignmentElement> subList) {
    // if (subList.size() < 1)
    // return subList;
    // ImmutableList.Builder<NCBIgffAlignmentElement> elements = new ImmutableList.Builder<NCBIgffAlignmentElement>();
    //
    // int firstElement_ref_start = subList.get(0).getRef_start();
    // int firstElement_alt_start = subList.get(0).getAlt_start();
    // NCBIgffAlignmentElementBuilder builder;
    //
    // for (NCBIgffAlignmentElement element : subList) {
    // builder = new NCBIgffAlignmentElementBuilder();
    // builder.type(element.getType());
    // builder.refStart(element.getRef_start() - firstElement_ref_start);
    // builder.altStart(element.getAlt_start() - firstElement_alt_start);
    // builder.length(element.getLength());
    // elements.add(builder.build());
    // }
    //
    // return elements.build();
    // }

}
