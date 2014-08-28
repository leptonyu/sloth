package me.icymint.sloth.acm;

public class TaroFriends {

	public int getNumber(int[] a, int X) {
		if (a.length < 2)
			return 0;
		quickSort(a);
		int[] mam = { a[0], a[0] };
		X <<= 1;
		for (int i = 1; i < a.length; i++) {
			compareAndSet(mam, a[i], X);
		}
		return mam[1] - mam[0];
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

	private static void swap(int[] arr, int i, int j) {
		int t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
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
