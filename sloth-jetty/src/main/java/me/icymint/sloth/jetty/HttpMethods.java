package me.icymint.sloth.jetty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.jetty.http.HttpMethod;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpMethods {
	HttpMethod[] value() default { HttpMethod.GET };

	String path();
}
