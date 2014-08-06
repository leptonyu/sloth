package me.icymint.sloth.lang;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;

public class CMachineTest {

	@Test
	public void mTest() throws IOException {
		Machine<Object> ch = Machine.WORD_MACHINE;
		for (Value<Object> t : ch.load(
				CharReader.newReader(
						getClass().getResourceAsStream("/sample.slh"),
						Charset.forName("UTF-8")), 1)) {
			if (Identifier.valueOf(t) == null)
				System.out.println(t.getLine() + ":"
						+ t.getValue().getClass().getSimpleName() + ":"
						+ t.getValue());
		}
	}
}
