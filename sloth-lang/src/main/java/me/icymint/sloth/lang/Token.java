package me.icymint.sloth.lang;

public final class Token<V> {
	public static <V> Token<V> create(V v) {
		return new Token<V>(v, null);
	}

	private final V v;
	private final V f;
	private final int level;
	private final Token<V> parent;

	private Token(V v, Token<V> parent) {
		this.v = v;
		this.parent = parent;
		level = parent != null ? parent.level + 1 : 0;
		f = parent != null ? parent.f : v;
	}

	public final V getFirstValue() {
		return f;
	}

	public final Token<V> getParent() {
		return parent;
	}

	public final V getValue() {
		return v;
	}

	public final int level() {
		return level;
	}

	public final Token<V> newToken(V v) {
		return create(v);
	}

	public final Token<V> nextToken(V v) {
		return new Token<V>(v, this);
	}
}
