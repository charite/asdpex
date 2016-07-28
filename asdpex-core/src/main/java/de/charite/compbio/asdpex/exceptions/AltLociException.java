/**
 * 
 */
package de.charite.compbio.asdpex.exceptions;

/**
 * Base Exception class.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AltLociException extends AltLociSelectorException {

	private static final long serialVersionUID = 1L;

	public AltLociException() {
		super();
	}

	public AltLociException(String msg) {
		super(msg);
	}

	public AltLociException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
