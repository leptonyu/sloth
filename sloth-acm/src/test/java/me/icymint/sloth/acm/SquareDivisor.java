package me.icymint.sloth.acm;

public class SquareDivisor {
	public long biggest(long n) {
		long y = 2, r = 1;
		while (true) {
			long yy = y * y;
			if (yy == n) {
				return r * n;
			}
			if (yy > n)
				return r;
			int c = 0;
			boolean f = false;
			while (n % y == 0) {
				c++;
				n /= y;
				f = true;
			}
			while (c > 1) {
				r *= yy;
				c -= 2;
			}
			if (y == 2)
				y = 3;
			else
				y += 2;
			if (f) {
				long sn = (long) Math.sqrt(n);
				if (sn * sn == n) {
					return r * n;
				}
			}
		}
	}

	public static void main(String[] x) {
		long start = System.currentTimeMillis();
		System.out.println(new SquareDivisor().biggest(999999998000000002l));
		System.out.println((System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		System.out.println(new SquareDivisor().biggest(999999875021574338l));
		System.out.println((System.currentTimeMillis() - start) + "ms");
	}
}