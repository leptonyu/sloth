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
package me.icymint.sloth.core.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import me.icymint.sloth.Deferred;
import me.icymint.sloth.core.module.Module;
import me.icymint.sloth.core.module.Plugin;

/**
 * Abstract context which implements interface Plugin. This context load
 * configurations from a configuration file which located in class path. Sub
 * classes which extends AbstractContext should provide a way to read
 * configurations from InputStream, and then use the created configuration
 * object to initial the context.
 * 
 * @author Daniel
 *
 */
public abstract class AbstractContext<P> implements Plugin {

	private P p = null;

	/**
	 * 
	 * @return Get the configuration object.
	 */
	public P configuration() {
		return p;
	}

	/**
	 * initial the context after create the configuration object.
	 * 
	 * @param context
	 *            The Module object which creates this Plugin.
	 * @param deferred
	 *            Deferred container used to collection clean operations to run
	 *            when closing the Plugin.
	 * @param properties
	 *            Configuration object created by
	 *            {@link #loadFromStream(InputStream)}.
	 * @param configpath
	 *            Class path in which the configuration file was found.
	 * @throws Exception
	 *             init error
	 */
	protected abstract void initAndDefer(Module context, Deferred deferred,
			P properties, File configpath) throws Exception;

	@Override
	public final void initAndDeferClose(Module provider, Deferred deferred)
			throws Exception {
		// Fetch the Declared Annotation.
		ContextConfiguration ccf = getClass().getDeclaredAnnotation(
				ContextConfiguration.class);
		if (ccf == null) {
			throw new ContextAnnotationNotFound(getClass().getName());
		}
		File configpath = null;
		P p = null;
		// Default value means no configuration file.
		if (!"".equals(ccf.value())) {
			URL url = getClass().getResource(ccf.value());
			if (url == null) {
				throw new FileNotFoundException(ccf.value());
			}
			String f = url.getFile();
			f = f.substring(0, f.length() - ccf.value().length());
			configpath = new File(f).getAbsoluteFile();
			try (InputStream input = url.openStream()) {
				p = loadFromStream(input);
			}
		}
		this.p = p;
		// Next step
		initAndDefer(provider, deferred, p, configpath);
	}

	/**
	 * create Configuration object from InputStream.
	 * 
	 * @param input
	 *            InputStream from file specified by annotation
	 *            {@link ContextConfiguration}
	 * @return Configuraton object, such as {@link Properties} etc.
	 * @throws IOException
	 *             Can not create target configuration object from InputStream.
	 */
	protected abstract P loadFromStream(InputStream input) throws IOException;
}
