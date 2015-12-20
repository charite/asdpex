package de.charite.compbio.hg38altlociselector.exceptions;

/**
 * Annotation exceptions are thrown when the information provided is not well
 * formed or not sufficient to create a correct annotation.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 */
public class AnnotationException extends AltLociSelectorException {

    public static final long serialVersionUID = 2L;

    public AnnotationException() {
        super();
    }

    public AnnotationException(String msg) {
        super(msg);
    }

    public AnnotationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}