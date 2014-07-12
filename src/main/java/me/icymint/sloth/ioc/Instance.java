package me.icymint.sloth.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 
 * @author Daniel
 *
 */
public final class Instance {
	private static boolean compareParameters(Class<?>[] pts, Object[] params) {
		if (pts.length == params.length) {
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null && !pts[i].isInstance(params[i])) {
					return false;
				}
			}
			return true;
		}
		return false;

	}

	@SuppressWarnings("unchecked")
	public static <E, T extends E> E create(Class<T> clzz, Object... params)
			throws InstantiationException {
		InstantiationException ex = new InstantiationException(clzz.getName());
		if (clzz == Instance.class) {
			throw ex;
		}
		try {
			if (params == null || params.length == 0) {
				return clzz.newInstance();
			} else {
				Constructor<T> t = null;
				for (Constructor<?> c : clzz.getConstructors()) {
					if (compareParameters(c.getParameterTypes(), params)) {
						if (t == null) {
							t = (Constructor<T>) c;
						} else {
							throw new Exception(
									"Can not exactly match the vararg Constructor type.");
						}
					}
				}
				if (t != null) {
					return t.getParameterCount() == 0 ? t.newInstance() : t
							.newInstance(params);
				}
			}
		} catch (Exception e) {
			ex.addSuppressed(e);
		}
		try {
			for (Method m : clzz.getDeclaredMethods()) {
				if (m.getReturnType() == clzz
						&& compareParameters(m.getParameterTypes(), params)) {
					return (T) m.invoke(clzz, params);
				}
			}
		} catch (Exception e) {
			ex.addSuppressed(e);
		}
		throw ex;
	}

	private Instance() {
	}
}
