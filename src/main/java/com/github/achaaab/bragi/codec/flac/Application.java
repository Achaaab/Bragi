package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FLAC METADATA_BLOCK_APPLICATION
 *
 * <a href="https://xiph.org/flac/format.html#metadata_block_application">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class Application extends MetadataBlockData {

	private static final Map<Integer, String> KNOWN_APPLICATION_NAMES;

	static {

		// they are from https://xiph.org/flac/id.html

		KNOWN_APPLICATION_NAMES = new HashMap<>();

		KNOWN_APPLICATION_NAMES.put(0x41544348, "FlacFile");
		KNOWN_APPLICATION_NAMES.put(0x42534F4C, "beSolo");
		KNOWN_APPLICATION_NAMES.put(0x42554753, "Bugs Player");
		KNOWN_APPLICATION_NAMES.put(0x43756573, "GoldWave cue points");
		KNOWN_APPLICATION_NAMES.put(0x46696361, "CUE Splitter");
		KNOWN_APPLICATION_NAMES.put(0x46746F6C, "flac-tools");
		KNOWN_APPLICATION_NAMES.put(0x4D4F5442, "MOTB MetaCzar");
		KNOWN_APPLICATION_NAMES.put(0x4D505345, "MP3 Stream Editor");
		KNOWN_APPLICATION_NAMES.put(0x4D754D4C, "MusicML: Music Metadata Language");
		KNOWN_APPLICATION_NAMES.put(0x52494646, "Sound Devices RIFF chunk storage");
		KNOWN_APPLICATION_NAMES.put(0x5346464C, "Sound Font FLAC");
		KNOWN_APPLICATION_NAMES.put(0x534F4E59, "Sony Creative Software");
		KNOWN_APPLICATION_NAMES.put(0x5351455A, "flacsqueeze");
		KNOWN_APPLICATION_NAMES.put(0x54745776, "TwistedWave");
		KNOWN_APPLICATION_NAMES.put(0x55495453, "UITS Embedding tools");
		KNOWN_APPLICATION_NAMES.put(0x61696666, "FLAC AIFF chunk storage");
		KNOWN_APPLICATION_NAMES.put(0x696D6167, "flac-image");
		KNOWN_APPLICATION_NAMES.put(0x7065656D, "Parseable Embedded Extensible Metadata");
		KNOWN_APPLICATION_NAMES.put(0x71667374, "QFLAC Studio");
		KNOWN_APPLICATION_NAMES.put(0x72696666, "FLAC RIFF chunk storage");
		KNOWN_APPLICATION_NAMES.put(0x74756E65, "TagTuner");
		KNOWN_APPLICATION_NAMES.put(0x78626174, "XBAT");
		KNOWN_APPLICATION_NAMES.put(0x786D6364, "xmcd");
	}

	private final int registeredId;
	private final byte[] data;

	private final String name;

	/**
	 * Decodes an APPLICATION metadata block from the given bit input stream.
	 *
	 * @param input  bit input stream to decode
	 * @param length length of this metadata block data
	 * @throws IOException I/O exception while decoding a APPLICATION metadata block
	 */
	Application(BitInputStream input, int length) throws IOException {

		registeredId = input.readUnsignedInteger(32);
		data = input.readBytes(length - 4);

		name = KNOWN_APPLICATION_NAMES.get(registeredId);
	}

	/**
	 * @return registered id of the application
	 */
	public int registeredId() {
		return registeredId;
	}

	/**
	 * @return application data
	 */
	public byte[] data() {
		return data;
	}

	/**
	 * @return name of the application, {@code null} if the registered id is unknown
	 */
	public String name() {
		return name;
	}
}