/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.sql.SQLException;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.db.DatabaseManger;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.io.parser.AccessionInfoParser;
import de.charite.compbio.hg38altlociselector.io.parser.AltScaffoldPlacementParser;
import de.charite.compbio.hg38altlociselector.io.parser.RegionInfoParser;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class CreateDatabaseCommand extends AltLociSelectorCommand {

    /**
     * @param args
     * @throws HelpRequestedException
     * @throws CommandLineParsingException
     */
    public CreateDatabaseCommand(String[] args) throws CommandLineParsingException, HelpRequestedException {
        super(args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand# parseCommandLine(java.lang.String[])
     */
    @Override
    protected Hg38altLociSeletorOptions parseCommandLine(String[] args)
            throws CommandLineParsingException, HelpRequestedException {
        try {
            return new CreateDatabaseCommandLineParser().parse(args);
        } catch (ParseException e) {
            throw new CommandLineParsingException("Could not parse the command line.", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand#run()
     */
    @Override
    public void run() throws AltLociSelectorException {
        // create database
        System.out.println("[INFO] Create database");
        if (options == null)
            System.err.println("[ERROR] option = null");
        DatabaseManger dbman = new DatabaseManger(options.getSqlitePath());
        // check files
        dbman.createDatabase();

        // accessions
        AccessionInfoParser aip;
        try {
            aip = new AccessionInfoParser(this.options.getAltAccessionsPath());
            dbman.uploadAccessionInfos(aip.parse());
            System.out.println("[INFO] Updated alt scaffold accessions");
            aip = new AccessionInfoParser(this.options.getChrAccessionsPath());
            dbman.uploadAccessionInfos(aip.parse());
            System.out.println("[INFO] Updated chromosome accessions");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // regions
        RegionInfoParser rip = new RegionInfoParser(this.options.getGenomicRegionsDefinitionsPath(),
                this.options.getChrAccessionsPath());
        System.out.println("[INFO] Updated regions");
        try {
            dbman.uploadRegionInfos(rip.parse());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // placement
        AltScaffoldPlacementParser aspp = new AltScaffoldPlacementParser(this.options.getAltScaffoldPlacementPath());
        try {
            dbman.uploadScaffoldPlacement(aspp.parse());
            System.out.println("[INFO] Updated alt scaffold placement");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
