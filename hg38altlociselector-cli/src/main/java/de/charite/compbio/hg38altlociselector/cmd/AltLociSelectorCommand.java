/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;

/**
 * Super class for all commands, i.e. the classes implementing one Command execution
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public abstract class AltLociSelectorCommand {

	/** Configuration to use for the command execution. */
	protected Hg38altLociSeletorOptions options;

	/**
	 * Initialize the JannovarCommand.
	 *
	 * @param argv
	 *            command line arguments to use
	 * @throws CommandLineParsingException
	 *             on problems with the command line
	 * @throws HelpRequestedException
	 *             if the user requested help through the command line parameters
	 */
	public AltLociSelectorCommand(String[] args) throws CommandLineParsingException, HelpRequestedException {
		this.options = parseCommandLine(args);
	}

	/**
	 * Function for parsing the command line.
	 *
	 * @param argv
	 *            command line to parse, as in the program's main function
	 * @return {@link Hg38altLociSeletorOptions} with the programs' configuration
	 * @throws CommandLineParsingException
	 *             on problems with the command line
	 * @throws HelpRequestedException
	 *             when the user requested the help page
	 */
	protected abstract Hg38altLociSeletorOptions parseCommandLine(String[] args)
			throws CommandLineParsingException, HelpRequestedException;

	public abstract void run() throws AltLociSelectorException;

}
