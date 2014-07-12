package me.icymint.sloth.defer;

/**
 * 延迟执行方法接口
 * 
 * @author Daniel
 *
 */
@FunctionalInterface
public interface DeferredOperation {
	/**
	 * 需要延迟执行的内容。
	 * 
	 * @throws Exception
	 *             执行出错抛出违例。
	 */
	void execute() throws Exception;
}
