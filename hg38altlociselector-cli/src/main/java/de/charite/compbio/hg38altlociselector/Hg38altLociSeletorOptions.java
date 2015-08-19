/**
 * 
 */
package de.charite.compbio.hg38altlociselector;

/**
 * Configuration for the Hg38altLociSeletor program.
 * 
 * Most of the parameters are only used by one or several but not all commands.
 * 
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public final class Hg38altLociSeletorOptions {

	/** directory to use for the downloads and the serialized file */
	public String downloadPath = "../data";
	public Command command;

	public String altAccessionsPath = downloadPath + "/alts_accessions_GRCh38.p2";
	public String altScaffoldPlacementPath = downloadPath + "/all_alt_scaffold_placement.txt";
	public String referencePath = downloadPath + "/bwa.kit/hs38DH.fa";

	/**
	 * The command that is to be executed.
	 */
	public enum Command {
		DOWNLOAD, ANNOTATE_VCF, CREATE_FASTA
	}
}
