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
package me.icymint.sloth.core.module;

import me.icymint.sloth.Deferred;

/**
 * Standard plugin, the implementation of this interface must follow the
 * following rules:
 * <p>
 * 1. Have a public constructor with no argument.<br>
 * 2. Use {@link RequirePlugins} to require the required Plugins used by this
 * plugin. <br>
 * 3. Only required plugins can be used in the target Plugin.
 * <p>
 * All the plugins are binding to a {@link Module} object. {@link Module} manage
 * plugins, it creates and runs the plugins when it is initializing, and destroy
 * the plugins when it is destroying.
 * 
 * @author Daniel
 * @see RequirePlugins
 * @see Module
 */
@FunctionalInterface
public interface Plugin {

	/**
	 * Initialize the plugins, and register the destroy operations using
	 * {@link Deferred} object. In this method, {@link Module#fork(Class...)}
	 * can not be used, it always throws IllegalStateException when invoke this
	 * method. Also {@link Module#close()} and {@link Module#init()} are
	 * forbidden.
	 * <p>
	 * When executing this method, all of the required plugins, which are
	 * annotated by {@link RequirePlugins}, have completed the initialization
	 * and are working properly. Use {@link Module#fetch(Class)} to get the
	 * specified Plugin object. Plugins that are not required by this Plugin
	 * should not be used by this method.
	 * <p>
	 * 
	 * 
	 * @param module
	 *            The Module object who owns this Plugin object.
	 * @param deferred
	 *            Deferred object.
	 * @throws Exception
	 *             Initialize error will throws Exception.
	 * @see Module#fetch(Class)
	 * @see RequirePlugins
	 */
	void initAndDeferClose(Module module, Deferred deferred) throws Exception;

	/**
	 * 
	 * @return name of the plugin.
	 */
	default String name() {
		return getClass().getSimpleName();
	}
}
