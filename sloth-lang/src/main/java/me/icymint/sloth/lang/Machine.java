package me.icymint.sloth.lang;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Machine<T> implements Transfer {
	private static class QuoteMachine extends Machine<StringBuilder> {

		@Override
		protected Value<StringBuilder> combine(Token token,
				Value<StringBuilder> last) throws IOException {
			if (last == null) {
				last = Value.create(new StringBuilder(), token.getLine());
			}
			int c = combinex(token, last);
			if (c >= 0)
				last.getValue().append((char) c);
			return last;
		}

		protected int combinex(Token token, Value<StringBuilder> last)
				throws IOException {
			if (token == null)
				return -1;
			if (token.getFirstValue() == '\\') {
				int k = token.level();
				char[] buf = new char[k + 1];
				while (token != null) {
					buf[k] = token.getValue();
					token = token.getParent();
					k--;
				}
				String abc = new String(buf);
				switch (abc) {
				case "\\a":
					return 7;
				case "\\b":
					return '\b';
				case "\\f":
					return '\f';
				case "\\n":
					return '\n';
				case "\\r":
					return '\r';
				case "\\t":
					return '\t';
				case "\\v":
					return 11;
				case "\\\\":
					return '\\';
				case "\\'":
					return '\'';
				case "\\\"":
					return '"';
				}
				char[] cs = abc.toCharArray();
				if (cs.length != 4)
					throw new IOException(abc);
				if (abc.toLowerCase().startsWith("\\x")) {
					int x = 0;
					for (int i = 2; i < 4; i++) {
						char c = cs[i];
						x = x << 4;
						if (c >= 'a') {
							x += (c - 'W');
						} else if (c >= 'A') {
							x += (c - '7');
						} else {
							x += (c - '0');
						}
					}
					return (char) x;
				} else {
					int x = 0;
					for (int i = 1; i < 4; i++) {
						x = (x << 3) + (cs[i] - '0');
					}
					return (char) x;
				}
			} else {
				return token.getValue();
			}
		}

		@Override
		protected void init() {
			addTransfer((t, v) -> {
				if (t.getFirstValue() != '\\')
					return false;
				int l = t.level();
				while ((v >= '0' && v <= '7') && l > 0 && l <= 2) {
					v = t.getValue();
					t = t.getParent();
					l--;
				}
				return l == 0;
			});
			addTransfer((t, v) -> {
				if (t.getFirstValue() != '\\')
					return false;
				int l = t.level();
				while (((v >= '0' && v <= '9') || (v >= 'a' && v <= 'f') || (v >= 'A' && v <= 'F'))
						&& l > 1 && l <= 2) {
					v = t.getValue();
					t = t.getParent();
					l--;
				}
				return l == 1 && (t.getValue() == 'x' || t.getValue() == 'X');
			});
			addTransfer((t, v) -> {
				if (t.getFirstValue() != '\\')
					return false;
				if (t.level() == 0) {
					if (v == 'a' || v == 'b' || v == 'f' || v == 'n'
							|| v == 'r' || v == 't' || v == 'v' || v == '\\'
							|| v == '\'' || v == '"') {
						return true;
					}
				}
				return false;
			});
		}

		@Override
		protected boolean lineEnd(char v) {
			return false;
		}
	}

	private static class WordMachine extends Machine<Object> {
		private WordMachine() {
		}

		@Override
		protected Value<Object> combine(Token token, Value<Object> last)
				throws IOException {
			Object x = combinex(token, last);
			return x != null ? Value.create(x, token.getLine()) : null;
		}

		private Object combinex(Token token, Value<Object> last)
				throws IOException {
			if (token == null)
				return null;
			if (token.getParent() != null) {
				int k = token.level();
				char[] buf = new char[k + 1];
				Token t = token;
				while (token != null) {
					buf[k] = token.getValue();
					token = token.getParent();
					k--;
				}
				int len = t.level() + 1;
				char f = t.getFirstValue();
				if (f == '"') {
					StringBuilder sb = new StringBuilder().append('\'');
					for (Value<StringBuilder> c : QMACHINE.load(
							CharReader.newReader(new String(buf, 1, len - 2)),
							t.getLine())) {
						sb.append(c.getValue());
					}
					return sb.append('\'').toString();
				} else if (f == '/') {
					int s = t.getSecondValue();
					if (s == '/' || s == '*')
						return null;
				} else if (t.getFirstValue() == '0') {
					if (t.getSecondValue() == 'b' || t.getSecondValue() == 'B') {
						int x = 0;
						for (int i = 2; i < len; i++) {
							x = (x << 1) + (buf[i] - '0');
						}
						return x;
					} else if (t.getSecondValue() == 'x'
							|| t.getSecondValue() == 'X') {
						int x = 0;
						for (int i = 2; i < len; i++) {
							char c = buf[i];
							if (c >= 'a') {
								x = (x << 4) + (c - 'W');
							} else if (c >= 'A') {
								x = (x << 4) + (c - '7');
							} else {
								x = (x << 4) + (c - '0');
							}
						}
						return x;
					} else {
						int x = 0;
						for (int i = 1; i < len; i++) {
							int v = (buf[i] - '0');
							if (v > 7) {
								return new String(buf);
							}
							x = (x << 3) + v;
						}
						return x;
					}
				} else if (f > '1' && f <= '9') {
					int x = 0;
					for (int i = 0; i < len; i++) {
						x = 10 * x + (buf[i] - '0');
					}
					return x;
				}
				String r = new String(buf);
				return r.equals("true") ? Boolean.TRUE
						: (r.equals("false") ? Boolean.FALSE : r);
			} else {
				char c = token.getFirstValue();
				if (c >= '!' && c <= '~') {
					return (c >= '0' && c <= '9') ? c - '0' : String.valueOf(c);
				} else if (c == '\n'
						&& (last == null || (!last.getValue().equals(";")
								&& !last.getValue().equals("}") && !last
								.getValue().equals("{")))) {
					return ";";
				}
			}
			return null;
		}

		@Override
		protected void init() {
			addTransfer(
					(t, v) -> {
						if (t.getFirstValue() == '/') {
							if (t.level() == 0) {
								return v == '/' || v == '*';
							} else if (t.getSecondValue() == '/') {
								return v != '\n';
							} else if (t.level() > 2) {
								return t.getValue() != '/'
										|| t.getParent().getValue() != '*';
							} else {
								return true;
							}
						} else {
							return false;
						}
					})
					.addTransfer(
							(t, v) -> t.getFirstValue() == '0' && v >= '0'
									&& v <= '7')
					.addTransfer(
							(t, v) -> {
								if (t.getFirstValue() != '0') {
									return false;
								}
								if (t.getSecondValue() < 0) {
									return v == 'b' || v == 'B';
								} else if (t.getSecondValue() == 'b'
										|| t.getSecondValue() == 'B') {
									return v == '0' || v == '1';
								} else {
									return false;
								}
							})
					.addTransfer(
							(t, v) -> {
								if (t.getFirstValue() != '0') {
									return false;
								}
								if (t.getSecondValue() < 0) {
									return v == 'x' || v == 'X';
								} else if (t.getSecondValue() == 'x'
										|| t.getSecondValue() == 'X') {
									return (v >= '0' && v <= '9')
											|| (v >= 'a' && v <= 'f')
											|| (v >= 'A' && v <= 'F');
								} else {
									return false;
								}
							})
					.addTransfer(
							(t, v) -> {
								if (t.getFirstValue() < '0'
										|| t.getFirstValue() > '9') {
									return false;
								}
								return v >= '0' && v <= '9';
							})
					.addTransfer(
							(t, v) -> {
								Character f = t.getFirstValue();
								if (f == '\'') {
									Token p = t.getParent();
									return p == null || t.getValue() != '\'';
								} else if (f == '"') {
									Token p = t.getParent();
									if (p != null && p.getValue() != '\\'
											&& t.getValue() == '"') {
										return false;
									} else {
										return (v >= ' ' && v <= '~')
												|| v == '\t';
									}
								} else if (f == '_' || (f >= 'a' && f <= 'z')
										|| (f >= 'A' && f <= 'Z')) {
									f = v;
									return f == '_' || (f >= 'a' && f <= 'z')
											|| (f >= 'A' && f <= 'Z')
											|| (f >= '0' && f <= '9');
								} else if (t.level() == 0) {
									return (v == '=' && (f == '>' || f == '<'
											|| f == '!' || f == '=' || f == ':'))
											|| (f == v && (f == '|' || f == '&'));
								}
								return false;
							});
		}

	}

	public static final Machine<Object> WORD_MACHINE = new WordMachine();

	private static final Machine<StringBuilder> QMACHINE = new QuoteMachine();

	private final Queue<Transfer> queue = new ConcurrentLinkedQueue<>();

	protected Machine() {
		init();
	}

	protected Machine<T> addTransfer(Transfer tf) {
		queue.offer(tf);
		return this;
	}

	@Override
	public boolean canTransfer(Token t, char v) {
		for (Transfer qt : queue) {
			if (qt.canTransfer(t, v))
				return true;
		}
		return false;
	}

	protected abstract Value<T> combine(Token token, Value<T> last)
			throws IOException;

	protected abstract void init();

	protected boolean lineEnd(char v) {
		return v == '\n';
	}

	public Queue<Value<T>> load(CharReader reader, int line) throws IOException {
		Queue<Value<T>> q = new ConcurrentLinkedQueue<>();
		char[] cbuf = new char[1];
		if (reader.read(cbuf) != 1) {
			throw new IOException();
		}
		Token f = Token.create(cbuf[0], line);
		cbuf = new char[4096];
		int size = 0;
		Value<T> last = null;
		while ((size = reader.read(cbuf)) >= 0) {
			for (int i = 0; i < size; i++) {
				char v = cbuf[i];
				if (canTransfer(f, v)) {
					f = f.nextToken(v, line);
				} else {
					Value<T> x = combine(f, last);
					if (x != null && last != x) {
						last = x;
						q.offer(x);
					}
					f = f.newToken(v, line);
				}
				if (lineEnd(v))
					line++;
			}
		}
		Value<T> x = combine(f, last);
		if (x != null && last != x) {
			last = x;
			q.offer(x);
		}
		return q;
	}
}
