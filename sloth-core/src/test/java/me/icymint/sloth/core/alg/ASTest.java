package me.icymint.sloth.core.alg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.atomic.LongAdder;

import org.junit.Test;

public class ASTest {

	String[] MAPS = new String[] { "/map.dat", "/map3.dat", "/nomap.dat",
			"/empty.dat", "/slide.dat", "/middle.dat", "/tough.dat" };

	public class MapArea implements Area {
		private final char[][] map = new char[24][80];
		private final Point start;
		private final Point end;
		private final Mover mover;
		private final LongAdder adder = new LongAdder();

		public MapArea(String mappath, Mover move) throws IOException {
			try (BufferedReader input = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(
							mappath)))) {
				String line = null;
				int i = 0;
				while ((line = input.readLine()) != null) {
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
			Cell last = AS.search(start, end, this, mover);
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
				if (t == Type.CLOSE) {
					map[p.x][p.y] = '!';
				} else if (t == Type.OPEN) {
					map[p.x][p.y] = '?';
				} else if (t == Type.PATH) {
					map[p.x][p.y] = '*';
				} else {
					map[p.x][p.y] = ' ';
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
