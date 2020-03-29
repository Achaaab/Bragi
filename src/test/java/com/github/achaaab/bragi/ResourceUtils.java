package com.github.achaaab.bragi;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.ClassLoader.getSystemResource;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class ResourceUtils {

	/**
	 * @param name name of the resource
	 * @return path to the named resource
	 */
	public static Path getPath(String name) {

		var url = getSystemResource(name);

		try {
			return Paths.get(url.toURI());
		} catch (URISyntaxException cause) {
			throw new Error(cause);
		}
	}
}