/**
 * 
 */
package de.charite.compbio.asdpex.exceptions;

import de.charite.compbio.asdpex.exceptions.AltLociSelectorException;

/**
 * Thrown when the user requests help.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class HelpRequestedException extends AltLociSelectorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HelpRequestedException() {
		super();
	}

	public HelpRequestedException(String msg) {
		super(msg);
	}

	public HelpRequestedException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
