package me.icymint.sloth.acm;

import org.junit.Test;

public class MatrixTest {
	private void print(Matrix y) {
		System.out.println(y);
		System.out.println();
	}

	@Test
	public void test() {
		Matrix y = Matrix.random(10, 0, 10);
		print(y);
		y = y.change();
		print(y);
	}
}
