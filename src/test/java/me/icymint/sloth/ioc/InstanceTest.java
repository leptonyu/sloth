package me.icymint.sloth.ioc;

import org.junit.Assert;
import org.junit.Test;

public class InstanceTest {
	public interface Hello {
		default String say() {
			return getClass().getName();
		}
	}

	public static class TestA implements Hello {
	}

	public static class TestB implements Hello {
		private TestB() {
		}
	}

	public static class TestC implements Hello {
		public static TestC abc() {
			return new TestC();
		}

		private TestC() {
		}
	}

	public static class TestD implements Hello {
		private TestD() {
		}

		public TestD abc() {
			return new TestD();
		}
	}

	public static class TestE implements Hello {
		private final String _hello;

		public TestE() {
			this("xxx");
		}

		public TestE(String hello) {
			_hello = hello;
		}

		@Override
		public String say() {
			return _hello;
		}
	}

	@Test(expected = InstantiationException.class)
	public void testInstanceDefault() throws InstantiationException {
		Instance.create(Instance.class);
	}

	@Test(expected = InstantiationException.class)
	public void testInstanceDefault2() throws InstantiationException {
		Assert.assertNull(Instance.create(Instance.class, "abc"));
	}

	@Test(expected = InstantiationException.class)
	public void testNonStaticConstructor() throws InstantiationException {
		//
		Instance.create(TestD.class);
	}

	@Test
	public void testNormalDefaultConstructor() throws InstantiationException {
		Assert.assertEquals(TestA.class.getName(), Instance.create(TestA.class)
				.say());
	}

	@Test
	public void testParameterConstuctor() throws InstantiationException {
		Assert.assertEquals("Hello", Instance.create(TestE.class, "Hello")
				.say());
		Assert.assertEquals(null, Instance.create(TestE.class, (String) null)
				.say());
		Assert.assertEquals("xxx", Instance
				.create(TestE.class, (Object[]) null).say());
		Assert.assertEquals("xxx", Instance.create(TestE.class).say());
	}

	@Test(expected = InstantiationException.class)
	public void testPrivateDefaultConstructor() throws InstantiationException {
		//
		Instance.create(TestB.class);
	}

	@Test
	public void testStaticConstructor() throws InstantiationException {
		//
		Assert.assertEquals(TestC.class.getName(), Instance.create(TestC.class)
				.say());
	}
}
