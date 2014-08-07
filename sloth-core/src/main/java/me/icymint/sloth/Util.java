/*
 * Copyright (C) 2014 Daniel Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.icymint.sloth;

public class Util {
	private Util() {
	}

	/**
	 * parse string using macro resolver.
	 * 
	 * @param source
	 *            string with macro expression.
	 * @param mr
	 *            macro resolver
	 * @return resolved string.
	 * @throws IllegalArgumentException
	 *             if there is unclosed macro expression, it will throw this
	 *             exception.
	 */
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
			throw new IllegalArgumentException("Unclosed macro <"
					+ sb.toString() + ">");
		}
		return abc;
	}
}
