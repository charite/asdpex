/**
 * 
 */
package de.charite.compbio.asdpex.exceptions;

/**
 * Base class for exceptions in AltLociSelector
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AltLociSelectorException extends Exception {

	public static final long serialVersionUID = 2L;

	public AltLociSelectorException() {
		super();
	}

	public AltLociSelectorException(String msg) {
		super(msg);
	}

	public AltLociSelectorException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
