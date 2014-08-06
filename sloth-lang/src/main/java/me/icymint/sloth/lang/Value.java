package me.icymint.sloth.lang;

public final class Value<V> {
	public static <V> Value<V> create(V v, int line) {
		return new Value<>(v, line);
	}

	private final int line;
	private final V v;

	private Value(V v, int line) {
		this.v = v;
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public V getValue() {
		return v;
	}
}
