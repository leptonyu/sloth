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
package me.icymint.sloth.astar;

/**
 * Define the distance between two points.
 * 
 * @author Daniel
 *
 */
public interface Metric {
	/**
	 * Default metric, use Manhattan distance.
	 */
	Metric DEFAULT = (a, b) -> Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	/**
	 * Euclidean metric.
	 */
	Metric SQUARE = (a, b) -> (int) Math.sqrt((a.x - b.x) * (a.x - b.x)
			+ (a.y - b.y) * (a.y - b.y));
	/**
	 * 
	 */
	Metric CROSS = (a, b) -> Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y));

	/**
	 * 
	 * @param a
	 *            point a
	 * @param b
	 *            point b
	 * @return the shortest distance between two points.
	 */
	int distance(Point a, Point b);
}
