/**
 * 
 */
package de.charite.compbio.hg38altlociselector.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

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
                stmt.executeUpdate("DROP TABLE " + table);
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table successfully droped");
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

}
