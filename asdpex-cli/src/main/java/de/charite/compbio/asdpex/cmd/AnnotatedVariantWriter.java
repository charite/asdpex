package de.charite.compbio.asdpex.cmd;

import java.io.IOException;
import java.util.EnumSet;

import de.charite.compbio.asdpex.Hg38altLociSeletorOptions;
import de.charite.compbio.asdpex.exceptions.AnnotationException;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Writer for the annotated varaints.
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 */
public class AnnotatedVariantWriter {
    /** writer for annotated VariantContext objects */
    private final VariantContextWriter out;

    public AnnotatedVariantWriter(VCFFileReader reader, ReferenceSequenceFile refFile,
            Hg38altLociSeletorOptions options) {
        VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
                .setReferenceDictionary(refFile.getSequenceDictionary())
                .setOptions(EnumSet.of(Options.INDEX_ON_THE_FLY)).setOutputFile(options.getOutputVcf());
        if (refFile.getSequenceDictionary() == null)
            builder.unsetOption(Options.INDEX_ON_THE_FLY);
        this.out = builder.build();

        VCFHeader header = new VCFHeader(reader.getFileHeader());
        // header.setSequenceDictionary(refFile.getSequenceDictionary());
        header.addMetaDataLine(new VCFHeaderLine("hg38altLociSelectorVersion", options.VERSION));
        header.addMetaDataLine(new VCFHeaderLine("hg38altLociSelectorCommand", "TODO ..."));
        // TODO add line with Command line call
        header.addMetaDataLine(
                new VCFFilterHeaderLine(options.VCFASDP, "Filtered due to a more likely alternative scaffold"));
        header.addMetaDataLine(new VCFInfoHeaderLine(options.VCFALTLOCISTRING, VCFHeaderLineCount.A,
                VCFHeaderLineType.String, "most likely alternate scaffold id replacement"));
        header.addMetaDataLine(new VCFInfoHeaderLine(options.VCFALTLOCIGENOTYPE, VCFHeaderLineCount.A,
                VCFHeaderLineType.String, "most likely alternate scaffold replacement genotype"));
        this.out.writeHeader(header);
    }

    /**
     * Write out the given VariantContext with additional annotation.
     *
     * @throws AnnotationException
     *             when a problem with annotation occurs
     * @throws IOException
     *             when problem with I/O occurs
     */
    public void put(VariantContext vc) {
        out.add(vc);
    }

    /**
     * Close writer, free resources
     */
    public void close() {
        out.close();
    }

}
