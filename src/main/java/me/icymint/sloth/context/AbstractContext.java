package me.icymint.sloth.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import me.icymint.sloth.defer.Deferred;
import me.icymint.sloth.module.Module;
import me.icymint.sloth.module.Plugin;

/**
 * 抽象上下文，提供了一个标准的加载方案。本上下文的实现类必须在类名称上注解 {@link ContextConfiguration}，否则将初始化报错。
 * 
 * @author Daniel
 *
 */
public abstract class AbstractContext implements Plugin {

	/**
	 * 实现本上下文的后续初始化工作。
	 * 
	 * @param context
	 *            模块上下文
	 * @param deferred
	 *            延迟执行注册对象
	 * @param properties
	 *            静态配置表
	 * @param configpath
	 *            配置文件所在的路径，可以指定相对目录，若静态配置表不存在，则该值为null。
	 */
	protected abstract void initAndDefer(Module context, Deferred deferred,
			Properties properties, File configpath);

	@Override
	public final void initAndDeferClose(Module provider, Deferred deferred)
			throws Exception {
		// 解析实现类的注解。
		ContextConfiguration ccf = getClass().getAnnotation(
				ContextConfiguration.class);
		if (ccf == null) {
			throw new ContextAnnotationNofFound(getClass().getName());
		}
		File configpath = null;
		Properties prop = new Properties();
		// 默认值表示没有静态配置
		if (!"".equals(ccf.path())) {
			URL url = getClass().getResource(ccf.path());
			if (url == null) {
				// 无法获取静态配置。
				throw new FileNotFoundException(ccf.path());
			}
			String f = url.getFile();
			f = f.substring(0, f.length() - ccf.path().length());
			configpath = new File(f).getAbsoluteFile();
			try (InputStream input = url.openStream()) {
				prop.load(input);
			}
		}
		// 下一步初始化
		initAndDefer(provider, deferred, prop, configpath);
	}
}
