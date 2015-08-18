/**
 * 
 */
package de.charite.compbio.hg38altlociselector.data;

/**
 * The alt_scaffold_placement.txt files contain the following infos: <br>
 * prim_asm_name <br>
 * alt_scaf_name <br>
 * alt_scaf_acc <br>
 * 
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AltScaffoldPlacementInfo {

	private String altScafName; // alt scaffold name
	private String altScafAcc; // alt scaffold accession
	private String chromosome; // parent name
	private String chromosomeAcc; // parent accession
	private String region; // region

	/**
	 * 
	 */
	private AltScaffoldPlacementInfo() {

	}

	public static class AltScaffoldPlacementInfoBuilder {

	}
}
