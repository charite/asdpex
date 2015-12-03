/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.data.AlternativeLociBuilder;
import de.charite.compbio.hg38altlociselector.data.AlternativeLocus;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.MergingIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextComparator;
import htsjdk.variant.vcf.VCFContigHeaderLine;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderVersion;

/**
 * INPUT
 * <ol>
 * <li>Region definitions
 * <li>VCF(s) alt. loci
 * <li>VCF sample
 * </ol>
 * 
 * PROCESSING
 * <ol>
 * <li>select region
 * <li>pick all alt loci for this region
 * <li>get all variants, that are in this region
 * <li>compare the overlaps and decide which allele (ref, alt.loci) is the most
 * probable
 * </ol>
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AnnotateVCFCommand extends AltLociSelectorCommand {

    /**
     * @param args
     * @throws HelpRequestedException
     * @throws CommandLineParsingException
     */
    public AnnotateVCFCommand(String[] args) throws CommandLineParsingException, HelpRequestedException {
        super(args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand#
     * parseCommandLine(java.lang.String[])
     */
    @Override
    protected Hg38altLociSeletorOptions parseCommandLine(String[] args)
            throws CommandLineParsingException, HelpRequestedException {
        try {
            return new AnnotateVCFCommandLineParser().parse(args);
        } catch (ParseException e) {
            throw new CommandLineParsingException("Could not parse the command line.", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand#run()
     */
    @Override
    public void run() throws AltLociSelectorException {
        // alt. loci info parsing
        ImmutableList<AlternativeLocus> loci = new AlternativeLociBuilder(options.altAccessionsPath,
                options.altScaffoldPlacementPath, options.genomicRegionsDefinitionsPath).build();

        VariantContextComparator variantContextComparator = null;
        VCFFileReader reader = new VCFFileReader(new File(this.options.inputVcf));
        // System.out.println(reader.getFileHeader().toString());
        VCFFileReader altLoci = new VCFFileReader(new File(this.options.altlociVcf));
        final Collection<CloseableIterator<VariantContext>> iteratorCollection = new ArrayList<CloseableIterator<VariantContext>>(
                2);
        iteratorCollection.add(reader.iterator());
        iteratorCollection.add(altLoci.iterator());
        VCFHeader header = reader.getFileHeader();
        header.addMetaDataLine(new VCFContigHeaderLine("<assembly=hg38>", VCFHeaderVersion.VCF4_1, "chr1", 0));
        variantContextComparator = reader.getFileHeader().getVCFRecordComparator();
        System.out.println(reader.getFileHeader().toString());
        // if
        // (!variantContextComparator.isCompatible(reader.getFileHeader().getContigLines()))
        // {
        // System.err.println("sample VCF file not compatible with comparator");
        // }

        final MergingIterator<VariantContext> mergingIterator = new MergingIterator<VariantContext>(
                variantContextComparator, iteratorCollection);

    }

}
