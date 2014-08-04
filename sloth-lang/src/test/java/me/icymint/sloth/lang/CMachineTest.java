package me.icymint.sloth.lang;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;

public class CMachineTest {
	@Test
	public void mTest() throws IOException {
		Machine<String, Character> ch = Machine.WORD_MACHINE;
		for (String t : ch.load(Reader.newReader(getClass()
				.getResourceAsStream("/sample.slh"), Charset.forName("UTF-8")))) {
			System.out.println(t);
		}
	}
}
