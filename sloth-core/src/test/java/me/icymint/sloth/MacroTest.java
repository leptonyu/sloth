package me.icymint.sloth;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jodd.util.StringTemplateParser;

import org.junit.Test;

public class MacroTest {

	@Test
	public void e2Test() throws IOException {
		Map<String, String> xx = new ConcurrentHashMap<>();
		xx.put("name", "Daniel");
		jodd.util.StringTemplateParser.MacroResolver mr = xx::get;
		StringTemplateParser stp = new StringTemplateParser();
		System.out.println(stp.parse("hello,${name}${x}!", mr));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			stp.parse("hello,${name}${x}!", mr);
		}
		System.out.println("2:" + (System.currentTimeMillis() - start) + "ms");
	}

	@Test
	public void e4Test() throws IOException {
		Map<String, String> xx = new ConcurrentHashMap<>();
		xx.put("name", "Daniel");
		MacroResolver mr = xx::get;
		System.out.println(Util.parse("hello,${name}${x}!", mr));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Util.parse("hello,${name}${x}!", mr);
		}
		System.out.println("4:" + (System.currentTimeMillis() - start) + "ms");
		System.out.println(Util.parse("hello,${name}${x}${xxx}\\${xxx}!", mr));
	}
}
