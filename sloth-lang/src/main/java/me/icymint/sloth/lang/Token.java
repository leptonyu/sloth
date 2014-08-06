package me.icymint.sloth.lang;

public final class Token {
	public static Token create(char v, int line) {
		return new Token(v, null, line);
	}

	private final char v;
	private final char f;
	private final int s;
	private final int level;
	private final Token parent;
	private final int line;

	private Token(char v, Token parent, int line) {
		this.v = v;
		this.parent = parent;
		level = parent != null ? parent.level + 1 : 0;
		f = parent != null ? parent.f : v;
		s = (parent != null ? (parent.parent == null ? v : parent.s) : -1);
		this.line = line;
	}

	public final char getFirstValue() {
		return f;
	}

	public final int getLine() {
		return line;
	}

	public final Token getParent() {
		return parent;
	}

	public final int getSecondValue() {
		return s;
	}

	public final char getValue() {
		return v;
	}

	public final int level() {
		return level;
	}

	public final Token newToken(char v, int line) {
		return create(v, line);
	}

	public final Token nextToken(char v, int line) {
		return new Token(v, this, line);
	}
}
