package me.icymint.sloth.acm;

import org.junit.Test;

public class MatrixTest {
	private void print(Matrix y) {
		System.out.println(y);
		System.out.println();
	}

	@Test
	public void test() {
		Matrix y = Matrix.random(5, 0, 5);
		print(y);
		System.out.println(y.abs());
		y = y.change();
		print(y);
		System.out.println(y.abs());
		print(y.change());
	}
}
