package me.icymint.sloth;

@FunctionalInterface
public interface MacroResolver {
	String getValue(String key);
}
