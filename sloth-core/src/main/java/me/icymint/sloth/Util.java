package me.icymint.sloth;

public class Util {
	public static String parse(String source, MacroResolver mr)
			throws IllegalArgumentException {
		return parseY(source, new StringBuilder(), mr).toString();
	}

	private static StringBuilder parseY(String source, StringBuilder abc,
			MacroResolver map) {
		int size = source.length();
		int flag = -1;
		StringBuilder sb = null;
		for (int i = 0; i < size; i++) {
			char c = source.charAt(i);
			if (flag == -1) {
				if (c == '$' || c == '\\')
					flag = c;
				else
					abc.append(c);
			} else if (flag == '$') {
				if (c == '{') {
					flag = c;
					sb = new StringBuilder();
				} else {
					abc.append((char) flag).append(c);
					flag = -1;
				}
			} else if (flag == '{') {
				if (c == '}') {
					String v = map.getValue(sb.toString());
					if (v != null) {
						if (v.contains("${"))
							parseY(v, abc, map);
						else
							abc.append(v);
					}
					sb = null;
					flag = -1;
				} else {
					sb.append(c);
				}
			} else if (flag == '\\') {
				abc.append(c);
				flag = -1;
			}
		}
		if (sb != null) {
			throw new IllegalArgumentException("Unclosed macro");
		}
		return abc;
	}
}
