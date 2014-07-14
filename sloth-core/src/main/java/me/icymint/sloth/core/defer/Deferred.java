/*
 * Copyright (C) 2014 Daniel Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.icymint.sloth.core.defer;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A container collecting {@link DeferredOperation} objects which need execute
 * later. It uses a {@link Stack} to hold them, and executes
 * {@link DeferredOperation} objects by order of last-in-first-out (LIFO).
 * <p>
 * 
 * @author Daniel Yu
 * @see DeferredOperation
 * @see Stack
 */
public final class Deferred implements AutoCloseable {
	/**
	 * Create a new Deferred object.
	 * 
	 * @return new created Deferred object.
	 */
	public static Deferred create() {
		return new Deferred();
	}

	private final AtomicBoolean _shutdown = new AtomicBoolean(false);
	private final Stack<DeferredOperation> _stack = new Stack<>();

	private Deferred() {
	}

	/**
	 * Execute all the operations in container and close this object.
	 */
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
	 * Add the {@link DeferredOperation} object to containers.
	 * 
	 * @param operation
	 *            target {@link DeferredOperation} object.
	 * @return {@link Deferred} object itself.
	 * @throws IllegalStateException
	 *             if the object has been closed by running {@link #close()},
	 *             then throws IllegalStateException exception.
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
