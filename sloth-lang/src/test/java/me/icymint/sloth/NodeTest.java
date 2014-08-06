package me.icymint.sloth;

import org.junit.Test;

public class NodeTest {
	@Test
	public void test() {
		findPattern("==");
		findPattern("+");
		findPattern("++");
	}

	private void findPattern(String p) {
		System.out.println("Type of <" + p + "> is " + Type.typeOf(p));
	}
}
