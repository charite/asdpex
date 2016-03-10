/**
 * 
 */
package de.charite.compbio.hg38altlociselector.db;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public final class DatabaseCommands {
    public final static String CREATE_TABLE_ACCESSION = "CREATE TABLE IF NOT EXISTS accession (chromosome TEXT NOT NULL, "
            + "refseq_accession TEXT PRIMARY KEY NOT NULL, " + "refseq_gi INTEGER NOT NULL, "
            + "genbank_accession TEXT NOT NULL, " + "genbank_gi INTEGER NOT NULL)";
    public final static String CREATE_TABLE_REGION = "CREATE TABLE IF NOT EXISTS region (name TEXT PRIMARY KEY NOT NULL,"
            + "refseq_accession TEXT NOT NULL, " + "start INTEGER NOT NULL," + "stop INTEGER NOT NULL, "
            + "FOREIGN KEY(refseq_accession) REFERENCES accession(refseq_accession))";
    public final static String CREATE_TABLE_PLACEMENT = "CREATE TABLE IF NOT EXISTS placement (alt_scaf_acc TEXT PRIMARY KEY NOT NULL, "
            + "region_name TEXT NOT NULL, " + "orientation INTEGER NOT NULL, " + "alt_scaf_start INTEGER NOT NULL, "
            + "alt_scaf_stop INTEGER NOT NULL, " + "alt_start_tail INTEGER NOT NULL, "
            + "alt_stop_tail INTEGER NOT NULL, " + "parent_start INTEGER NOT NULL, " + "parent_stop INTEGER NOT NULL, "
            + "FOREIGN KEY(region_name) REFERENCES region(name), "
            + "FOREIGN KEY(alt_scaf_acc) REFERENCES accession(refseq_accession))";
}
