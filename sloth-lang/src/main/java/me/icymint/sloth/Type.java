package me.icymint.sloth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.icymint.sloth.IdentifierTree.CharRange;
import me.icymint.sloth.IdentifierTree.IdentifierPattern;

public enum Type {
	PLUS("+"), PLUSS("==", new CharRange[] { CharRange.plus(IdentifierTree
			.fetch('=')) });
	private static Map<IdentifierPattern, Type> map = new ConcurrentHashMap<>();
	static {
		for (Type t : values()) {
			map.put(t._id, t);
		}
	}
	private final IdentifierPattern _id;
	private final String _pattern;

	private Type(String pattern) {
		this(pattern, IdentifierTree.INSTANCE.addPattern(pattern));
	}

	private Type(String pattern, CharRange[] crs) {
		this(pattern, IdentifierTree.INSTANCE.addPattern(crs));
	}

	private Type(String pattern, IdentifierPattern id) {
		_id = id;
		_pattern = pattern;
	}

	@Override
	public String toString() {
		return _pattern;
	}

	public static Type typeOf(String pattern) {
		IdentifierPattern xx = IdentifierTree.INSTANCE.matches(pattern);
		return xx != null ? map.get(xx) : null;
	}
}
