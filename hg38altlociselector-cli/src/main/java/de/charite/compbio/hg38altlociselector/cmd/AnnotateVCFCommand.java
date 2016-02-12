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
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
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
 * <li>compare the overlaps and decide which allele (ref, alt.loci) is the most probable
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
     * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand# parseCommandLine(java.lang.String[])
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
     * @see de.charite.compbio.hg38altlociselector.cmd.AltLociSelectorCommand#run()
     */
    @Override
    public void run() throws AltLociSelectorException {
        // init Variant INPUT
        final VCFFileReader inputVCF = new VCFFileReader(new File(this.options.inputVcf));

        // init regions with alt. scaffolds
        ImmutableList<Region> regions = new RegionBuilder(options.getAltAccessionsPath(),
                options.getAltScaffoldPlacementPath(), options.getGenomicRegionsDefinitionsPath(),
                options.getChrAccessionsPath()).build();
                // System.out.println(regions.size());

        // init Reference FastA file e.g. Sequence Dictionary
        final ReferenceSequenceFile refFile = ReferenceSequenceFileFactory
                .getReferenceSequenceFile(new File(options.getReferencePath()));
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
                // System.out.println(chr + " : 1 - " +
                // refFile.getSequence("chr" + chr).length());
            } else {
                int i;
                for (i = 0; i < regionsOnChromosome.size(); i++) {
                    // write out block before region
                    if (i == 0) {
                        writeVariants(inputVCF, refFile, writerVCF, chr, 1,
                                regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart() - 1);
                        // System.out.println(chr + " : 1 - "
                        // +
                        // (regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart()
                        // - 1));
                    } else {
                        writeVariants(inputVCF, refFile, writerVCF, chr,
                                regions.get(regionsOnChromosome.get(i - 1)).getRegionInfo().getStop() + 1,
                                regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart() - 1);

                        // System.out.println(chr + " : "
                        // + (regions.get(regionsOnChromosome.get(i -
                        // 1)).getRegionInfo().getStop() + 1) + " - "
                        // +
                        // (regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart()
                        // - 1));
                        // TODO check with unittesting if the STart AND stop are
                        // including
                    }
                    // check and write block with alternative scaffold
                    ArrayList<VariantContext> refVariantList = Lists.newArrayList(inputVCF.query("chr" + chr,
                            regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart(),
                            regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStop()));

                    ArrayList<PairwiseVariantContextIntersect> intersectList = new ArrayList<>();
                    VCFFileReader locusVCF;
                    ArrayList<VariantContext> locusVariantList;
                    PairwiseVariantContextIntersect intersect;
                    for (MetaLocus locus : regions.get(regionsOnChromosome.get(i)).getLoci().values()) {
                        locusVCF = new VCFFileReader(new File(this.options.getTempFolder() + "/"
                                + locus.getAccessionInfo().createFastaIdentifier() + ".vcf.gz"));
                        locusVariantList = Lists.newArrayList(locusVCF.iterator());
                        intersect = VariantContextUtil.intersectVariantContext(refVariantList, locusVariantList);
                        // System.out.println(intersect.toString());
                        intersectList.add(intersect);
                        // intersectList.add(VariantContextUtil.intersectVariantContext(refVariantList,
                        // locusVariantList));
                    }
                    // System.out.println("LOCI: " +
                    // regions.get(regionsOnChromosome.get(i)).getLoci().size());
                    ArrayList<Integer> mostProbableAlleles = VariantContextUtil
                            .getMostProbableAlternativeScaffolds(intersectList);
                    // System.out.println("Intersect: " + intersectList.size());
                    if (mostProbableAlleles.size() > 0) { // at least one alt.
                                                          // scaffold identified
                        if (mostProbableAlleles.size() == 2
                                && mostProbableAlleles.get(0) == mostProbableAlleles.get(1)) {
                            // System.out.println("Index: " +
                            // mostProbableAlleles.get(0));
                            // System.out.println(regions.get(regionsOnChromosome.get(i)).getLoci().size());
                            writeModVariants(inputVCF, refFile, writerVCF, chr,
                                    regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart(),
                                    regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStop(),
                                    (MetaLocus) regions.get(regionsOnChromosome.get(i)).getLoci().values()
                                            .toArray()[mostProbableAlleles.get(0)],
                                    GenotypeType.HOM_VAR);
                        } else {
                            writeModVariants(inputVCF, refFile, writerVCF, chr,
                                    regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart(),
                                    regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStop(),
                                    (MetaLocus) regions.get(regionsOnChromosome.get(i)).getLoci().values()
                                            .toArray()[mostProbableAlleles.get(0)],
                                    GenotypeType.HET);
                            // System.out.println(chr + " : "
                            // +
                            // regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart()
                            // + " - "
                            // +
                            // regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStop());
                        }
                    } else { // no alt. scaffold identified
                        writeVariants(inputVCF, refFile, writerVCF, chr,
                                regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart(),
                                regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStop());
                        // System.out.println(
                        // chr + " : " +
                        // regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStart()
                        // + " - "
                        // +
                        // regions.get(regionsOnChromosome.get(i)).getRegionInfo().getStop());
                    }
                }
                // write final block up to the end of the chromosome
                writeVariants(inputVCF, refFile, writerVCF, chr,
                        regions.get(regionsOnChromosome.get(i - 1)).getRegionInfo().getStop() + 1,
                        refFile.getSequence("chr" + chr).length());
                        // System.out.println(
                        // chr + " : " + (regions.get(regionsOnChromosome.get(i
                        // - 1)).getRegionInfo().getStop() + 1)
                        // + " - " + refFile.getSequence("chr" + chr).length());

                // System.out.println(chr + " - " + regionsOnChromosome);
                // System.exit(0);
            }
        }

        // close the variant writer
        writerVCF.close();

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

    /**
     * Writes the Variants out
     * 
     * @param reader
     * @param refFile
     * @param writer
     * @param chr
     */
    private void writeModVariants(VCFFileReader reader, ReferenceSequenceFile refFile, AnnotatedVariantWriter writer,
            String chr, int start, int stop, MetaLocus locus, GenotypeType type) {
        // CloseableIterator<VariantContext> currentVariants =
        // reader.query("chr" + chr, start, stop);
        // while (currentVariants.hasNext()) {
        // writer.put(currentVariants.next());
        // }
        // return;

        // System.out.println(locus);
        // write block before alt scaffold starts
        writeVariants(reader, refFile, writer, chr, start, locus.getPlacementInfo().getParentStart() - 1);
        // System.out.println(chr + " : " + start + " - " +
        // (locus.getPlacementInfo().getParentStart() - 1));
        // write block covering the alt. scaffold
        CloseableIterator<VariantContext> currentVariants = reader.query("chr" + chr,
                locus.getPlacementInfo().getParentStart(), locus.getPlacementInfo().getParentStop());

        // System.out.println(chr + " : " +
        // locus.getPlacementInfo().getParentStart() + " - "
        // + locus.getPlacementInfo().getParentStop());
        VariantContextBuilder builder;
        VariantContext curVC;
        while (currentVariants.hasNext()) {
            builder = new VariantContextBuilder(currentVariants.next());
            builder.filter(options.VCFALTLOCISTRING);
            builder.attribute(options.VCFALTLOCISTRING, locus.getAccessionInfo().createFastaIdentifier());
            builder.attribute(options.VCFALTLOCIGENOTYPE, type.toString());
            curVC = builder.make();
            writer.put(curVC);
        }
        // write block after alt scaffold ends
        writeVariants(reader, refFile, writer, chr, locus.getPlacementInfo().getParentStop() + 1, stop);
        // System.out.println(chr + " : " +
        // (locus.getPlacementInfo().getParentStop() + 1) + " - " + stop);
    }

}
