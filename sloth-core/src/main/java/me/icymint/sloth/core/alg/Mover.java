package me.icymint.sloth.core.alg;

import java.util.ArrayList;
import java.util.List;

public class Mover implements Metric {
	public static final Mover DEFAULT = new Mover(Metric.DEFAULT).add(1, 0)
			.add(-1, 0).add(0, 1).add(0, -1);
	public static final Mover CROSS = new Mover(Metric.DEFAULT).add(1, 0)
			.add(-1, 0).add(0, 1).add(0, -1).add(1, 1).add(-1, 1).add(1, -1)
			.add(-1, -1);
	public static final Mover HORSE = new Mover(Metric.DEFAULT).add(1, 2)
			.add(2, 1).add(-2, 1).add(-1, 2).add(1, -2).add(2, -1).add(-2, -1)
			.add(-1, -2);
	private static final Mover[] vs = new Mover[] { DEFAULT, CROSS };

	private final List<Point> list = new ArrayList<>();
	private final Metric metric;

	public Mover(Metric metric) {
		this.metric = metric == null ? Metric.DEFAULT : metric;
	}

	protected Mover add(int dx, int dy) {
		list.add(new Point(dx, dy));
		return this;
	}

	public List<Point> dxdys() {
		return list;
	}

	@Override
	public int distance(Point a, Point b) {
		return metric.distance(a, b);
	}

	public final static Mover[] values() {
		return vs;
	}

}
