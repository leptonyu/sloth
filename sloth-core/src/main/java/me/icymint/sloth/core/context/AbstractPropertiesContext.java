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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Use Properties object as the target Configuration object.
 * 
 * @author Daniel
 *
 */
public abstract class AbstractPropertiesContext extends
		AbstractContext<Properties> {
	@Override
	protected final Properties loadFromStream(InputStream input)
			throws IOException {
		Properties p = new Properties();
		p.load(input);
		return p;
	}
}
