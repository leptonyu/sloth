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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate the implementation classes of {@link Plugin} to define the required
 * plugins of the target Plugin.
 * <p>
 * This annotation can be inherit from super classes or interfaces as if they
 * extends the interface {@link Plugin}. Sub plugin can not drop the required
 * plugins required by it's super plugin.
 * 
 * 
 * @author Daniel
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePlugins {
	/**
	 * 
	 * 
	 * @return Required plugins' classes.
	 */
	Class<? extends Plugin>[] value() default {};
}
