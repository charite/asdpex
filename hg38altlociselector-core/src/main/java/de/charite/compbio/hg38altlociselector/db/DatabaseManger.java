/**
 * 
 */
package de.charite.compbio.hg38altlociselector.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.hg38altlociselector.data.AccessionInfo;
import de.charite.compbio.hg38altlociselector.data.AltScaffoldPlacementInfo;
import de.charite.compbio.hg38altlociselector.data.RegionInfo;

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
        System.out.println("Opened database successfully");
    }

    public void createDatabase() {
        dropTables("region", "accession", "placement");
        createTable(DatabaseCommands.CREATE_TABLE_REGION);
        createTable(DatabaseCommands.CREATE_TABLE_ACCESSION);
        createTable(DatabaseCommands.CREATE_TABLE_PLACEMENT);
    }

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
     * @param infos
     * @throws SQLException
     */
    public void uploadAccessionInfos(ImmutableMap<String, AccessionInfo> infos) throws SQLException {

        // Statement stmt = this.connectionInstance.createStatement();
        PreparedStatement stmt = this.connectionInstance.prepareStatement(
                "INSERT INTO accession (chromosome, refseq_accession, refseq_gi, genbank_accession, genbank_gi) VALUES (?,?,?,?,?)");
        String sql;
        for (AccessionInfo info : infos.values()) {
            stmt.setString(1, info.getChromosome());
            stmt.setString(2, info.getRefseqAccessionVersion());
            stmt.setInt(3, info.getRefseqGi());
            stmt.setString(4, info.getGenbankAccessionVersion());
            stmt.setInt(5, info.getGenbankGi());
            stmt.execute();
        }
        stmt.close();
        this.connectionInstance.commit();
    }

    public void uploadRegionInfos(ImmutableMap<String, RegionInfo> infos) throws SQLException {
        PreparedStatement stmt = this.connectionInstance
                .prepareStatement("INSERT INTO region (name, refseq_accession, start, stop) VALUES (?,?,?,?)");
        String sql;
        for (RegionInfo info : infos.values()) {
            if (info.getChromosomeInfo() == null) {
                System.out.println("[ERROR] failed to get Chromosome info for region: " + info.getRegionName());
                continue;
            }
            stmt.setString(1, info.getRegionName());
            stmt.setString(2, info.getChromosomeInfo().getRefseqAccessionVersion());
            stmt.setInt(3, info.getStart());
            stmt.setInt(4, info.getStop());
            stmt.execute();
        }
        stmt.close();
        this.connectionInstance.commit();
    }

    public void uploadScaffoldPlacement(ImmutableMap<String, AltScaffoldPlacementInfo> infos) throws SQLException {
        PreparedStatement stmt = this.connectionInstance.prepareStatement(
                "INSERT INTO placement (alt_scaf_acc, region_name, orientation, alt_scaf_start, alt_scaf_stop, alt_start_tail, alt_stop_tail, parent_start, parent_stop) VALUES (?,?,?,?,?,?,?,?,?)");
        String sql;
        for (AltScaffoldPlacementInfo info : infos.values()) {
            stmt.setString(1, info.getAltScafAcc());
            stmt.setString(2, info.getRegion());
            stmt.setInt(3, info.isStrand() ? 1 : 0);
            stmt.setInt(4, info.getAltScafStart());
            stmt.setInt(5, info.getAltScafStop());
            stmt.setInt(6, info.getAltStartTail());
            stmt.setInt(7, info.getAltStopTail());
            stmt.setInt(8, info.getParentStart());
            stmt.setInt(9, info.getParentStop());
            stmt.execute();
        }
        stmt.close();
        this.connectionInstance.commit();

    }

}
