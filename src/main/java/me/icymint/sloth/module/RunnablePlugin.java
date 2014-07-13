package me.icymint.sloth.module;

/**
 * 可运行的插件，本插件在初始化结束以后，将会被模块运行。
 * 
 * @author Daniel
 *
 */
public interface RunnablePlugin extends Plugin {
	/**
	 * 运行该插件，请注意安全结束的问题，如果需要一直运行，则请使用如下方案，保证能够顺利退出：
	 * 
	 * <pre>
	 * public void execute(Module module) throws Exception {
	 * 	while (module.isReady()) {
	 * 		// 这里完成插件的工作，原则上这部分代码运行的时间越短越好。
	 * 	}
	 * }
	 * </pre>
	 *
	 * @param module
	 *            插件所在的模块。
	 * @throws Exception
	 *             插件运行失败。
	 */
	void execute(Module module) throws Exception;
}
