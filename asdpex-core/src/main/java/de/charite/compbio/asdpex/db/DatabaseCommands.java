/**
 * 
 */
package de.charite.compbio.asdpex.db;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public final class DatabaseCommands {
    public static final String CREATE_TABLE_ACCESSION = "CREATE TABLE IF NOT EXISTS accession (chromosome TEXT NOT NULL, "
            + "refseq_accession TEXT PRIMARY KEY NOT NULL, " + "refseq_gi INTEGER NOT NULL, "
            + "genbank_accession TEXT NOT NULL, " + "genbank_gi INTEGER NOT NULL)";
    public static final String CREATE_TABLE_REGION = "CREATE TABLE IF NOT EXISTS region (name TEXT PRIMARY KEY NOT NULL,"
            + "refseq_accession TEXT NOT NULL, " + "start INTEGER NOT NULL," + "stop INTEGER NOT NULL, "
            + "FOREIGN KEY(refseq_accession) REFERENCES accession(refseq_accession))";
    public static final String CREATE_TABLE_PLACEMENT = "CREATE TABLE IF NOT EXISTS placement (alt_scaf_acc TEXT PRIMARY KEY NOT NULL, "
            + "region_name TEXT NOT NULL, " + "orientation INTEGER NOT NULL, " + "alt_scaf_start INTEGER NOT NULL, "
            + "alt_scaf_stop INTEGER NOT NULL, " + "alt_start_tail INTEGER NOT NULL, "
            + "alt_stop_tail INTEGER NOT NULL, " + "parent_start INTEGER NOT NULL, " + "parent_stop INTEGER NOT NULL, "
            + "FOREIGN KEY(alt_scaf_acc) REFERENCES accession(refseq_accession))";
    public static final String CREATE_TABLE_ASDP = "CREATE TABLE IF NOT EXISTS asdp (region TEXT NOT NULL, "
            + "position INTEGER NOT NULL, " + "magic INTEGER NOT NULL, " + "ref TEXT NOT NULL, " + "alt TEXT NOT NULL, "
            + "alt_scaffold TEXT NOT NULL, " + "type TEXT NOT NULL, " + "length INTEGER, "
            + "FOREIGN KEY(region) REFERENCES region(name), "
            + "FOREIGN KEY(alt_scaffold) REFERENCES accession(genbank_accession), "
            + "PRIMARY KEY (position, magic, alt_scaffold))";
}
