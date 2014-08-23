package me.icymint.sloth;

public abstract class LanguageTree {
	public static class LanguageNode {
		private final LanguageNode parent;
		private LanguageNode firstchild;
		private LanguageNode next;
		private final int level;
		private final CharRange cr;

		private LanguageNode(LanguageNode parent, CharRange cr) {
			this.parent = parent;
			level = this.parent == null ? 0 : this.parent.level + 1;
			this.cr = cr;
		}

		public int level() {
			return level;
		}
	}

	private final LanguageNode root = new LanguageNode(null, c -> true);

	protected abstract void init();

	protected void addpattern(CharRange[] cr) {
		LanguageNode node = root;
		for (int i = 0; i < cr.length; i++) {
			CharRange c = cr[i];
			LanguageNode x = node.firstchild;
			if (x == null) {
				node.firstchild = new LanguageNode(node, c);
				node = node.firstchild;
			} else {
				while (x.cr != c) {
					if (x.next == null) {
						x.next = new LanguageNode(node, c);
						node = x.next;
						break;
					} else {
						x = x.next;
					}
				}
				if (x.cr == c)
					node = x;
			}
		}
	}
}
