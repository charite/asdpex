/**
 * 
 */
package de.charite.compbio.asdpex.exceptions;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.asdpex.exceptions.AltLociSelectorException;

/**
 * Exception thrown on problems with the command line.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class CommandLineParsingException extends AltLociSelectorException {

	public CommandLineParsingException() {
		super();
	}

	public CommandLineParsingException(String msg) {
		super(msg);
	}

	public CommandLineParsingException(String msg, ParseException cause) {
		super(msg, cause);
	}

	private static final long serialVersionUID = 1L;

}
