package me.icymint.sloth.core.alg;

public interface Metric {
	Metric DEFAULT = (a, b) -> Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	Metric SQUARE = (a, b) -> (int) Math.sqrt((a.x - b.x) * (a.x - b.x)
			+ (a.y - b.y) * (a.y - b.y));
	Metric CROSS = (a, b) -> Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y));

	int distance(Point a, Point b);
}
