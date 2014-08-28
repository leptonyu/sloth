package me.icymint.sloth.sort;

import java.util.Arrays;
import java.util.Stack;

import org.junit.Test;

public class SortsTest {
	@Test
	public void sort() {
		for (int i = 0; i < 90; i++) {
			System.out.println(String.format("f(%d)=%d", i, Sorts.fac(i)));
		}
	}

	@Test
	public void hanoi() {
		int n = 4;
		Stack<Integer> sx = new Stack<Integer>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2507477624991332448L;

			@Override
			public String toString() {
				return "Sx";
			}
		};
		Stack<Integer> sy = new Stack<Integer>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2507477624991332448L;

			@Override
			public String toString() {
				return "Sy";
			}
		};
		Stack<Integer> sz = new Stack<Integer>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2507477624991332448L;

			@Override
			public String toString() {
				return "Sz";
			}
		};
		for (int i = 0; i < n; i++) {
			sx.add(n - i);
		}
		Sorts.hanoi(n, sx, sy, sz);
	}

	@Test
	public void gcd() {
		gcd(12, 34);
		gcd(2, 34);
		gcd(16, 34);
		gcd(12, 4);
		gcd(1, 94);
		gcd(10000, 12000);
	}

	private void gcd(int a, int b) {
		System.out
				.println(String.format("gcd(%d,%d)=%d", a, b, Sorts.gcd(a, b)));
	}

	@Test
	public void quick() {
		int[] a = Sorts.randomArray(10, 0, 100);
		System.out.println(Arrays.toString(a));
		Sorts.quickSort(a);
		System.out.println(Arrays.toString(a));
		Sorts.shift(a, 4);
		System.out.println(Arrays.toString(a));
		Sorts.search(a, a[0]);
		System.out.println(String.format("%d is at index %d", a[0],
				Sorts.search(a, a[0])));
	}
}
