package me.icymint.sloth.acm;

import java.util.Arrays;
import java.util.Random;

public class Matrix {
	public static interface MatrixInit {
		void init(int[][] data, int n, int m);
	}

	public static Matrix identity(int n) {
		return new Matrix(n, (d, x, y) -> {
			int nn = Math.min(x, y);
			for (int i = 0; i < nn; i++) {
				d[i][i] = 1;
			}
		});
	}

	public static Matrix random(int n, int min, int max) {
		Random r = new Random();
		int bound = max - min;
		return new Matrix(n, (d, x, y) -> {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					d[i][j] = min + r.nextInt(bound);
				}
			}
		});
	}

	public static Matrix zero(int n) {
		return new Matrix(n, null);
	}

	private final int[][] data;

	private final int n;

	private final int m;

	public Matrix(int i, int j, MatrixInit init) {
		if (i < i || j < 0)
			throw new IllegalArgumentException();
		data = new int[i][j];
		n = i;
		m = j;
		if (init != null)
			init.init(data, n, m);
	}

	public Matrix(int n, MatrixInit init) {
		this(n, n, init);
	}

	public void copyTo(Matrix target) {
		if (this.getRows() != target.getRows()
				|| this.getColumns() != target.getColumns()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				target.data[i][j] = data[i][j];
			}
		}
	}

	public void copyTo(Matrix mm, int fromi, int toi, int fromj, int toj) {
		int x = toi - fromi, y = toj - fromj;
		if (x < 0 || x > n || y < 0 || y > m)
			throw new IllegalArgumentException();
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				mm.data[fromi + i][fromj + j] = data[i][j];
			}
		}
	}

	public void decrease(Matrix ma) {
		if (this.getRows() != ma.getRows()
				|| this.getColumns() != ma.getColumns()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < ma.n; i++) {
			for (int j = 0; j < ma.m; j++) {
				data[i][j] -= ma.data[i][j];
			}
		}
	}

	public Matrix[][] divid(int x, int y) {
		if (x < 0 || x > n || y < 0 || y > m) {
			throw new IllegalArgumentException();
		}
		Matrix[][] mm = { { subMatrix(0, x, 0, y), subMatrix(0, x, y, m) },
				{ subMatrix(x, n, 0, y), subMatrix(x, n, y, m) } };
		return mm;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Matrix) {
			Matrix t = (Matrix) o;
			if (t.n != n || t.m != m)
				return false;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					if (data[i][j] != t.data[i][j])
						return false;
				}
			}
		}
		return super.equals(o);
	}

	public void exchangeColumn(int i, int j) {
		if (i > j) {
			exchangeColumn(j, i);
		} else if (i < j) {
			if (i < 0 || j >= m)
				throw new IllegalArgumentException();
			for (int x = 0; x < n; x++) {
				int t = data[x][i];
				data[x][i] = data[x][j];
				data[x][j] = t;
			}
		}
	}

	public void exchangeRow(int i, int j) {
		if (i > j) {
			exchangeRow(j, i);
		} else if (i < j) {
			if (i < 0 || j >= n)
				throw new IllegalArgumentException();
			for (int x = 0; x < m; x++) {
				int t = data[i][x];
				data[i][x] = data[j][x];
				data[j][x] = t;
			}
		}
	}

	public int getColumns() {
		return m;
	}

	public int getRows() {
		return n;
	}

	public int getValue(int i, int j) {
		return data[i][j];
	}

	public void increase(Matrix ma) {
		if (this.getRows() != ma.getRows()
				|| this.getColumns() != ma.getColumns()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < ma.n; i++) {
			for (int j = 0; j < ma.m; j++) {
				data[i][j] += ma.data[i][j];
			}
		}
	}

	public void increaseColumn(int i, int j, int k) {
		if (k == 0)
			return;
		if (i < 0 || i >= m || j < 0 || j >= m || k == 0)
			throw new IllegalArgumentException();
		if (i == j) {
			kableColumn(i, k + 1);
		} else {
			for (int x = 0; x < n; x++) {
				data[x][i] += k * data[x][j];
			}
		}
	}

	public void increaseRow(int i, int j, int k) {
		if (k == 0)
			return;
		if (i < 0 || i >= n || j < 0 || j >= n || k == 0)
			throw new IllegalArgumentException();
		if (i == j) {
			kableRow(i, k + 1);
		} else {
			for (int x = 0; x < m; x++) {
				data[i][x] += k * data[j][x];
			}
		}
	}

	public void inverse() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				data[i][j] = -data[i][j];
			}
		}
	}

	public void kableColumn(int i, int k) {
		if (i < 0 || i >= m || k == 0)
			throw new IllegalArgumentException();
		for (int x = 0; x < n; x++) {
			data[x][i] *= k;
		}
	}

	public void kableRow(int i, int k) {
		if (i < 0 || i >= n || k == 0)
			throw new IllegalArgumentException();
		for (int x = 0; x < m; x++) {
			data[i][x] *= k;
		}
	}

	public Matrix linkColumn(Matrix ma) {
		if (m != ma.m) {
			throw new IllegalArgumentException();
		}
		Matrix mm = new Matrix(n + ma.n, m, null);
		this.copyTo(mm, 0, n, 0, m);
		ma.copyTo(mm, n, n + ma.n, 0, m);
		return mm;
	}

	public Matrix linkRow(Matrix ma) {
		if (n != ma.n) {
			throw new IllegalArgumentException();
		}
		Matrix mm = new Matrix(n, m + ma.m, null);
		this.copyTo(mm, 0, n, 0, m);
		ma.copyTo(mm, 0, n, m, m + ma.m);
		return mm;
	}

	public Matrix minus(Matrix ma) {
		if (this.getRows() != ma.getRows()
				|| this.getColumns() != ma.getColumns()) {
			throw new IllegalArgumentException();
		}
		return new Matrix(n, m, (d, x, y) -> {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					d[i][j] = data[i][j] - ma.data[i][j];
				}
			}
		});
	}

	public Matrix multiply(Matrix ma) {
		if (ma.getColumns() != getRows()) {
			throw new IllegalArgumentException();
		}
		Matrix mm = new Matrix(n, ma.m, null);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < ma.m; j++) {
				int x = 0;
				for (int k = 0; k < m; k++) {
					x += getValue(i, k) * ma.getValue(k, j);
				}
				mm.setValue(i, j, x);
			}
		}
		return mm;
	}

	public Matrix negative() {
		return new Matrix(n, m, (d, x, y) -> {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					d[i][j] = -data[i][j];
				}
			}
		});
	}

	public Matrix plus(Matrix ma) {
		if (this.getRows() != ma.getRows()
				|| this.getColumns() != ma.getColumns()) {
			throw new IllegalArgumentException();
		}
		return new Matrix(n, m, (d, x, y) -> {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					d[i][j] = data[i][j] + ma.data[i][j];
				}
			}
		});
	}

	private void setValue(int i, int j, int value) {
		data[i][j] = value;
	}

	public Matrix subMatrix(int fromi, int toi, int fromj, int toj) {
		int x = toi - fromi, y = toj - fromj;
		if (x < 0 || x > n || y < 0 || y > m)
			throw new IllegalArgumentException();
		if (x == 0 || y == 0)
			return new Matrix(0, null);
		Matrix nm = new Matrix(x, y, null);
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				nm.data[i][j] = data[fromi + i][fromj + j];
			}
		}
		return nm;
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

	public Matrix change() {
		Matrix mm = this.clone();
		int max = Math.min(n, m);
		for (int k = 0; k < max; k++) {
			int o = mm.data[k][k];
			if (o == 0)
				for (int i = k + 1; i < n; i++) {
					int t = mm.data[i][k];
					if (t != 0) {
						o = t;
						mm.exchangeRow(k, i);
						break;
					}
				}
			if (o < 0) {
				mm.kableRow(k, -1);
				o = -o;
			}
			if (o != 0) {
				for (int i = k + 1; i < n; i++) {
					int r = mm.data[i][k];
					if (r != 0) {
						mm.kableRow(i, o);
						mm.increaseRow(i, k, -r);
					}
				}
			}
		}
		return mm;
	}

	public int abs() {
		if (m != n)
			return 0;
		Matrix mm = change();
		int v = 1;
		for (int i = 0; i < n; i++) {
			v *= mm.data[i][i];
		}
		return v;
	}

	protected void trimRow(int i) {
		if (i < 0 || i >= n)
			throw new IllegalArgumentException();
		int gcd = 0;
		for (int x = 0; x < m; x++) {
			int t = Math.abs(data[i][x]);
			if (t != 0) {
				if (gcd == 0)
					gcd = t;
				else
					gcd = gcd(t, gcd);
			}
		}
		if (gcd > 1) {
			for (int x = 0; x < m; x++) {
				data[i][x] /= gcd;
			}
		}
	}

	@Override
	public Matrix clone() {
		Matrix mm = new Matrix(n, m, null);
		copyTo(mm);
		return mm;
	}

	@Override
	public String toString() {
		if (m == 0 || n == 0)
			return "[]";
		StringBuilder sb = new StringBuilder();
		for (int[] line : data) {
			sb.append(Arrays.toString(line)).append("\n");
		}
		return sb.deleteCharAt(sb.lastIndexOf("\n")).toString();
	}
}
