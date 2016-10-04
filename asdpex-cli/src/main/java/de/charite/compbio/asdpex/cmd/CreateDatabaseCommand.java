/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.cli.ParseException;

import com.google.common.collect.Lists;

import de.charite.compbio.asdpex.db.DatabaseManger;
import de.charite.compbio.asdpex.exceptions.AltLociSelectorException;
import de.charite.compbio.asdpex.io.parser.AccessionInfoParser;
import de.charite.compbio.asdpex.io.parser.AltScaffoldPlacementParser;
import de.charite.compbio.asdpex.io.parser.RegionInfoParser;
import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

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
        if (!options.getDataPath().equals(""))
            createAlternativeScaffoldTables(dbman);
        if (options.getAltlociVcf() != null)
            createAsdpTable(dbman);

    }

    private void createAsdpTable(DatabaseManger dbman) {
        // add the additional ASDP table
        dbman.addAsdpTable();
        //
        ArrayList<VariantContext> variantList = Lists
                .newArrayList(new VCFFileReader(new File(this.options.getAltlociVcf())).iterator());
        try {
            dbman.uploadAsdp(variantList);
            System.out.println("[INFO] Updated ASDPs");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createAlternativeScaffoldTables(DatabaseManger dbman) {
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
