package me.icymint.sloth.core.alg;

@FunctionalInterface
public interface Area {
	enum Type {
		NULL, OPEN, CLOSE, PATH;
	}

	boolean isOpen(Point p);

	default void paint(Point p, Type become) {
	}
}
