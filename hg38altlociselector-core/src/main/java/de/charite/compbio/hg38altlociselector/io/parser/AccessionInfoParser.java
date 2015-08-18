/**
 * 
 */
package de.charite.compbio.hg38altlociselector.io.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.hg38altlociselector.data.AccessionInfo;
import de.charite.compbio.hg38altlociselector.data.AccessionInfo.AccessionInfoBuilder;
import de.charite.compbio.hg38altlociselector.exceptions.AccessionInfoParseException;
import de.charite.compbio.hg38altlociselector.util.IOUtil;

/**
 * 
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class AccessionInfoParser {

	private File file;
	/** the logger object to use */
	// private static final Logger LOGGER = LoggerFactory.getLogger(AccessionInfoParser.class);

	/**
	 * Number of tab-separated fields in then NCBI alts_accessions_xxx file
	 */
	private static final int NFIELDS = 5;

	/**
	 * 
	 */
	public AccessionInfoParser(String filepath) {
		this.file = new File(filepath);
	}

	private BufferedReader open() {
		try {
			return IOUtil.getBufferedReaderFromFileName(this.file);
		} catch (IOException e) {
			// LOGGER.error("failed to open the file: " + this.file.getAbsolutePath());
			e.printStackTrace();
		}
		return null;
	}

	public ImmutableList<AccessionInfo> parse() {
		ImmutableList.Builder<AccessionInfo> result = new ImmutableList.Builder<AccessionInfo>();
		BufferedReader reader;
		reader = this.open();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				try {
					AccessionInfoBuilder aiBuilder = createBuilderFromLine(line);
					result.add(aiBuilder.build());
				} catch (AccessionInfoParseException e) {
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

	private AccessionInfoBuilder createBuilderFromLine(String line) throws AccessionInfoParseException {
		AccessionInfoBuilder builder = new AccessionInfoBuilder();
		String[] fields = line.split("\t");
		if (fields.length != this.NFIELDS) {
			String error = String.format(
					"Malformed line in UCSC knownGene.txt file:\n%s\nExpected %d fields but there were %d", line,
					NFIELDS, fields.length);
			throw new AccessionInfoParseException(error);
		}
		builder.chromosome(fields[0]);
		builder.refseqAccessionVersion(fields[1]);
		try {
			builder.refseqGi(Integer.parseInt(fields[2]));
		} catch (NumberFormatException e) {
			throw new AccessionInfoParseException("Failed to parse Interger from Regseq gi field entry: " + fields[2]);
		}
		builder.genbankAccessionVersion(fields[3]);
		try {
			builder.genbankGi(Integer.parseInt(fields[4]));
		} catch (NumberFormatException e) {
			throw new AccessionInfoParseException("Failed to parse Interger from GenBank gi field entry: " + fields[4]);
		}

		return builder;
	}

}
