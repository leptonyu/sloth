package me.icymint.sloth.core.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class AStar {
	interface NewCell {
		Cell check(Cell now, Point a);
	}

	private static class Adder {
		private ArrayList<Cell> list = new ArrayList<>();
		private final NewCell newc;
		private final Cell now;

		Adder(NewCell newc, Cell now) {
			this.newc = newc;
			this.now = now;
		}

		Adder add(int dx, int dy) {
			Cell c = newc.check(now, new Point(now.point().x + dx,
					now.point().y + dy));
			if (c != null)
				list.add(c);
			return this;
		}

		Cell get() {
			if (list.isEmpty())
				return null;
			else {
				Collections.sort(list);
				return list.get(0);
			}
		}
	}

	public static Collection<Cell> searchPath(Point source, Point dest,
			MapResolver mr, boolean cross) {
		Stack<Cell> path = new Stack<>();
		if (source == null || dest == null || !mr.isOpen(source)
				|| !mr.isOpen(dest)) {
		} else {
			Map<Point, Cell> map = new ConcurrentHashMap<>();
			NewCell newc = (now, a) -> {
				if (now == null) {
					now = Cell.create(a, 0, dest, mr.isOpen(a));
					map.put(a, now);
					return now;
				}
				Cell old = map.get(a);
				if (old == null) {
					now = Cell.create(a, now.step() + 1, dest, mr.isOpen(a));
					map.put(a, now);
					return now.isOpen() ? now : null;
				}
				if (!old.isOpen()) {
					return null;
				}
				now = Cell.create(a, now.step() + 1, dest, mr.isOpen(a));
				if (old.compareTo(now) >= 0) {
					map.put(a, now);
					return now.isOpen() ? now : null;
				}
				return path.contains(old) ? null : old;
			};
			Cell first = newc.check(null, source);
			if (first != null) {
				path.push(first);
			}
			while (!path.isEmpty()) {
				Cell now = path.peek();
				if (now.point().equals(dest)) {
					break;
				}
				Adder adder = new Adder(newc, now).add(1, 0).add(-1, 0)
						.add(0, 1).add(0, -1);
				if (cross) {
					adder.add(1, 1).add(1, -1).add(-1, 1).add(-1, -1);
				}
				Cell next = adder.get();
				if (next != null && next.isOpen()) {
					path.push(next);
				} else {
					now.close();
					path.pop();
				}
			}
		}
		TreeMap<Integer, Cell> list = new TreeMap<>();
		while (!path.isEmpty()) {
			Cell c = path.pop();
			if (!list.containsKey(c.step())) {
				list.put(c.step(), c);
			}
		}
		return list.values();
	}
}
