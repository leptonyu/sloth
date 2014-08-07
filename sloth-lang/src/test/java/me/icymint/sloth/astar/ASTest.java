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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.atomic.LongAdder;

import me.icymint.sloth.astar.AStarUtil;
import me.icymint.sloth.astar.Area;
import me.icymint.sloth.astar.Cell;
import me.icymint.sloth.astar.Mover;
import me.icymint.sloth.astar.Point;

import org.junit.Test;

public class ASTest {

	public class MapArea implements Area {
		private final char[][] map = new char[24][80];
		private final Point start;
		private final Point end;
		private final Mover mover;
		private final LongAdder adder = new LongAdder();
		private final String name;
		private final File file;

		public MapArea(String mappath, Mover move) throws IOException {
			name = mappath;
			File file = new File("target/" + name + ".play");
			int k = 1;
			while (file.exists()) {
				file = new File("target/" + name + (k++) + ".play");
			}
			file.createNewFile();
			this.file = file;
			try (BufferedReader input = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(
							"/" + mappath + ".dat")))) {
				String line = null;
				int i = 0;
				while ((line = input.readLine()) != null) {
					Files.write(file.toPath(), (line + "\n").getBytes(),
							StandardOpenOption.APPEND);
					map[i++] = line.toCharArray();
				}
			}
			Point s = null, e = null;
			for (int i = 0; i < map.length; i++) {
				char[] sub = map[i];
				for (int j = 0; j < sub.length; j++) {
					if (sub[j] == 's') {
						s = new Point(i, j);
					} else if (sub[j] == 'e') {
						e = new Point(i, j);
					}
				}
			}
			start = s;
			end = e;
			mover = move == null ? Mover.DEFAULT : move;
		}

		@Override
		public boolean isOpen(Point p) {
			return p.x < 24 && p.y < 80 && p.x >= 0 && p.y >= 0
					&& map[p.x][p.y] != 'o';
		}

		public void paint() {
			Cell last = AStarUtil.search(start, end, this, mover);
			int i = 0;
			while (last != null) {
				i++;
				this.paint(last.getLocation(), Type.PATH);
				last = last.getParent();
			}
			paintAll();
			System.out.println(adder.sum() + ":" + i);

		}

		@Override
		public void paint(Point p, Type t) {
			adder.increment();
			char x = map[p.x][p.y];
			if (x != 'e' && x != 's') {
				char change = ' ';
				if (t == Type.CLOSE) {
					change = '!';
				} else if (t == Type.OPEN) {
					change = '?';
				} else if (t == Type.PATH) {
					change = '*';
				}
				map[p.x][p.y] = change;
				try {
					Files.write(file.toPath(),
							(p.x + "," + p.y + "," + change + "\n").getBytes(),
							StandardOpenOption.APPEND);
				} catch (IOException e) {
				}
			}
		}

		public void paintAll() {
			for (int i = 0; i < map.length; i++) {
				String line = Arrays.toString(map[i]).replaceAll(",", "");
				System.out.println(line.substring(1, line.length() - 1));
			}
		}

	}

	String[] MAPS = new String[] { "map", "map3", "nomap", "empty", "slide",
			"middle", "tough" };

	public void mapTest(String mappath, Mover mover) throws IOException {
		new MapArea(mappath, mover).paint();
	}

	@Test
	public void mapTestCross() throws IOException {
		for (String map : MAPS) {
			for (Mover move : Mover.values()) {
				mapTest(map, move);
			}
		}
	}
}
