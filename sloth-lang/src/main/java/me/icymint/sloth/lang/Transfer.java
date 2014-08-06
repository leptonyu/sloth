package me.icymint.sloth.lang;

@FunctionalInterface
public interface Transfer {
	boolean canTransfer(Token t, char v);
}
