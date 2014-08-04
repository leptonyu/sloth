package me.icymint.sloth.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;

@FunctionalInterface
public interface Reader<V> {
	public static Reader<Character> newReader(InputStream input, Charset cs) {
		return newReader(new InputStreamReader(input, cs));
	}

	public static Reader<Character> newReader(java.io.Reader r) {
		return size -> {
			char[] cbuf = new char[size];
			int mx = r.read(cbuf);
			if (mx >= 0) {
				Character[] v = new Character[mx];
				for (int i = 0; i < mx; i++) {
					v[i] = cbuf[i];
				}
				return v;
			} else {
				return null;
			}
		};
	}

	public static Reader<Character> newReader(String str) {
		return newReader(new StringReader(str));
	}

	default V read() throws IOException {
		V[] vs = read(1);
		return (vs != null && vs.length == 1) ? vs[0] : null;
	}

	V[] read(int size) throws IOException;
}
