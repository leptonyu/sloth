package me.icymint.sloth.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解插件之间的关系。
 * 
 * @author Daniel
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DepPlugins {
	/**
	 * 依赖的插件列表。
	 * 
	 * @return 依赖插件的类。
	 */
	Class<? extends Plugin>[] value() default {};
}
