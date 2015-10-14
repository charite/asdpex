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
	public String dataPath = "../data";

	public String fastqOutputPath = "../fasta";

	public String alignmentPath = "../data/alignments";

	public String seedInfoPath = "../seed";
	
	public String seqanALign = "../seqan/regionalign2bed";

	public Command command;

	public String altAccessionsPath = dataPath + "/alts_accessions_GRCh38.p2";
	public String altScaffoldPlacementPath = dataPath + "/all_alt_scaffold_placement.txt";
	public String referencePath = dataPath + "/bwa.kit/hs38DH.fa";
	public String genomicRegionsDefinitionsPath = dataPath + "/genomic_regions_definitions.txt";

	public boolean singleAltLociFile = false;

	/**
	 * Line length of output fasta files.
	 */
	public int fastaLineLength = 70;

	/**
	 * The command that is to be executed.
	 */
	public enum Command {
		DOWNLOAD, ANNOTATE_VCF, CREATE_FASTA, CREATE_SEED
	}
}