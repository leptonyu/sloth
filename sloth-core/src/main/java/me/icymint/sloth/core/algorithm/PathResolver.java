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

/**
 * 
 * @author Daniel
 *
 */
@FunctionalInterface
public interface PathResolver {
	PathResolver DEFAULT = (p, dest) -> Math.abs(dest.x - p.x)
			+ Math.abs(dest.y - p.y);

	/**
	 * 
	 * @param now
	 *            start point
	 * @param dest
	 *            dest point
	 * @return estimate of the shortest path of two point.
	 */
	int resolve(Point now, Point dest);
}
