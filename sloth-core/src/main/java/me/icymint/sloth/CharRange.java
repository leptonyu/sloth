package me.icymint.sloth;

@FunctionalInterface
public interface CharRange {
	final class Util {
		private static class HEX implements CharRange {

			@Override
			public boolean contains(char c) {
				return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')
						|| (c >= 'A' && c <= 'Z');
			}

			@Override
			public int max() {
				return 8;
			}

			@Override
			public int min() {
				return 1;
			}

		}

		private static class Name implements CharRange {

			@Override
			public boolean contains(char c) {
				return c == '_' || (c >= '0' && c <= '9')
						|| UPPERCASE.contains(c) || LOWERCASE.contains(c);
			}

			@Override
			public int max() {
				return -1;
			}

			@Override
			public int min() {
				return 0;
			}

		}

		private static class Number implements CharRange {

			@Override
			public boolean contains(char c) {
				return c >= '0' && c <= '9';
			}

			@Override
			public int max() {
				return 14;
			}

			@Override
			public int min() {
				return 0;
			}

		}

		private static class OCT implements CharRange {

			@Override
			public boolean contains(char c) {
				return c >= '0' && c <= '7';
			}

			@Override
			public int max() {
				return 16;
			}

			@Override
			public int min() {
				return 1;
			}

		}

		private static class Quote implements CharRange {

			@Override
			public boolean contains(char c) {
				return c != '\'';
			}

			@Override
			public int max() {
				return -1;
			}

			@Override
			public int min() {
				return 0;
			}

		}

		static CharRange UPPERCASE = c -> c >= 'A' && c <= 'Z';

		static CharRange LOWERCASE = c -> c >= 'a' && c <= 'z';

		private Util() {
		}
	}

	CharRange[] STRING = new CharRange[] { c -> c == '\'', new Util.Quote(),
			c -> c == '\'' };

	CharRange[] DEC = new CharRange[] { c -> c >= '1' && c <= '9',
			new Util.Number() };

	CharRange[] OCT = new CharRange[] { c -> c == '0', new Util.OCT() };

	CharRange[] HEX = new CharRange[] { c -> c == '0',
			c -> c == 'x' || c == 'X', new Util.HEX() };

	CharRange[] NAME = new CharRange[] {
			c -> c == '_' || Util.UPPERCASE.contains(c)
					|| Util.LOWERCASE.contains(c), new Util.Name() };

	/**
	 * 
	 * 
	 * @param c
	 *            the given character.
	 * @return if the specific character is in the range return true, otherwise
	 *         return false.
	 */
	boolean contains(char c);

	default int max() {
		return 1;
	}

	default int min() {
		return 1;
	}
}
