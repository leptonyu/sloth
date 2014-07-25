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

import me.icymint.sloth.core.algorithm.AStar.Adder;

/**
 * 
 * @author Daniel
 *
 */
@FunctionalInterface
public interface MoveResolver {
	/**
	 * origin point can move up, down, left and right.
	 */
	MoveResolver DEFAULT = (d) -> d.add(1, 0).add(-1, 0).add(0, 1).add(0, -1);
	/**
	 * origin point can move up, down, left, right and to the four corners.
	 */
	MoveResolver CROSS = (d) -> DEFAULT.path(d.add(1, 1).add(-1, 1).add(1, -1)
			.add(-1, -1));

	/**
	 * Define the list of points from origin point can move to next.
	 * 
	 * @param adder
	 *            a container to check this points.
	 * @return Adder itself.
	 */
	Adder path(Adder adder);
}
