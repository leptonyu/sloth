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
package me.icymint.sloth.core.alg;

/**
 * A cell is a wrapper of a Point with some useful arguments calculated from the
 * start point and the end point.
 * 
 * @author Daniel
 *
 */
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
