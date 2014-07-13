package me.icymint.sloth.module;

import me.icymint.sloth.module.Module.Scheduler;

/**
 * 可调度插件。
 * 
 * @author Daniel
 *
 */
public interface ScheduledPlugin extends ExecutionThreadPlugin {

	/**
	 * 请不要实现该方法。
	 */
	@Override
	default void execute(Module module) throws Exception {
		schedule().execute(this, module);
	}

	/**
	 * 按照一定时间间隔运行该代码。
	 * 
	 * @throws RuntimeException
	 *             执行错误抛出违例。
	 */
	void runOneIteration() throws RuntimeException;

	/**
	 * 如何调度执行。
	 * 
	 * @return 返回调度器。
	 */
	Scheduler schedule();
}
