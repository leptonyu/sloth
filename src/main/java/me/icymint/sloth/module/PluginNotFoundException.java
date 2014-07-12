package me.icymint.sloth.module;

public class PluginNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1707944767440148587L;

	public PluginNotFoundException(String name) {
		super(String.format("Plugin %s NOT found!", name));
	}

}
