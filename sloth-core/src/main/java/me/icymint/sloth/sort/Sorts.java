package me.icymint.sloth.sort;

import java.util.Random;
import java.util.Stack;

public class Sorts {
	private Sorts() {
	}

	public static int[] randomArray(int n, int min, int max) {
		Random r = new Random();
		int[] a = new int[n];
		int dif = max - min;
		for (int i = 0; i < n; i++)
			a[i] = dif > 0 ? Math.abs(r.nextInt()) % dif + min : min;
		return a;
	}

	public static long fac(int n) {
		if (n < 2)
			return n;
		long f = 0;
		long s = 1;
		while (n > 0) {
			f = f + s;
			s = f - s;
			n--;
		}
		return f;
	}

	public static void quickSort(int[] arr) {
		quickSort(arr, 0, arr.length - 1);
	}

	private static void quickSort(int[] arr, int i, int j) {
		if (i < j) {
			int m = i;
			int n = j;
			int temp = arr[i];
			while (m != n) {
				while (arr[n] >= temp && m < n)
					n--;
				while (arr[m] <= temp && m < n)
					m++;
				if (m < n)
					swap(arr, m, n);
			}
			arr[i] = arr[m];
			arr[m] = temp;
			quickSort(arr, i, m - 1);
			quickSort(arr, m + 1, j);
		}
	}

	public static void bubbleSort(int[] arr) {
		int n = arr.length;
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int i = 1; i < n; i++) {
				if (arr[i] < arr[i - 1]) {
					swap(arr, i, i - 1);
					sorted = false;
				}
			}
			n--;
		}
	}

	private static void swap(int[] arr, int i, int j) {
		int t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}

	public static void hanoi(int n, Stack<Integer> sx, Stack<Integer> sy,
			Stack<Integer> sz) {
		if (n > 0) {
			hanoi(n - 1, sx, sz, sy);
			System.out.println(String.format("%s-->%s: move %d", sx, sz,
					sx.peek()));
			sz.push(sx.pop());
			hanoi(n - 1, sy, sx, sz);
		}
	}

	public static int gcd(int a, int b) {
		int p = 1;
		while ((a & 1) == 0 && (b & 1) == 0) {
			p <<= 1;
			a >>= 1;
			b >>= 1;
		}
		while (true) {
			int t = Math.abs(a - b);
			if (t == 0)
				return a * p;
			while ((t & 1) == 0) {
				t >>= 1;
			}
			if (a >= b)
				a = t;
			else
				b = t;
		}
	}

	public static void reverse(int[] arr, int i, int j) {
		while (i < j) {
			swap(arr, i++, j--);
		}
	}

	public static void shift(int[] arr, int k) {
		if (arr.length < 2)
			return;
		k = k % arr.length;
		if (k < 2)
			return;
		reverse(arr, 0, k - 1);
		reverse(arr, k, arr.length - 1);
		reverse(arr, 0, arr.length - 1);
	}

	public static int ackermann(int m, int n) {
		if (n < 0 || m < 0)
			throw new IllegalArgumentException();
		if (m == 0)
			return n + 1;
		else {
			if (n == 0)
				return ackermann(m - 1, 1);
			else
				return ackermann(m - 1, ackermann(m, n - 1));
		}
	}

	public static int search(int[] arr, int v) {
		if (arr == null || arr.length == 0)
			return -1;
		return search(arr, v, 0, arr.length - 1);
	}

	private static int search(int[] arr, int v, int i, int j) {
		if (i < j) {
			int m = (i + j) >> 1;
			if (arr[m] >= arr[i]) {
				if (arr[m] >= v && v >= arr[i]) {
					return quickSearch(arr, v, i, m);
				} else {
					return search(arr, v, m + 1, j);
				}
			} else {
				if (arr[i] <= v && v <= arr[m]) {
					return quickSearch(arr, v, m, j);
				} else {
					return search(arr, v, i, m - 1);
				}
			}
		} else if (i == j) {
			return arr[i] == v ? i : -1;
		}
		return -1;
	}

	private static int quickSearch(int[] arr, int v, int i, int j) {
		if (i <= j) {
			int m = (i + j) >> 1;
			if (arr[m] > v) {
				return quickSearch(arr, v, i, m - 1);
			} else if (arr[m] > v) {
				return quickSearch(arr, v, m + 1, j);
			} else {
				return m;
			}
		}
		return -1;
	}

	public static int after(int[] a, int X) {
		if (a.length < 2)
			return 0;
		int[] mam = { a[0], a[0] };
		X <<= 1;
		for (int i = 1; i < a.length; i++) {
			compareAndSet(mam, a[i], X);
		}
		return mam[1] - mam[0];
	}

	private static void compareAndSet(int[] mam, int i, int x) {
		int left = mam[0], right = mam[1];
		for (int j = i - x; j <= i + x; j += x) {
			if (mam[0] > j) {
				if (left == mam[0])
					left = j;
				else
					left = Math.max(j, left);
			} else if (mam[1] < j) {
				if (right == mam[1])
					right = j;
				else
					right = Math.min(j, right);
			} else {
				return;
			}
		}
		if (left < right) {
			mam[0] = left;
		} else if (left > right) {
			mam[1] = right;
		}
	}
}
