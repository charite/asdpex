/**
 * 
 */
package de.charite.compbio.hg38altlociselector.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.hg38altlociselector.data.AccessionInfo;
import de.charite.compbio.hg38altlociselector.data.AltScaffoldPlacementInfo;
import de.charite.compbio.hg38altlociselector.data.AltScaffoldPlacementInfo.AltScaffoldPlacementInfoBuilder;
import de.charite.compbio.hg38altlociselector.data.RegionInfo;
import de.charite.compbio.hg38altlociselector.data.RegionInfo.RegionInfoBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContext.Type;

/**
 * Simple class to manage the SQLite database
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class DatabaseManger {
    private String databasePath;
    private static DataSource dataSource;
    private Connection connectionInstance;

    /**
     * hidden empty constructor
     */
    private DatabaseManger() {
    }

    /**
     * 
     * @param path
     *            to the SQLite DB file
     */
    public DatabaseManger(String path) {
        this.databasePath = path;

        try {
            Class.forName("org.sqlite.JDBC");
            connectionInstance = DriverManager.getConnection("jdbc:sqlite:" + this.databasePath);
            connectionInstance.setAutoCommit(false);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("[INFO] Opened database successfully");
    }

    /**
     * Creates the database by simply dropping existing tables for (region, accession, placement) and recreate them.
     * This is easier than deleting just the entries.
     */
    public void createDatabase() {
        dropTables("placement", "region", "accession");
        createTable(DatabaseCommands.CREATE_TABLE_ACCESSION);
        createTable(DatabaseCommands.CREATE_TABLE_REGION);
        createTable(DatabaseCommands.CREATE_TABLE_PLACEMENT);
    }

    /**
     * Creates the asdp table by simply dropping an existing table and then recreate it. This is easier than deleting
     * just the entries.
     */
    public void addAsdpTable() {
        dropTables("asdp");
        createTable(DatabaseCommands.CREATE_TABLE_ASDP);
    }

    /**
     * Create (honestly it just executes an update) a table by executing the predefined update SQL command.
     * 
     * @param sql
     *            the predefined SQL command
     */
    private void createTable(String sql) {
        Statement stmt = null;
        try {
            stmt = this.getConnectionInstance().createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    /**
     * Drop all passed tables by table name.
     * 
     * @param strings
     *            names of the tables to be dropped
     */
    private void dropTables(String... strings) {
        Statement stmt = null;
        try {
            stmt = this.getConnectionInstance().createStatement();
            for (String table : strings) {
                stmt.executeUpdate("DROP TABLE IF EXISTS " + table);
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Return the {@link Connection} to the SQLite database.
     * 
     * @return
     */
    public Connection getConnectionInstance() {
        if (connectionInstance == null) {
            System.err.println(this.getClass().getName() + ": failed to get the connection - null");

        }
        return connectionInstance;
    }

    /**
     * Close the {@link Connection} to the SQLite database.
     */
    public void closeConnection() {
        try {
            this.connectionInstance.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Upload all {@link AccessionInfo} into the database.
     * 
     * @param accessions
     *            {@link AccessionInfo}s to be uploaded
     * @throws SQLException
     */
    public void uploadAccessionInfos(ImmutableMap<String, AccessionInfo> accessions) throws SQLException {

        // Statement stmt = this.connectionInstance.createStatement();
        PreparedStatement stmt = this.connectionInstance.prepareStatement(
                "INSERT INTO accession (chromosome, refseq_accession, refseq_gi, genbank_accession, genbank_gi) VALUES (?,?,?,?,?)");
        for (AccessionInfo acc : accessions.values()) {
            stmt.setString(1, acc.getChromosome());
            stmt.setString(2, acc.getRefseqAccessionVersion());
            stmt.setInt(3, acc.getRefseqGi());
            stmt.setString(4, acc.getGenbankAccessionVersion());
            stmt.setInt(5, acc.getGenbankGi());
            stmt.execute();
        }
        stmt.close();
        this.connectionInstance.commit();
    }

    /**
     * Upload the list of {@link RegionInfo}s into the database.
     * 
     * @param regions
     *            {@link RegionInfo}s to be uploaded
     * @throws SQLException
     */
    public void uploadRegionInfos(ImmutableMap<String, RegionInfo> regions) throws SQLException {
        PreparedStatement stmt = this.connectionInstance
                .prepareStatement("INSERT INTO region (name, refseq_accession, start, stop) VALUES (?,?,?,?)");
        for (RegionInfo region : regions.values()) {
            if (region.getChromosomeInfo() == null) {
                System.out.println("[WARN] failed to get Chromosome info for region: " + region.getRegionName());
                continue;
            }
            stmt.setString(1, region.getRegionName());
            stmt.setString(2, region.getChromosomeInfo().getRefseqAccessionVersion());
            stmt.setInt(3, region.getStart());
            stmt.setInt(4, region.getStop());
            stmt.execute();
        }
        stmt.close();
        this.connectionInstance.commit();
    }

    /**
     * Upload the {@link AltScaffoldPlacementInfo}s into the database.
     * 
     * @param placements
     *            {@link AltScaffoldPlacementInfo}s to be uploaded
     * @throws SQLException
     */
    public void uploadScaffoldPlacement(ImmutableMap<String, AltScaffoldPlacementInfo> placements) throws SQLException {
        PreparedStatement stmt = this.connectionInstance.prepareStatement(
                "INSERT INTO placement (alt_scaf_acc, region_name, orientation, alt_scaf_start, alt_scaf_stop, alt_start_tail, alt_stop_tail, parent_start, parent_stop) VALUES (?,?,?,?,?,?,?,?,?)");
        for (AltScaffoldPlacementInfo place : placements.values()) {
            stmt.setString(1, place.getAltScafAcc());
            stmt.setString(2, place.getRegion());
            stmt.setInt(3, place.isStrand() ? 1 : 0);
            stmt.setInt(4, place.getAltScafStart());
            stmt.setInt(5, place.getAltScafStop());
            stmt.setInt(6, place.getAltStartTail());
            stmt.setInt(7, place.getAltStopTail());
            stmt.setInt(8, place.getParentStart());
            stmt.setInt(9, place.getParentStop());
            stmt.execute();
        }
        stmt.close();
        this.connectionInstance.commit();
    }

    /**
     * Uploads the {@link List} of ASDP {@link VariantContext}s into the database.
     * 
     * @param variantList
     * @throws SQLException
     */
    public void uploadAsdp(ArrayList<VariantContext> variantList) throws SQLException {
        PreparedStatement stmt = this.connectionInstance.prepareStatement(
                "INSERT INTO asdp (region, position, magic, ref, alt, alt_scaffold, type, length) VALUES (?,?,?,?,?,?,?,?)");
        String alt_scaffold;
        for (VariantContext variant : variantList) {
            stmt.setString(1, (String) variant.getAttribute("RE"));
            stmt.setInt(2, variant.getStart());
            stmt.setInt(3, variant.getReference().getDisplayString().length()
                    - variant.getAlternateAllele(0).getDisplayString().length());
            stmt.setString(4, variant.getReference().getDisplayString());
            stmt.setString(5, variant.getAlternateAllele(0).getDisplayString());
            alt_scaffold = (String) variant.getAttribute("AL");
            alt_scaffold = alt_scaffold.substring(alt_scaffold.indexOf('_') + 1, alt_scaffold.length() - 4);
            alt_scaffold = alt_scaffold.replace('v', '.');
            stmt.setString(6, alt_scaffold);
            if (variant.getType() == Type.SYMBOLIC)
                stmt.setString(7, (String) variant.getAttribute("SVTYPE"));
            else
                stmt.setString(7, "SNV");
            Object svlen = variant.getAttribute("SVLEN");
            if (svlen != null)
                stmt.setInt(8, Integer.parseInt((String) svlen));
            else
                stmt.setNull(8, Types.INTEGER);
            stmt.execute();
        }
        stmt.close();
        this.connectionInstance.commit();
    }

    /**
     * Returns the number of entries in the specified table.
     * 
     * @param tableName
     *            name of the table (e.g. region, placement, asdp)
     * @return number of entries in the table
     * @throws SQLException
     */
    public int getTableSize(String tableName) throws SQLException {

        PreparedStatement stmt = this.connectionInstance.prepareStatement("SELECT COUNT(*) AS size FROM " + tableName);
        ResultSet rs = stmt.executeQuery();
        return (rs.getInt("size"));
    }

    /**
     * Returns the Identifier of
     * 
     * @param chromosome
     * @return
     * @throws SQLException
     */
    public ImmutableList<RegionInfo> getRegionNamesOnChromosome(String chromosome) throws SQLException {
        ImmutableList.Builder<RegionInfo> builder = new ImmutableList.Builder<>();
        RegionInfoBuilder regionInfoBuilder;
        PreparedStatement stmt = this.connectionInstance
                .prepareStatement("SELECT DISTINCT r.name, r.start, r.stop FROM region r, accession a, placement p "
                        + "WHERE r.refseq_accession = a.refseq_accession " + "AND r.name = p.region_name "
                        + "AND a.chromosome = ? " + "ORDER BY r.start ASC");
        stmt.setString(1, chromosome.replaceFirst("^chr", ""));
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            regionInfoBuilder = new RegionInfoBuilder();
            regionInfoBuilder.chromosome(null);
            regionInfoBuilder.regionName(rs.getString(1));
            regionInfoBuilder.start(rs.getInt(2));
            regionInfoBuilder.stop(rs.getInt(3));
            builder.add(regionInfoBuilder.build());
        }
        return (builder.build());
    }

    /**
     * Returns a list of {@link AltScaffoldPlacementInfo}s in the region with the given name.
     * 
     * @param regionName
     *            name of the region
     * @return list of {@link AltScaffoldPlacementInfo}s in the region
     * @throws SQLException
     */
    public ImmutableList<AltScaffoldPlacementInfo> getAltScaffoldPlacementInfos(String regionName) throws SQLException {
        ImmutableList.Builder<AltScaffoldPlacementInfo> builder = new ImmutableList.Builder<>();
        AltScaffoldPlacementInfoBuilder aspBuilder;
        PreparedStatement stmt = this.connectionInstance
                .prepareStatement("SELECT * FROM placement p WHERE p.region_name = ?");
        stmt.setString(1, regionName);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            aspBuilder = new AltScaffoldPlacementInfoBuilder();
            aspBuilder.altScafAcc(rs.getString(1));
            aspBuilder.region(rs.getString(2));
            aspBuilder.strand(rs.getInt(3) == 1 ? true : false);
            aspBuilder.altScafStart(rs.getInt(4));
            aspBuilder.altScafStop(rs.getInt(5));
            aspBuilder.altStartTail(rs.getInt(6));
            aspBuilder.altStopTail(rs.getInt(7));
            aspBuilder.parentStart(rs.getInt(8));
            aspBuilder.parentStop(rs.getInt(9));
            builder.add(aspBuilder.build());
        }
        return (builder.build());
    }

    /**
     * Returns the alt. scaffold identifier used in the reference fasta file (e.g chr1_KI270760v1_alt)
     * 
     * @param altScaffoldAccession
     *            alt. scaffold refseq identifier (e.g. NT_187514.1)
     * @return
     * @throws SQLException
     */
    public String getFastaIdentifier(String altScaffoldAccession) throws SQLException {
        PreparedStatement stmt = this.connectionInstance.prepareStatement(
                "SELECT a.chromosome, a.genbank_accession FROM accession a WHERE a.refseq_accession = ?");
        stmt.setString(1, altScaffoldAccession);
        ResultSet rs = stmt.executeQuery();
        StringBuilder sb = new StringBuilder();
        if (rs.next()) {
            sb.append("chr").append(rs.getInt(1)).append("_").append(rs.getString(2).replace(".", "v")).append("_alt");
            return sb.toString();
        } else
            return null;
    }

    /**
     * Returns the minimum ASDP position for a specific region.
     * 
     * @param region
     *            name of the region (e.g. REGION108)
     * @return minimum ASDP position in region
     * @throws SQLException
     */
    public int getRegionMinimumAsdpPosition(String region) throws SQLException {

        PreparedStatement stmt = this.connectionInstance
                .prepareStatement("SELECT MIN(a.position) AS min FROM asdp a WHERE a.region = ?");
        stmt.setString(1, region);
        ResultSet rs = stmt.executeQuery();
        return (rs.getInt("min"));
    }

    /**
     * Returns the maximum ASDP position for a specific region.
     * 
     * @param region
     *            name of the region (e.g. REGION108)
     * @return maximum ASDP position in region
     * @throws SQLException
     */
    public int getRegionMaximumAsdpPosition(String region) throws SQLException {

        PreparedStatement stmt = this.connectionInstance
                .prepareStatement("SELECT MAX(a.position) AS max FROM asdp a WHERE a.region = ?");
        stmt.setString(1, region);
        ResultSet rs = stmt.executeQuery();
        return (rs.getInt("max"));
    }
}
