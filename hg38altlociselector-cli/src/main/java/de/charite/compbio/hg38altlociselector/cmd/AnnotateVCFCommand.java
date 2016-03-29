/**
 * 
 */
package de.charite.compbio.hg38altlociselector.cmd;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.cli.ParseException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.charite.compbio.hg38altlociselector.Hg38altLociSeletorOptions;
import de.charite.compbio.hg38altlociselector.data.AltScaffoldPlacementInfo;
import de.charite.compbio.hg38altlociselector.data.PairwiseVariantContextIntersect;
import de.charite.compbio.hg38altlociselector.data.Region;
import de.charite.compbio.hg38altlociselector.data.RegionBuilder;
import de.charite.compbio.hg38altlociselector.data.RegionInfo;
import de.charite.compbio.hg38altlociselector.db.DatabaseManger;
import de.charite.compbio.hg38altlociselector.exceptions.AltLociSelectorException;
import de.charite.compbio.hg38altlociselector.exceptions.CommandLineParsingException;
import de.charite.compbio.hg38altlociselector.exceptions.HelpRequestedException;
import de.charite.compbio.hg38altlociselector.util.VariantContextUtil;
import htsjdk.samtools.reference.ReferenceSequence;
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
        // DB Manger
        final DatabaseManger dbMan = new DatabaseManger(this.options.getSqlitePath());

        // init Variant INPUT
        final VCFFileReader inputVCF = new VCFFileReader(new File(this.options.getInputVcf()));

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

        // CloseableIterator<VariantContext> currentVariants;

        // TODO find a better way to perform region selection - maybe with the
        // database
        // final VCFFileReader locusVCF = new VCFFileReader(new
        // File(this.options.altlociVcf));
        // locusVCF.close();
        ArrayList<VariantContext> variantList = Lists
                .newArrayList(new VCFFileReader(new File(this.options.getAltlociVcf())).iterator());
        try {
            System.out.println("[INFO] number of known (SNV) ASDPs: " + dbMan.getTableSize("asdp"));
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + options.getSqlitePath());
            System.err.println("\tand get asdp table");
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("[INFO] Annotate regions:");
        System.out.println("0%       50%       100%");
        System.out.println("|.........|.........|");
        int c = 1;
        int limit = 0;
        // TODO we need a check if the VCF file contains a header or if we have to use the reference sequence
        ReferenceSequence contig;
        while ((contig = refFile.nextSequence()) != null) {
            // TODO should we really skip non toplevel contigs or add extra flag
            if (contig.getName().contains("_")) {
                System.out.println("[INFO] Skipping contig: " + contig.getName());
                continue;
            }

            // fetch regions on chromosome
            // ArrayList<Integer> regionsOnChromosome = new ArrayList<>();
            ImmutableList<RegionInfo> regionsOnChromosome = null;
            try {
                regionsOnChromosome = dbMan.getRegionNamesOnChromosome(contig.getName());
            } catch (SQLException e) {
                System.err.println("Failed to connect to database: " + options.getSqlitePath());
                System.err.println("\tand get regions for chromosome: " + contig.getName());
                e.printStackTrace();
                System.exit(0);
            }

            if (regionsOnChromosome.isEmpty()) { // now regions on chromosome
                System.out.println("[INFO] " + contig.getName() + " w/o region(s)");
                writeVariants(inputVCF, refFile, writerVCF, contig.getName(), 1, contig.length());

            } else {
                int i;
                int firstASDPonRegionPosition = 0;
                int lastASDPonRegionPosition = 0;
                int lastASDPonRegionPositionPrev = 0;
                for (i = 0; i < regionsOnChromosome.size(); i++) {
                    try {
                        firstASDPonRegionPosition = dbMan
                                .getRegionMinimumAsdpPosition(regionsOnChromosome.get(i).getRegionName());
                        lastASDPonRegionPosition = dbMan
                                .getRegionMaximumAsdpPosition(regionsOnChromosome.get(i).getRegionName());
                        if (i > 0)
                            lastASDPonRegionPositionPrev = dbMan
                                    .getRegionMaximumAsdpPosition(regionsOnChromosome.get(i - 1).getRegionName());
                    } catch (SQLException e1) {
                        System.err.println("Failed to connect to database: " + options.getSqlitePath());
                        System.err.println(
                                "\tand get ASDP position for region: " + regionsOnChromosome.get(i).getRegionName());
                        e1.printStackTrace();
                        System.exit(0);
                    }
                    if (firstASDPonRegionPosition == 0) {
                        System.out.println("[WARN] found no 'first' ASDP for region "
                                + regionsOnChromosome.get(i).getRegionName() + " set to region start.");
                        firstASDPonRegionPosition = regionsOnChromosome.get(i).getStart();
                    }
                    if (lastASDPonRegionPosition == 0) {
                        System.out.println("[WARN] found no 'last' ASDP for region "
                                + regionsOnChromosome.get(i).getRegionName() + " set to region stop.");
                        lastASDPonRegionPosition = regionsOnChromosome.get(i).getStop();
                    }
                    if (i > 0 && lastASDPonRegionPositionPrev == 0) {
                        System.out.println("[WARN] found no 'last' ASDP for region "
                                + regionsOnChromosome.get(i - 1).getRegionName() + " set to region stop.");
                        lastASDPonRegionPositionPrev = regionsOnChromosome.get(i - 1).getStop();
                    }
                    // System.out.println("region: " + regionsOnChromosome.get(i).getRegionName());
                    // System.out.println("\trange region:\t" + regionsOnChromosome.get(i).getStart() + " - "
                    // + regionsOnChromosome.get(i).getStop());
                    // System.out
                    // .println("\trange asdps:\t" + firstASDPonRegionPosition + " - " + lastASDPonRegionPosition);

                    // write out block before region
                    if (i == 0) {
                        writeVariants(inputVCF, refFile, writerVCF, contig.getName(), 1, firstASDPonRegionPosition - 1);

                    } else {
                        writeVariants(inputVCF, refFile, writerVCF, contig.getName(), lastASDPonRegionPositionPrev + 1,
                                firstASDPonRegionPosition - 1);
                    }
                    // check and write block with alternative scaffold
                    ArrayList<VariantContext> refVariantList = Lists.newArrayList(
                            inputVCF.query(contig.getName(), firstASDPonRegionPosition, lastASDPonRegionPosition));

                    ArrayList<PairwiseVariantContextIntersect> intersectList = new ArrayList<>();
                    // ArrayList<VariantContext> variantList;
                    ArrayList<VariantContext> locusVariantList = new ArrayList<>();
                    PairwiseVariantContextIntersect intersect;

                    ImmutableList<AltScaffoldPlacementInfo> altScaffolds = null;
                    try {
                        altScaffolds = dbMan.getAltScaffoldPlacementInfos(regionsOnChromosome.get(i).getRegionName());
                    } catch (SQLException e) {
                        System.err.println("Failed to connect to database: " + options.getSqlitePath());
                        System.err.println(
                                "\tand fetch alt. scaffolds for region: " + regionsOnChromosome.get(i).getRegionName());
                        e.printStackTrace();
                        System.exit(0);
                    }
                    String fastaIdentifier = null;
                    for (AltScaffoldPlacementInfo info : altScaffolds) {

                        try {
                            fastaIdentifier = dbMan.getFastaIdentifier(info.getAltScafAcc());
                        } catch (SQLException e) {
                            System.err.println("Failed to connect to database: " + options.getSqlitePath());
                            System.err.println(
                                    "\tand generate FastA identifier for alt. scaffolds: " + info.getAltScafAcc());
                            e.printStackTrace();
                            System.exit(0);
                        }
                        if (100.0 * c++ / regions.size() > limit) {
                            limit += 5;
                            System.out.print("*");
                        }
                        for (VariantContext variantContext : variantList) {
                            if (variantContext.getAttribute("AL").equals(fastaIdentifier))
                                locusVariantList.add(variantContext);
                        }
                        intersect = VariantContextUtil.intersectVariantContext(refVariantList, locusVariantList);
                        intersectList.add(intersect);
                    }
                    ArrayList<Integer> mostProbableAlleles = VariantContextUtil
                            .getMostProbableAlternativeScaffolds(intersectList);
                    if (mostProbableAlleles.size() > 0) { // at least one alt.scaffold identified
                        try {
                            if (mostProbableAlleles.size() == 2
                                    && mostProbableAlleles.get(0) == mostProbableAlleles.get(1)) {
                                writeModVariants(inputVCF, refFile, writerVCF, contig.getName(),
                                        firstASDPonRegionPosition, lastASDPonRegionPosition,
                                        dbMan.getFastaIdentifier(
                                                altScaffolds.get(mostProbableAlleles.get(0)).getAltScafAcc()),
                                        GenotypeType.HOM_VAR, intersectList.get(mostProbableAlleles.get(0)));
                            } else {
                                writeModVariants(inputVCF, refFile, writerVCF, contig.getName(),
                                        firstASDPonRegionPosition, lastASDPonRegionPosition,
                                        dbMan.getFastaIdentifier(
                                                altScaffolds.get(mostProbableAlleles.get(0)).getAltScafAcc()),
                                        GenotypeType.HET, intersectList.get(mostProbableAlleles.get(0)));
                            }
                        } catch (SQLException e) {
                            System.err.println("Failed to connect to database: " + options.getSqlitePath());
                            System.err.println("\tand generate FastA identifier for alt. scaffolds: "
                                    + altScaffolds.get(mostProbableAlleles.get(0)).getAltScafAcc());
                            e.printStackTrace();
                            System.exit(0);
                        }
                    } else { // no alt. scaffold identified
                        writeVariants(inputVCF, refFile, writerVCF, contig.getName(), firstASDPonRegionPosition,
                                lastASDPonRegionPosition);
                    }
                }
                // write final block up to the end of the chromosome
                writeVariants(inputVCF, refFile, writerVCF, contig.getName(), lastASDPonRegionPositionPrev + 1,
                        contig.length());
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

        CloseableIterator<VariantContext> currentVariants = reader.query(chr, start, stop);
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
     * @param pairwiseVariantContextIntersect
     */
    private void writeModVariants(VCFFileReader reader, ReferenceSequenceFile refFile, AnnotatedVariantWriter writer,
            String chr, int start, int stop, String altLocusID, GenotypeType type,
            PairwiseVariantContextIntersect pairwiseVariantContextIntersect) {

        // write block before alt scaffold starts up to the first ASDP in the alt scaffold
        writeVariants(reader, refFile, writer, chr, start,
                pairwiseVariantContextIntersect.getSet2SNVs().get(0).getStart() - 1);

        // write block covering the alt. scaffold ASDPs
        CloseableIterator<VariantContext> currentVariants = reader.query(chr,
                pairwiseVariantContextIntersect.getSet2SNVs().get(0).getStart(), pairwiseVariantContextIntersect
                        .getSet2SNVs().get(pairwiseVariantContextIntersect.getSet2SNVs().size() - 1).getStart());

        VariantContextBuilder builder;
        VariantContext curVC;
        while (currentVariants.hasNext()) {
            builder = new VariantContextBuilder(currentVariants.next());
            builder.filter(Hg38altLociSeletorOptions.VCFASDP);
            builder.attribute(Hg38altLociSeletorOptions.VCFALTLOCISTRING, altLocusID);
            builder.attribute(Hg38altLociSeletorOptions.VCFALTLOCIGENOTYPE, type.toString());
            curVC = builder.make();
            writer.put(curVC);
        }
        // write block after alt scaffold ASDPs ends
        writeVariants(reader, refFile, writer, chr, pairwiseVariantContextIntersect.getSet2SNVs()
                .get(pairwiseVariantContextIntersect.getSet2SNVs().size() - 1).getStart() + 1, stop);

    }

}
