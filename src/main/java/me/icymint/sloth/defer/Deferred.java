/**
 * 
 */
package me.icymint.sloth.defer;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Daniel
 *
 */
public final class Deferred implements AutoCloseable {
	/**
	 * 创建Deferred实例。
	 * 
	 * @return 返回Deferred实例。
	 */
	public static Deferred create() {
		return new Deferred();
	}

	private final AtomicBoolean _shutdown = new AtomicBoolean(false);
	private final Stack<DeferredOperation> _stack = new Stack<>();

	private Deferred() {
	}

	@Override
	public void close() throws Exception {
		if (_shutdown.compareAndSet(false, true)) {
			Exception ex = new Exception();
			while (true) {
				DeferredOperation oper = null;
				try {
					oper = _stack.pop();
				} catch (EmptyStackException e) {
					break;
				}
				try {
					oper.execute();
				} catch (Exception e) {
					ex.addSuppressed(e);
				}
			}
			if (ex.getSuppressed().length > 0)
				throw ex;
		}
	}

	/**
	 * 注册延迟执行操作。
	 * 
	 * @param operation
	 *            具体的延迟执行操作。
	 * @return 返回对象本身
	 * @throws IllegalStateException
	 *             如果本对象已经关闭，则不允许进行注册。
	 */
	public final Deferred defer(DeferredOperation operation)
			throws IllegalStateException {
		if (_shutdown.get()) {
			throw new IllegalStateException();
		}
		_stack.push(operation);
		return this;
	}

}
