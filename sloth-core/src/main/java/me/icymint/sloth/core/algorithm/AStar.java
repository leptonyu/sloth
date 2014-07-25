/*
 * Copyright (C) 2014 Daniel Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.icymint.sloth.core.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A star algorithm.
 * 
 * @author Daniel
 *
 */
public class AStar {
	public static class Adder {
		private ArrayList<Cell> list = new ArrayList<>();
		private final NewCell newc;
		private final Cell now;

		Adder(NewCell newc, Cell now) {
			this.newc = newc;
			this.now = now;
		}

		public Adder add(int dx, int dy) {
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

	interface NewCell {
		Cell check(Cell now, Point a);
	}

	public static class Cell implements Comparable<Cell> {

		static Cell create(Point p, int h, Point dest, PathResolver g) {
			return new Cell(p, h, g.resolve(p, dest));
		}

		private final Point _p;
		private volatile boolean _open = true;
		private final int _h;
		private final int _g;
		private final int _f;

		private Cell(Point p, int h, int g) {
			_p = p;
			_h = h;
			_g = g;
			_f = h + g;
		}

		void close() {
			_open = false;
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

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Cell) {
				Cell cell = (Cell) obj;
				return cell.point().equals(point());
			}
			return super.equals(obj);
		}

		int f() {
			return _f;
		}

		int g() {
			return _g;
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

		@Override
		public String toString() {
			return "[" + isOpen() + ",step=" + step() + ": x=" + point().x
					+ ",y=" + point().y + "]";
		}
	}

	/**
	 * Search a path from start point a to end point b with a-* algorithm.
	 * 
	 * @param source
	 *            start point
	 * @param dest
	 *            end point
	 * @param mr
	 *            map resolver to show if certain point is open or closed.
	 * @return the path found.
	 */

	public static Collection<Cell> searchPath(Point source, Point dest,
			MapResolver mr) {
		return searchPath(source, dest, mr, MoveResolver.DEFAULT);
	}

	/**
	 * Search a path from start point a to end point b with a-* algorithm.
	 * 
	 * @param source
	 *            start point
	 * @param dest
	 *            end point
	 * @param mr
	 *            map resolver to show if certain point is open or closed.
	 * @return the path found.
	 */
	public static Collection<Cell> searchCrossPath(Point source, Point dest,
			MapResolver mr) {
		return searchPath(source, dest, mr, MoveResolver.CROSS);
	}

	/**
	 * Search a path from start point a to end point b with a-* algorithm.
	 * 
	 * @param source
	 *            start point
	 * @param dest
	 *            end point
	 * @param mr
	 *            map resolver to show if certain point is open or closed.
	 * @param pr
	 *            path resolver to show how to move
	 * @return the path found.
	 */
	public static Collection<Cell> searchPath(Point source, Point dest,
			MapResolver mr, MoveResolver pr) {
		return searchPath(source, dest, mr, pr, PathResolver.DEFAULT);
	}

	/**
	 * Search a path from start point a to end point b with a-* algorithm.
	 * 
	 * @param source
	 *            start point
	 * @param dest
	 *            end point
	 * @param mr
	 *            map resolver to show if certain point is open or closed.
	 * @param pr
	 *            path resolver to show how to move
	 * @param dr
	 *            dest resolver to estimate the path.
	 * @return the path found.
	 */
	public static Collection<Cell> searchPath(Point source, Point dest,
			MapResolver mr, MoveResolver pr, PathResolver dr)
			throws NullPointerException {
		Stack<Cell> path = new Stack<>();
		if (source == null || dest == null || !mr.isOpen(source)
				|| !mr.isOpen(dest)) {
		} else {
			Map<Point, Cell> map = new ConcurrentHashMap<>();
			NewCell newc = (now, a) -> {
				if (!mr.isOpen(a))
					return null;
				if (now == null) {
					now = Cell.create(a, 0, dest, dr);
					map.put(a, now);
					return now;
				}
				Cell old = map.get(a);
				if (old == null) {
					now = Cell.create(a, now.step() + 1, dest, dr);
					map.put(a, now);
					return now.isOpen() ? now : null;
				}
				if (!old.isOpen()) {
					return null;
				}
				now = Cell.create(a, now.step() + 1, dest, dr);
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
				Cell next = pr.path(new Adder(newc, now)).get();
				if (next != null && next.isOpen()) {
					path.push(next);
				} else {
					now.close();
					path.pop();
				}
			}
		}
		TreeMap<Integer, Cell> list = new TreeMap<>();
		if (path.size() > 0) {
			while (!path.isEmpty()) {
				Cell now = path.pop();
				if (list.containsKey(now.step())) {
					Cell old = list.get(now.step());
					if (old.f() > now.f()) {
						list.put(now.step(), now);
					}
				} else {
					list.put(now.step(), now);
				}
			}
		}
		return list.values();
	}
}
