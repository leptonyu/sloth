package me.icymint.sloth.core.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class AStarTest {
	@Test
	public void test() {
		Collection<Cell> stack = AStar.searchPath(new Point(0, 0), new Point(
				12, 12), p -> true, true);
		for (Cell c : stack) {
			System.out.println(c);
		}
		stack = AStar.searchPath(new Point(12, 12), new Point(0, 0), p -> true,
				false);
		for (Cell c : stack) {
			System.out.println(c);
		}
		// long start = System.currentTimeMillis();
		// stack = AStar.searchPath(new Point(10000, 10000), new Point(0, 0),
		// p -> true, true);
		// System.out.println("Search true "
		// + (System.currentTimeMillis() - start) + "ms");
		// start = System.currentTimeMillis();
		// stack = AStar.searchPath(new Point(10000, 10000), new Point(0, 0),
		// p -> true, false);
		// System.out.println("Search false "
		// + (System.currentTimeMillis() - start) + "ms");
	}

	@Test
	public void mapTest() throws IOException {
		char[][] map = new char[24][80];
		try (BufferedReader input = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream("/map.dat")))) {
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
		System.out.println(s + "--->" + e);
		Collection<Cell> stack = AStar.searchPath(s, e,
				p -> map[p.x][p.y] != 'o' && p.x < 24 && p.y < 80 && p.x >= 0
						&& p.y >= 0, false);
		for (Cell c : stack) {
			char v = map[c.point().x][c.point().y];
			if (v == ' ')
				map[c.point().x][c.point().y] = '*';
		}
		for (int i = 0; i < map.length; i++) {
			System.out.println(Arrays.toString(map[i]).replaceAll(",", ""));
		}
		for (Cell c : stack) {
			System.out.println(c);
		}
	}
}
