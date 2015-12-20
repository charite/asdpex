/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.data.MetaLocus;
import de.charite.compbio.hg38altlociselector.data.PairwiseVariantContextIntersect;
import de.charite.compbio.hg38altlociselector.data.Region;
import de.charite.compbio.hg38altlociselector.data.RegionBuilder;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.reference.TopLevelChromosomes;
import de.charite.compbio.hg38altlociselector.util.VariantContextUtil;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

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
        // init Variant INPUT
        final VCFFileReader inputVCF = new VCFFileReader(new File(this.options.inputVcf));

        // init regions with alt. scaffolds
        ImmutableList<Region> regions = new RegionBuilder(options.altAccessionsPath, options.altScaffoldPlacementPath,
                options.genomicRegionsDefinitionsPath, options.chrAccessionsPath).build();
                // System.out.println(regions.size());

        // init Reference FastA file e.g. Sequence Dictionary
        final ReferenceSequenceFile refFile = ReferenceSequenceFileFactory
                .getReferenceSequenceFile(new File(options.referencePath));
        if (!refFile.isIndexed()) {
            System.err.println("Reference fasta file is not indexed");
            System.exit(1); // TODO throw exception
        }

        // init Variant file writer
        // final VariantContextWriter writerVCF = new
        // VariantContextWriterBuilder()
        // .setReferenceDictionary(refFile.getSequenceDictionary())
        // .setOptions(EnumSet.of(Options.INDEX_ON_THE_FLY)).setOutputFile(options.outputVcf).build();
        final AnnotatedVariantWriter writerVCF = new AnnotatedVariantWriter(inputVCF, refFile, options);

        // start writing

        int regionIdx = 0;
        // int chrStart;

        CloseableIterator<VariantContext> currentVariants;

        // currentVariants = inputVCF.iterator();
        // while (currentVariants.hasNext()) {
        // try {
        // writerVCF.put(currentVariants.next());
        // } catch (TribbleException e) {
        // e.printStackTrace();
        // }
        // // writerVCF.put(currentVariants.next());
        // }
        // writerVCF.close();
        // System.out.println("finished");
        // System.exit(0);
        // TODO find a better way to perform region selection - maybe with the
        // database
        for (String chr : TopLevelChromosomes.getInstance().getToplevel()) {
            // chrStart = 0;
            ArrayList<Integer> regionsOnChromosome = new ArrayList<>();
            for (int i = 0; i < regions.size(); i++) {
                if (regions.get(i).getRegionInfo().getChromosomeInfo().getChromosome().compareTo(chr) == 0)
                    regionsOnChromosome.add(i);
            }
            if (regionsOnChromosome.isEmpty()) {
                System.out.println(chr + " w/o region(s)");
                writeVariants(inputVCF, refFile, writerVCF, chr, 1, refFile.getSequence("chr" + chr).length());
            } else {
                // writeVariants(inputVCF, refFile, writerVCF, chr, 1,
                // regions.get(regionsOnChromosome.get(0)).getRegionInfo().getStart()
                // - 1);
                int i;
                for (i = 0; i < regionsOnChromosome.size(); i++) {
                    // write out block before region
                    if (i == 0)
                        writeVariants(inputVCF, refFile, writerVCF, chr, 1,
                                regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart() - 1);
                    else
                        writeVariants(inputVCF, refFile, writerVCF, chr,
                                regions.get(regionsOnChromosome.get(i - 1)).getRegionInfo().getStop() + 1,
                                regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart() - 1);
                    // TODO check with unittesting if the STart AND stop are
                    // including

                }
                // write final block up to the end of the chromosome
                writeVariants(inputVCF, refFile, writerVCF, chr,
                        regions.get(regionsOnChromosome.get(i - 1)).getRegionInfo().getStop() + 1,
                        refFile.getSequence("chr" + chr).length());

                System.out.println(chr + " - " + regionsOnChromosome);
            }
        }

        // close the variant writer
        writerVCF.close();

        System.exit(0);

        int c = 0;
        for (Region region : regions) {
            if (region.getLoci() == null) {
                System.out.println("[INFO] skip region: " + region.getRegionInfo().getRegionName());
                continue;
            }
            System.out.println(
                    "[INFO] region: " + region.getRegionInfo().getRegionName() + ": " + region.getLoci().size());
            ArrayList<VariantContext> refVariantList = Lists
                    .newArrayList(inputVCF.query("chr" + region.getRegionInfo().getChromosomeInfo().getChromosome(),
                            region.getRegionInfo().getStart(), region.getRegionInfo().getStop()));

            ArrayList<PairwiseVariantContextIntersect> intersectList = new ArrayList<>();
            for (MetaLocus locus : region.getLoci().values()) {
                VCFFileReader locusVCF = new VCFFileReader(new File(
                        this.options.tempFolder + "/" + locus.getAccessionInfo().createFastaIdentifier() + ".vcf.gz"));
                ArrayList<VariantContext> locusVariantList = Lists.newArrayList(locusVCF.iterator());
                PairwiseVariantContextIntersect inter = VariantContextUtil.intersectVariantContext(refVariantList,
                        locusVariantList);
                System.out.println(inter.toString());
                // intersectList.add(VariantContextUtil.intersectVariantContext(refVariantList,
                // locusVariantList));
            }
            // System.exit(0);
            c += region.getLoci().size();
        }
        System.out.println("total of loci: " + c);

    }

    /**
     * Writes the Variants out
     * 
     * @param reader
     * @param refFile
     * @param writer
     * @param chr
     */
    private void writeVariants(VCFFileReader reader, ReferenceSequenceFile refFile, AnnotatedVariantWriter writer,
            String chr, int start, int stop) {

        CloseableIterator<VariantContext> currentVariants = reader.query("chr" + chr, start, stop);
        while (currentVariants.hasNext()) {
            writer.put(currentVariants.next());
        }
    }

}
