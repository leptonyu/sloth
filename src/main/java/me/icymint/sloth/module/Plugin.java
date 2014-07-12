package me.icymint.sloth.module;

import me.icymint.sloth.defer.Deferred;

/**
 * 标准插件接口，其可以被模块自动构建，必须符合一些条件：
 * <p>
 * 1. 具有公共的空参数构建器。<br>
 * 2. 使用{@link DepPlugins}来注解依赖插件，没有的话可以不写。
 * <p>
 * 插件的生命周期绑定在模块上面，因此插件随着模块的创建而创建，关闭而关闭。不能如子模块一样可以主动关闭。
 * 
 * @author Daniel
 * @see DepPlugins
 * @see Module
 */
@FunctionalInterface
public interface Plugin {

	/**
	 * 初始化插件，并把卸载操作注册到模块中。在实现本方法的时候，不可以使用这些方法：{@link Module#fork(Class...)}和
	 * {@link Module#close()}和 {@link Module#init()}，否则将抛出意想不到的违例。
	 * <p>
	 * 该方法的执行期间，可以使用{@link Module#get(Class)}获取其依赖的插件(插件的依赖关系通过注解
	 * {@link DepPlugins}
	 * 来定义)，这些插件已经完成初始化。但是不要在这期间使用同一模块中的其它插件，对于没有依赖关系的插件，它们的初始化过程时随机的
	 * ，原则上除非明确依赖关系 ，否则不要使用没有依赖关系的插件。
	 * <p>
	 * 
	 * 
	 * @param context
	 *            所属模块对象。其用于提供
	 * @param deferred
	 *            用于注册关闭动作。
	 * @throws Exception
	 *             初始化失败的时候抛出违例。
	 * @see Module#get(Class)
	 */
	void initAndDeferClose(Module context, Deferred deferred) throws Exception;
}
