package me.icymint.sloth.core.alg;

import java.util.HashMap;
import java.util.Map;

import me.icymint.sloth.core.alg.Area.Type;

public class AS {

	public static Cell search(Point start, Point end, Area area) {
		return search(start, end, area, Mover.DEFAULT);
	}

	public static Cell searchCross(Point start, Point end, Area area) {
		return search(start, end, area, Mover.CROSS);
	}

	public static Cell search(Point start, Point end, Area area, Mover move) {
		if (start == null || end == null || area == null || !area.isOpen(start)
				|| !area.isOpen(end)) {
			return null;
		}
		Map<Point, Cell> close = new HashMap<>();
		Map<Point, Cell> open = new HashMap<>();
		Cell now = new Cell(null, start, move, end);
		area.paint(start, Type.OPEN);
		open.put(start, now);
		while (now != null) {
			Point p = now.getLocation();
			area.paint(p, Type.CLOSE);
			open.remove(p);
			close.put(p, now);
			if (p.equals(end)) {
				break;
			}
			Cell small = null;
			for (Point dp : move.dxdys()) {
				Point np = new Point(p.x + dp.x, p.y + dp.y);
				if (!area.isOpen(np) || close.containsKey(np)) {
					continue;
				}
				Cell old = open.get(np);
				Cell next = new Cell(now, np, move, end);
				if (old == null || old.compareTo(next) > 0) {
					if (old == null)
						area.paint(np, Type.OPEN);
					open.put(np, next);
					if (small == null || next.delta > small.delta) {
						small = next;
					}
				}
			}
			now = small;
			small = null;
			if (!open.isEmpty())
				small = open.values().stream().sorted().findFirst()
						.orElse(null);
			if (now == null || (small != null && now.compareTo(small) > 0))
				now = small;
		}
		return close.get(end);
	}
}
