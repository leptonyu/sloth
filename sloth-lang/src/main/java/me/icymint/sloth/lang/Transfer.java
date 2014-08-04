package me.icymint.sloth.lang;

@FunctionalInterface
public interface Transfer<V> {
	boolean canTransfer(Token<V> t, V v);
}
