/**
 * 
 */
package de.charite.compbio.hg38altlociselector.exceptions;

import org.apache.commons.cli.ParseException;

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
