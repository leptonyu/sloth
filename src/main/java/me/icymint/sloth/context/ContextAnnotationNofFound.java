package me.icymint.sloth.context;

public class ContextAnnotationNofFound extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1257448520556231638L;

	public ContextAnnotationNofFound(String name) {
		super(String.format("Annotation %s NOT found in class %s!",
				ContextConfiguration.class.getName(), name));
	}

}
