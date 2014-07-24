package me.icymint.sloth.core.algorithm;

public class Cell implements Comparable<Cell> {

	static Cell create(Point p, int h, Point dest, boolean open) {
		return new Cell(p, h, Math.abs(dest.x - p.x) + Math.abs(dest.y - p.y),
				open);
	}

	private final Point _p;
	private volatile boolean _open;
	private final int _h;
	private final int _g;
	private final int _f;

	private Cell(Point p, int h, int g, boolean open) {
		_p = p;
		_h = h;
		_g = g;
		_f = h + g;
		_open = open;
	}

	void close() {
		_open = false;
	}

	boolean isOpen() {
		return _open;
	}

	public Point point() {
		return _p;
	}

	public int step() {
		return _h;
	}

	int g() {
		return _g;
	}

	int f() {
		return _f;
	}

	@Override
	public String toString() {
		return "[" + isOpen() + ",step=" + step() + ": x=" + point().x + ",y="
				+ point().y + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cell) {
			Cell cell = (Cell) obj;
			return cell.point().equals(point());
		}
		return super.equals(obj);
	}

	@Override
	public int compareTo(Cell o) {
		if (isOpen()) {
			if (o.isOpen()) {
				return (f() - o.f());
			} else {
				return -1;
			}
		} else if (o.isOpen()) {
			return 1;
		} else {
			return (step() - o.step()) - (o.g() - g());
		}
	}
}
