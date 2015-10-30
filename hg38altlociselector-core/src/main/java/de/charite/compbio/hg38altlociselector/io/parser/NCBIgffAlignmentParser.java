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
		String line;
		try {
			reader = IOUtil.getBufferedReaderFromFileName(this.gffFile);
			while ((line = reader.readLine()) != null) {
				try {
					if (line.startsWith("#"))
						continue;
					else {
						createAlignmentFromLine(line, alignments);
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

	private void createAlignmentFromLine(String line, Builder<NCBIgffAlignment> alignments)
			throws NCBIgffAlignmentInfoParseException {
		String refId = null;
		int refStart = 0;
		int refStop = 0;
		String altId = null;
		int altStart = 0;
		int altStop = 0;
		boolean refStrand = true;
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
		try {
			refStart = Integer.parseInt(fields[3]);
		} catch (NumberFormatException e) {
			throw new NCBIgffAlignmentInfoParseException(e.toString());
		}
		try {
			refStop = Integer.parseInt(fields[4]);
		} catch (NumberFormatException e) {
			throw new NCBIgffAlignmentInfoParseException(e.toString());
		}

		refStrand = fields[6].equals("+") ? true : false;

		fields = fields[ATTRIBUTES].split(";");
		for (String att : fields) {
			if (att.startsWith("Target=")) {
				String[] subfields = att.substring(7).split(" ");
				altId = subfields[0];
				try {
					altStart = Integer.parseInt(subfields[1]);
				} catch (NumberFormatException e) {
					throw new NCBIgffAlignmentInfoParseException(e.toString());
				}
				try {
					altStop = Integer.parseInt(subfields[2]);
				} catch (NumberFormatException e) {
					throw new NCBIgffAlignmentInfoParseException(e.toString());
				}
				altStrand = subfields[3].equals("+") ? true : false;
			}

			if (att.startsWith("Gap=")) {
				feedBuilderWithMatches(elements, att.substring(4));
				break;
			}
		}
		// System.out.println("before collapse: " + elements.build().size());
		// collapse the Alignment elements
		elements = collapseElements(elements.build());
		// System.out.println("after collapse: " + elements.build().size());

		alignments.add(new NCBIgffAlignment(refId, altId, refStart, refStop, refStrand, altStart, altStop, altStrand,
				elements.build()));
	}

	/**
	 * Collapses {@link NCBIgffAlignmentElement}s. If there are contiuous Insertions/deletions/matches these elements
	 * will be condensed into a single one of the cummulative length.
	 * 
	 * @param elements
	 * @return
	 */
	private ImmutableList.Builder<NCBIgffAlignmentElement> collapseElements(
			ImmutableList<NCBIgffAlignmentElement> elements) {

		ImmutableList.Builder<NCBIgffAlignmentElement> collapsedElements = new ImmutableList.Builder<NCBIgffAlignmentElement>();
		if (elements.size() == 0)
			return collapsedElements;
		NCBIgffAlignmentElement prev_element = null;
		NCBIgffAlignmentElementBuilder matchBuilder;
		for (NCBIgffAlignmentElement cur_element : elements) {
			if (prev_element != null) {
				if (prev_element.getType() == cur_element.getType()) { // collapse continuous same types
					matchBuilder = new NCBIgffAlignmentElementBuilder();
					matchBuilder.refStart(prev_element.getRef_start());
					matchBuilder.altStart(prev_element.getAlt_start());
					matchBuilder.type(prev_element.getType());
					matchBuilder.length(prev_element.getLength() + cur_element.getLength());
					prev_element = matchBuilder.build();
				} else {
					collapsedElements.add(prev_element);
					prev_element = cur_element;
				}
			} else {
				prev_element = cur_element;
			}

		}
		collapsedElements.add(prev_element);
		return collapsedElements;
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

		NCBIgffAlignmentElementBuilder matchBuilder = new NCBIgffAlignmentElementBuilder();
		String[] fields = elements.split(" ");
		for (String elem : fields) {
			int length = Integer.parseInt(elem.substring(1));

			matchBuilder.refStart(this.start_ref);
			matchBuilder.altStart(this.start_alt);
			matchBuilder.length(length);
			//
			switch (elem.charAt(0)) {
			case 'M':
				this.start_alt += length;
				this.start_ref += length;
				matchBuilder.type(NCBIgffAlignmentElementType.MATCH);
				break;
			case 'I':
				this.start_alt += length;
				matchBuilder.type(NCBIgffAlignmentElementType.INSERTION);
				break;
			case 'D':
				this.start_ref += length;
				matchBuilder.type(NCBIgffAlignmentElementType.DELETION);
				break;
			default:
				throw new NCBIgffAlignmentInfoParseException("unknown alignment block description: " + elem);
			}
			builder.add(matchBuilder.build());
			// switch (elem.charAt(0)) {
			// case 'M':
			// // System.out.println(
			// // String.format("add ref - %d\t alt - %d\tlength - %d", this.start_ref, this.start_alt, length));
			// matchBuilder.refStart(this.start_ref);
			// matchBuilder.altStart(this.start_alt);
			// matchBuilder.length(length);
			// matchBuilder.type('M');
			// builder.add(matchBuilder.build());
			// this.start_alt += length;
			// this.start_ref += length;
			// break;
			// case 'I':
			// this.start_alt += length;
			// matchBuilder.type('M');
			// break;
			// case 'D':
			// this.start_ref += length;
			// break;
			// default:
			// throw new NCBIgffAlignmentInfoParseException("unknown alignment block description: " + elem);
			// }
		}

	}

}
