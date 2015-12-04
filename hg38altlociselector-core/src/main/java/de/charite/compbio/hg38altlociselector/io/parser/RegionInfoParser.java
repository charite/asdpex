/**
 * 
 */
package de.charite.compbio.hg38altlociselector.io.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.hg38altlociselector.data.RegionInfo;
import de.charite.compbio.hg38altlociselector.data.RegionInfo.RegionInfoBuilder;
import de.charite.compbio.hg38altlociselector.exceptions.AccessionInfoParseException;
import de.charite.compbio.hg38altlociselector.util.IOUtil;

/**
 * Parser for the NCBI genomic_regions_definitions.txt
 *
 * @author Marten Jäger <marten.jaeger@charite.de>
 *
 */
public class RegionInfoParser {

	private File file;

	/**
	 * Number of tab-separated fields in then NCBI genomic_regions_definitions.txt file
	 */
	private static final int NFIELDS = 4;

	/**
	 * Dummy to prevent from using
	 */
	@SuppressWarnings("unused")
	private RegionInfoParser() {
	}

	/**
	 * 
	 * @param filepath
	 */
	public RegionInfoParser(String filepath) {
		this.file = new File(filepath);
	}

	/**
	 * Parse the file with the {@link RegionInfo}s.
	 * 
	 * @return an immutable map with the 'region_name' column ids as keys.
	 */
	public ImmutableMap<String, RegionInfo> parse() {
		ImmutableMap.Builder<String, RegionInfo> result = new ImmutableMap.Builder<String, RegionInfo>();
		BufferedReader reader = null;
		// reader = this.open();
		String line;
		try {
			reader = IOUtil.getBufferedReaderFromFileName(this.file);
			while ((line = reader.readLine()) != null) {
				try {
					if (line.startsWith("#"))
						continue;
					RegionInfoBuilder builder = createBuilderFromLine(line);
					RegionInfo info = builder.build();
					result.put(info.getRegionName(), info);
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

	private RegionInfoBuilder createBuilderFromLine(String line) throws AccessionInfoParseException {
		RegionInfoBuilder builder = new RegionInfoBuilder();
		String[] fields = line.split("\t");
		if (fields.length != RegionInfoParser.NFIELDS) {
			String error = String.format(
					"Malformed line in NCBI genomic_regions_definitions.txt file:\n%s\nExpected %d fields but there were %d",
					line, NFIELDS, fields.length);
			throw new AccessionInfoParseException(error);
		}
		builder.regionName(fields[0]);
		builder.chromosome(fields[1]);
		try {
			builder.start(Integer.parseInt(fields[2]));
		} catch (NumberFormatException e) {
			throw new AccessionInfoParseException("Failed to parse Integer from start field entry: " + fields[2]);
		}
		try {
			builder.stop(Integer.parseInt(fields[3]));
		} catch (NumberFormatException e) {
			throw new AccessionInfoParseException("Failed to parse Integer from stop field entry: " + fields[3]);
		}

		return builder;
	}
}
