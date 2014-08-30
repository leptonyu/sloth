package me.icymint.sloth.acm;

import java.util.Arrays;

public class ORSolitaireDiv2 {
	public int getMinimum(int[] numbers, int goal) {
		int delta = 0;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] == goal)
				delta++;
		}
		numbers = trim(numbers, goal);
		if (numbers.length == 0)
			return delta;
		int[] f = new int[32];
		for (int n : numbers) {
			copy(f, n);
		}
		int m = -1;
		for (int i = 0; i < f.length; i++) {
			if (f[i] > 0) {
				int t = xxxx(i, f[i], numbers, goal);
				if (m == -1 || m > t) {
					m = t;
				}
			}
		}
		return m + delta;
	}

	private int xxxx(int k, int fk, int[] numbers, int goal) {
		int rb = 1 << k;
		int[] pp = new int[numbers.length];
		int pi = 0;
		int ng = rb;
		for (int n : numbers) {
			if ((rb & n) != 0) {
				pp[pi++] = n;
			} else {
				ng |= n;
			}
		}
		if (ng == goal) {
			return fk;
		}
		pp = Arrays.copyOfRange(pp, 0, pi);
		int x = goal - ng;
		int dif = 0;
		for (int d : pp) {
			ng |= d;
			if ((d & x) == 0) {
				dif++;
			}
		}
		if (ng == goal)
			return fk - dif;
		return 0;
	}

	private int[] trim(int[] numbers, int goal) {
		int i = 0, k = 0;
		int[] x = new int[numbers.length];
		int inverse = ~goal;
		while (i < numbers.length) {
			int t = numbers[i];
			if (t < goal && (t & inverse) == 0) {
				x[k++] = t;
			}
			i++;
		}
		return Arrays.copyOfRange(x, 0, k);
	}

	private int copy(int[] dest, int copy) {
		int i = 0;
		while (copy > 0) {
			if ((copy & 1) == 1) {
				dest[i] += 1;
			}
			i++;
			copy >>= 1;
		}
		return i;
	}

	public static void main(String... xx) {
		long start = System.currentTimeMillis();
		System.out.println(new ORSolitaireDiv2().getMinimum(new int[] {
				744207802, 459506930, 96031044, 94305333, 71623564, 401261714,
				143654920, 180838023, 426804418, 292454963, 396375937,
				783935062, 983980901, 302100998 }, 751841480));
		System.out.println(new ORSolitaireDiv2().getMinimum(new int[] { 1546,
				5465461, 4565461, 456451, 1546456, 1456546, 1456, 1676, 154664,
				15446, 1455676, 154645, 1, 145646, 7671, 1556746, 5465,
				15464565, 1546546, 1000000 }, 1));
		System.out.println((System.currentTimeMillis() - start) + "ms");
	}
}