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
import de.charite.compbio.hg38altlociselector.util.VariantContextUtil;
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
		// alt. loci info parsing

		VCFFileReader inputVCF = new VCFFileReader(new File(this.options.inputVcf));

		ImmutableList<Region> regions = new RegionBuilder(options.altAccessionsPath, options.altScaffoldPlacementPath,
				options.genomicRegionsDefinitionsPath, options.chrAccessionsPath).build();
		System.out.println(regions.size());
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
				// intersectList.add(VariantContextUtil.intersectVariantContext(refVariantList, locusVariantList));
			}
			// System.exit(0);
			c += region.getLoci().size();
		}
		System.out.println("total of loci: " + c);

		// ------------------------------------------------------------------------------------- //

		// ImmutableList<AlternativeLocus> loci = new AlternativeLociBuilder(options.altAccessionsPath,
		// options.altScaffoldPlacementPath, options.genomicRegionsDefinitionsPath).build();

		// VariantContextComparator variantContextComparator = null;
		// VCFFileReader reader = new VCFFileReader(new File(this.options.inputVcf));
		// // System.out.println(reader.getFileHeader().toString());
		// VCFFileReader altLoci = new VCFFileReader(new File(this.options.altlociVcf));
		// final Collection<CloseableIterator<VariantContext>> iteratorCollection = new
		// ArrayList<CloseableIterator<VariantContext>>(
		// 2);
		// iteratorCollection.add(reader.iterator());
		// iteratorCollection.add(altLoci.iterator());
		// VCFHeader header = reader.getFileHeader();
		// header.addMetaDataLine(new VCFContigHeaderLine("<assembly=hg38>", VCFHeaderVersion.VCF4_1, "chr1", 0));
		// variantContextComparator = reader.getFileHeader().getVCFRecordComparator();
		// System.out.println(reader.getFileHeader().toString());
		// // if
		// // (!variantContextComparator.isCompatible(reader.getFileHeader().getContigLines()))
		// // {
		// // System.err.println("sample VCF file not compatible with comparator");
		// // }
		//
		// final MergingIterator<VariantContext> mergingIterator = new MergingIterator<VariantContext>(
		// variantContextComparator, iteratorCollection);

	}

}
