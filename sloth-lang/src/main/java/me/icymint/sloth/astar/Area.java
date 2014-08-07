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
 * Area
 * 
 * @author Daniel
 *
 */
@FunctionalInterface
public interface Area {
	enum Type {
		NULL, OPEN, CLOSE, PATH;
	}

	/**
	 * if the specific point in the area is open.
	 * 
	 * @param p
	 *            the specific point.
	 * @return true stand for the point is open, otherwise return false.
	 */
	boolean isOpen(Point p);

	/**
	 * Paint the point when type changes to new one.
	 * 
	 * @param p
	 *            the specific point.
	 * @param become
	 *            new type.
	 */
	default void paint(Point p, Type become) {
	}
}
