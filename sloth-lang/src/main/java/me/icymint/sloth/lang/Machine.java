package me.icymint.sloth.lang;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Machine<T, V> implements Transfer<V> {
	private static class QuoteMachine extends Machine<Character, Character> {

		@Override
		protected Character combine(Token<Character> token, Character last)
				throws IOException {
			if (token == null)
				return null;
			if (token.getFirstValue() == '\\') {
				StringBuilder sb = new StringBuilder();
				while (token != null) {
					sb.append(token.getValue());
					token = token.getParent();
				}
				String abc = sb.reverse().toString();
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
				if (abc.startsWith("\\x")) {
					int m = 4, x = 0;
					for (int i = 2; i < cs.length; i++) {
						char c = cs[i];
						if (c >= 'a') {
							x += (c - 'W') << m;
						} else if (c >= 'A') {
							x += (c - '7') << m;
						} else {
							x += (c - '0') << m;
						}
						m -= 4;
					}
					return (char) x;
				} else {
					int m = 6, x = 0;
					for (int i = 1; i < cs.length; i++) {
						x += (cs[i] - '0') << m;
						m -= 3;
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
				return l == 1 && t.getValue() == 'x';
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
	}
	private static class WordMachine extends Machine<String, Character> {
		private WordMachine() {
		}

		@Override
		protected String combine(Token<Character> token, String last)
				throws IOException {
			if (token == null)
				return null;
			StringBuilder sb = new StringBuilder();
			while (token != null) {
				sb.append(token.getValue());
				token = token.getParent();
			}
			int len = sb.length();
			if (len > 1) {
				String r = sb.reverse().toString();
				if (r.startsWith("\"")) {
					sb = new StringBuilder().append('\'');
					for (Character c : Q_MACHINE.load(Reader.newReader(r
							.substring(1, r.length() - 1)))) {
						sb.append(c);
					}
					return sb.append('\'').toString();
				} else {
					return r;
				}
			} else if (len == 1) {
				char c = sb.charAt(0);
				if (c >= '!' && c <= '~') {
					return sb.toString();
				} else if (c == '\n'
						&& (last == null || (!last.equals(";")
								&& !last.equals("}") && !last.equals("{")))) {
					return ";";
				}
			}
			return null;
		}

		@Override
		protected void init() {
			this.addTransfer((t, v) -> {
				Character f = t.getFirstValue();
				if (f == '\'') {
					Token<Character> p = t.getParent();
					return p == null || t.getValue() != '\'';
				} else if (f == '"') {
					Token<Character> p = t.getParent();
					if (p != null && p.getValue() != '\\'
							&& t.getValue() == '"') {
						return false;
					} else {
						return (v >= ' ' && v <= '~') || v == '\t';
					}
				} else if (f == '_' || (f >= 'a' && f <= 'z')
						|| (f >= 'A' && f <= 'Z')) {
					f = v;
					return f == '_' || (f >= 'a' && f <= 'z')
							|| (f >= 'A' && f <= 'Z') || (f >= '0' && f <= '9');
				} else if (t.level() == 0) {
					return (v == '=' && (f == '>' || f == '<' || f == '!'
							|| f == '=' || f == ':'))
							|| (f == v && (f == '|' || f == '&'));
				}
				return false;
			});
		}
	}

	public static final Machine<String, Character> WORD_MACHINE = new WordMachine();

	private static final Machine<Character, Character> Q_MACHINE = new QuoteMachine();

	private final Queue<Transfer<V>> queue = new ConcurrentLinkedDeque<>();

	protected Machine() {
		init();
	}

	protected Machine<T, V> addTransfer(Transfer<V> tf) {
		queue.offer(tf);
		return this;
	}

	@Override
	public boolean canTransfer(Token<V> t, V v) {
		for (Transfer<V> qt : queue) {
			if (qt.canTransfer(t, v))
				return true;
		}
		return false;
	}

	protected abstract T combine(Token<V> token, T last) throws IOException;

	protected abstract void init();

	public Queue<T> load(Reader<V> reader) throws IOException {
		Queue<T> q = new ConcurrentLinkedQueue<>();
		Token<V> f = Token.create(reader.read());
		V[] vs = null;
		T last = null;
		while ((vs = reader.read(2048)) != null) {
			for (V v : vs) {
				if (canTransfer(f, v)) {
					f = f.nextToken(v);
				} else {
					T x = combine(f, last);
					if (x != null) {
						last = x;
						q.offer(x);
					}
					f = f.newToken(v);
				}
			}
		}
		T x = combine(f, last);
		if (x != null) {
			last = x;
			q.offer(x);
		}
		return q;
	}
}
