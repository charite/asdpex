/**
 * 
 */
package de.charite.compbio.hg38altlociselector.exceptions;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class NCBIgffAlignmentInfoParseException extends Exception {
	public static final long serialVersionUID = 2L;

	public NCBIgffAlignmentInfoParseException() {
		super();
	}

	public NCBIgffAlignmentInfoParseException(String msg) {
		super(msg);
	}

	public NCBIgffAlignmentInfoParseException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
