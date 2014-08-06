package me.icymint.sloth;

import java.util.Map;
import java.util.WeakHashMap;

public class IdentifierTree {
	public static enum IdentifierType {
		NORMAL, OR, STAR, PLUS
	}

	public static IdentifierTree INSTANCE = new IdentifierTree();

	private static class CharRangeImpl implements CharRange {
		private final CharRange cr;
		private final IdentifierType type;

		private CharRangeImpl(CharRange cr, IdentifierType type) {
			this.cr = cr;
			this.type = type;
		}

		@Override
		public boolean in(char c) {
			return cr.in(c);
		}

		@Override
		public IdentifierType type() {
			return type;
		}

	}

	public static interface CharRange {
		public static CharRange or(CharRange cr) {
			return new CharRangeImpl(cr, IdentifierType.OR);
		}

		public static CharRange plus(CharRange cr) {
			return new CharRangeImpl(cr, IdentifierType.PLUS);
		}

		public static CharRange star(CharRange cr) {
			return new CharRangeImpl(cr, IdentifierType.STAR);
		}

		boolean in(char c);

		default IdentifierType type() {
			return IdentifierType.NORMAL;
		}
	}

	private static final Map<Character, CharRange> map = new WeakHashMap<>();

	public static CharRange fetch(char i) {
		CharRange cr = map.get(i);
		if (cr == null) {
			cr = c -> c == i;
			map.put(i, cr);
		}
		return cr;
	}

	public static class IdentifierPattern {
		private final IdentifierPattern parent;
		private IdentifierPattern firstchild;
		private IdentifierPattern next;
		private final CharRange v;
		private final int level;

		private IdentifierPattern(IdentifierPattern parent, CharRange v) {
			this.parent = parent;
			this.v = v;
			this.level = this.parent == null ? 0 : this.parent.level + 1;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof IdentifierPattern) {
				return obj == this;
			} else {
				return super.equals(obj);
			}
		}

		public IdentifierPattern next(char c) {
			IdentifierPattern p = firstchild;
			while (p != null) {
				if (p.v.in(c)) {
					if (p.v.type() == IdentifierType.PLUS
							|| p.v.type() == IdentifierType.STAR) {
						return this;
					} else {
						return p;
					}
				} else {
					if (p.v.type() == IdentifierType.OR
							|| p.v.type() == IdentifierType.STAR) {
						IdentifierPattern pp = p.next(c);
						if (pp == null)
							p = p.next;
						else {
							return pp;
						}
					} else {
						p = p.next;
					}
				}
			}
			return p;
		}
	}

	private final IdentifierPattern root = new IdentifierPattern(null,
			c -> true);

	private IdentifierTree() {
	}

	IdentifierPattern addPattern(CharRange[] pattern) {
		IdentifierPattern p = root;
		int size = pattern.length;
		for (int i = 0; i < size; i++) {
			CharRange c = pattern[i];
			if (p.firstchild == null) {
				p.firstchild = new IdentifierPattern(p, c);
				p = p.firstchild;
				continue;
			} else {
				IdentifierPattern v = p.firstchild;
				while (v.v != c) {
					if (v.next == null) {
						v.next = new IdentifierPattern(p, c);
					}
					v = v.next;
				}
				p = v;
			}
		}
		return p;
	}

	IdentifierPattern addPattern(String pattern) {
		CharRange[] cr = new CharRange[pattern.length()];
		for (int i = 0; i < cr.length; i++) {
			cr[i] = fetch(pattern.charAt(i));
		}
		return addPattern(cr);
	}

	IdentifierPattern matches(String pattern) {
		if (pattern == null || pattern.isEmpty()) {
			return null;
		}
		int size = pattern.length();
		IdentifierPattern p = root;
		for (int i = 0; i < size; i++) {
			p = p.next(pattern.charAt(i));
			if (p == null)
				return null;
		}
		return p;
	}
}
