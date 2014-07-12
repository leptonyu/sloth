package me.icymint.sloth.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定模块静态配置的类路径。
 * 
 * @author Daniel
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContextConfiguration {
	/**
	 * 
	 * @return 上下文配置文件对应的Properties文件路径，返回null表示不设置静态配置文件。
	 */
	String path() default "";
}
