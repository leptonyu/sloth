package me.icymint.sloth.acm;

import java.util.HashSet;
import java.util.Set;

public class Bonuses {
	public int[] getDivision(int[] points) {
		int total = 0;
		for (int p : points) {
			total += p;
		}
		int[] r = new int[points.length];
		int rt = 0;
		for (int i = 0; i < r.length; i++) {
			r[i] = (points[i] * 100) / total;
			rt += r[i];
		}
		int left = 100 - rt;
		Set<Integer> max = new HashSet<>();
		while (left > 0) {
			int k = -1;
			for (int i = 0; i < r.length; i++) {
				if (!max.contains(i)) {
					if (k == -1 || r[i] > r[k]) {
						k = i;
					}
				}
			}
			r[k]++;
			left--;
		}
		return r;
	}
}