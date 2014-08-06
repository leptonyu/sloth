package me.icymint.sloth.lang;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum Identifier {
	GOTO("goto"), imp, IMPORT("import"), defer, IF("if"), ELSE("else"), WHILE(
			"while"), CLASS("class"), def, func, //
	LT("<"), GT(">"), GE(">="), LE("<="), EQ("=="), LBB("{"), RBB("}"), LSB("("), RSB(
			")"), LMB("["), RMB("]"), E("="), DEFINE(":="), DOT("."), END(";"), //
	PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVID("/"), MOD("%");

	public static Identifier valueOf(Value<Object> v) {
		return v == null ? null : map.get(v.getValue());
	}

	private static final Map<Object, Identifier> map = new ConcurrentHashMap<>();
	static {
		for (Identifier id : values()) {
			map.put(id.v, id);
		}
	}

	private final Object v;

	private Identifier() {
		this(null);
	}

	private Identifier(Object v) {
		this.v = v == null ? name() : v;
	}

	public boolean is(Value<Object> v) {
		if (v == null)
			return false;
		return v.getValue().equals(this.v);
	}

	public String value() {
		return (String) v;
	}
}
