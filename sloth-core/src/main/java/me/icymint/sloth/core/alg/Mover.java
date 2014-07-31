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

import java.util.ArrayList;
import java.util.List;

/**
 * Define the way to move from one point to others.
 * 
 * @author Daniel
 *
 */
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
