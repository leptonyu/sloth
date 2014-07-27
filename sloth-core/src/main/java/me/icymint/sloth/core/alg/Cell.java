package me.icymint.sloth.core.alg;

public class Cell implements Comparable<Cell> {

	private final Cell parent;
	private final Point point;
	private final int h;
	private final int g;
	private final int f;
	private final int x;
	private final int y;
	final int delta;

	Cell(Cell parent, Point point, Metric metric, Point dest) {
		this.parent = parent;
		this.point = point;
		h = metric.distance(point, dest);
		g = parent == null ? 0 : parent.g
				+ metric.distance(point, parent.point);
		f = h + g;
		x = dest.x - point.x;
		y = dest.y - point.y;
		int dx = (parent != null) ? (point.x - parent.point.x) : 0;
		int dy = (parent != null) ? (point.y - parent.point.y) : 0;
		delta = (parent != null) ? (dx * parent.x + dy * parent.y) : 0;
	}

	public Cell getParent() {
		return parent;
	}

	public Point getLocation() {
		return point;
	}

	@Override
	public String toString() {
		return "[step=" + g + ": x=" + getLocation().x + ",y="
				+ getLocation().y + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Cell) {
			return ((Cell) o).getLocation().equals(getLocation());
		}
		return super.equals(o);
	}

	@Override
	public int compareTo(Cell next) {
		return f - next.f;
	}

}
