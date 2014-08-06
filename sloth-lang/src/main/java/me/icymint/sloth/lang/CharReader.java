package me.icymint.sloth.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;

@FunctionalInterface
public interface CharReader {
	public static CharReader newReader(InputStream input, Charset cs) {
		return newReader(new InputStreamReader(input, cs));
	}

	public static CharReader newReader(java.io.Reader r) {
		return r::read;
	}

	public static CharReader newReader(String str) {
		return newReader(new StringReader(str));
	}

	int read(char[] cbuf) throws IOException;
}
