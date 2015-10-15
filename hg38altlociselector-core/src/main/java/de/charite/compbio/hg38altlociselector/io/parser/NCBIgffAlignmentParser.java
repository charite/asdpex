/**
 * 
 */
package de.charite.compbio.hg38altlociselector.io.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import de.charite.compbio.hg38altlociselector.data.NCBIgffAlignment;
import de.charite.compbio.hg38altlociselector.data.NCBIgffAlignmentElement;
import de.charite.compbio.hg38altlociselector.data.NCBIgffAlignmentElement.NCBIgffAlignmentElementBuilder;
import de.charite.compbio.hg38altlociselector.data.NCBIgffAlignmentElementType;
import de.charite.compbio.hg38altlociselector.exceptions.NCBIgffAlignmentInfoParseException;
import de.charite.compbio.hg38altlociselector.util.IOUtil;

/**
 * Parses the NCBI alignment files for the alt loci These files only contain a single information line
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class NCBIgffAlignmentParser {

	private static final int START = 3;
	private static final int END = 4;
	private static final int ATTRIBUTES = 8;
	private static final int NFIELDS = 9;

	private File gffFile;
	private int start_ref = 0;
	private int start_alt = 0;

	/**
	 * hidden empty constructor
	 */
	private NCBIgffAlignmentParser() {
	}

	public NCBIgffAlignmentParser(String filename) {
		this(new File(filename));
	}

	public NCBIgffAlignmentParser(File file) {
		this.gffFile = file;
	}

	public ImmutableList<NCBIgffAlignment> parse() {
		ImmutableList.Builder<NCBIgffAlignment> alignments = new ImmutableList.Builder<NCBIgffAlignment>();
		BufferedReader reader = null;
		NCBIgffAlignment alignment;
		String line;
		try {
			reader = IOUtil.getBufferedReaderFromFileName(this.gffFile);
			while ((line = reader.readLine()) != null) {
				try {
					if (line.startsWith("#"))
						continue;
					else {
						alignment = createAlignmentFromLine(line);
						postprocess(alignment, alignments);
					}
				} catch (NCBIgffAlignmentInfoParseException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// LOGGER.error("failed to read line from file: " + this.file.getAbsolutePath());
			e.printStackTrace();
		}
		IOUtil.close(reader);

		return alignments.build();
	}

	/**
	 * Processes the alignment found in the gff line info field.
	 * Manly the corrupting large insertions end deletions are processed and the alignment is split into two separate alignments. 
	 * @param alignment - the alignment to be processed.
	 * @param alignments - the ListBuilder where the checked and/or processed alignments are put
	 */
	private void postprocess(NCBIgffAlignment alignment, Builder<NCBIgffAlignment> alignments) {
		alignments.add(alignment);
		NCBIgffAlignmentElement previousElement = null;
		for(NCBIgffAlignmentElement element : alignment.getElements()){
			if( previousElement != null){
				if(previousElement.getType() == NCBIgffAlignmentElementType.DELETION 
						&& previousElement.getLength() > 10000 
						&& element.getType() == NCBIgffAlignmentElementType.INSERTION
						&& element.getLength() > 10000){
					System.err.println("We have to split this alignment: " + alignment.getAltId());
				}
				if(previousElement.getType() == NCBIgffAlignmentElementType.INSERTION 
						&& previousElement.getLength() > 10000 
						&& element.getType() == NCBIgffAlignmentElementType.DELETION
						&& element.getLength() > 10000){
					System.err.println("We have to split this alignment: " + alignment.getAltId());
				}
			}
			previousElement = element;
		}
	}

	private NCBIgffAlignment createAlignmentFromLine(String line)
			throws NCBIgffAlignmentInfoParseException {
		String refId = null;
		int refStart = 0;
		int refStop = 0;
		String altId = null;
		int altStart = 0;
		int altStop = 0;
		boolean altStrand = true;
		ImmutableList.Builder<NCBIgffAlignmentElement> elements = new ImmutableList.Builder<NCBIgffAlignmentElement>();
		
		String[] fields = line.split("\t");
		if (fields.length != this.NFIELDS) {
			String error = String.format(
					"Malformed line in NCBI alts_accessions_xxx file:\n%s\nExpected %d fields but there were %d", line,
					NFIELDS, fields.length);
			throw new NCBIgffAlignmentInfoParseException(error);
		}
		
		refId = fields[0];
		try{
			refStart = Integer.parseInt(fields[3]);
		}catch(NumberFormatException e){ 
			throw new NCBIgffAlignmentInfoParseException(e.toString());
		}
		try{
			refStop = Integer.parseInt(fields[4]);
		}catch(NumberFormatException e){ 
			throw new NCBIgffAlignmentInfoParseException(e.toString());
		}



		fields = fields[ATTRIBUTES].split(";");
		for (String att : fields) {
			if(att.startsWith("Target=")){
				String[] subfields = att.substring(7).split(" ");
				altId = subfields[0];
				try{
					altStart = Integer.parseInt(subfields[1]);
				}catch(NumberFormatException e){ 
					throw new NCBIgffAlignmentInfoParseException(e.toString());
				}
				try{
					altStop = Integer.parseInt(subfields[2]);
				}catch(NumberFormatException e){ 
					throw new NCBIgffAlignmentInfoParseException(e.toString());
				}
				altStrand = subfields[3].equals("+") ? true: false; 
			}
				
			if (att.startsWith("Gap=")) {
				feedBuilderWithMatches(elements, att.substring(4));
				break;
			}
		}
//		System.out.println(elements.build().size());
		return(new NCBIgffAlignment( refId, altId, refStart, refStop,altStart,altStop, altStrand,elements.build()));
	}

	/**
	 * Feed the provided builder with the matches extracted from
	 * 
	 * @param builder
	 * @param elements
	 * @throws NCBIgffAlignmentInfoParseException
	 */
	private void feedBuilderWithMatches(Builder<NCBIgffAlignmentElement> builder, String elements)
			throws NCBIgffAlignmentInfoParseException {

		NCBIgffAlignmentElementBuilder elementBuilder = new NCBIgffAlignmentElementBuilder();
		String[] fields = elements.split(" ");
		for (String elem : fields) {
			int length = Integer.parseInt(elem.substring(1));
			
			elementBuilder.refStart(this.start_ref);
			elementBuilder.altStart(this.start_alt);
			elementBuilder.length(length);
			elementBuilder.type(elem.charAt(0));
			builder.add(elementBuilder.build());
			// and adapt length	
			switch (elem.charAt(0)) {
				case 'M':
					this.start_alt += length;
					this.start_ref += length;
					break;
				case 'I':
					this.start_alt += length;
					break;
				case 'D':
					this.start_ref += length;
					break;
				default:
					throw new NCBIgffAlignmentInfoParseException("unknown alignment block description: " + elem);
			}
		}

	}

}
