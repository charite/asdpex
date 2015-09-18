/**
 * 
 */
package de.charite.compbio.hg38altlociselector.io.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import de.charite.compbio.hg38altlociselector.data.NCBIgffAlignmentMatch;
import de.charite.compbio.hg38altlociselector.data.NCBIgffAlignmentMatch.NCBIgffAlignmentMatchBuilder;
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

	public ImmutableList<NCBIgffAlignmentMatch> parse() {
		ImmutableList.Builder<NCBIgffAlignmentMatch> result = new ImmutableList.Builder<NCBIgffAlignmentMatch>();
		BufferedReader reader = null;
		String line;
		try {
			reader = IOUtil.getBufferedReaderFromFileName(this.gffFile);
			while ((line = reader.readLine()) != null) {
				try {
					if (line.startsWith("#"))
						continue;
					else {
						createBuilderFromLine(line, result);
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

		return result.build();
	}

	private void createBuilderFromLine(String line, Builder<NCBIgffAlignmentMatch> builder)
			throws NCBIgffAlignmentInfoParseException {
		String[] fields = line.split("\t");
		if (fields.length != this.NFIELDS) {
			String error = String.format(
					"Malformed line in NCBI alts_accessions_xxx file:\n%s\nExpected %d fields but there were %d", line,
					NFIELDS, fields.length);
			throw new NCBIgffAlignmentInfoParseException(error);
		}

		fields = fields[ATTRIBUTES].split(";");
		for (String att : fields) {
			if (att.startsWith("Gap=")) {
				feedBuilderWithMatches(builder, att.substring(4));
				break;
			}
		}
	}

	/**
	 * Feed the provided builder with the matches extracted from
	 * 
	 * @param builder
	 * @param elements
	 * @throws NCBIgffAlignmentInfoParseException
	 */
	private void feedBuilderWithMatches(Builder<NCBIgffAlignmentMatch> builder, String elements)
			throws NCBIgffAlignmentInfoParseException {

		NCBIgffAlignmentMatchBuilder matchBuilder = new NCBIgffAlignmentMatchBuilder();
		String[] fields = elements.split(" ");
		for (String elem : fields) {
			int length = Integer.parseInt(elem.substring(1));
			switch (elem.charAt(0)) {
			case 'M':
				// System.out.println(
				// String.format("add ref - %d\t alt - %d\tlength - %d", this.start_ref, this.start_alt, length));
				matchBuilder.refStart(this.start_ref);
				matchBuilder.altStart(this.start_alt);
				matchBuilder.length(length);
				builder.add(matchBuilder.build());
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
