/*
 * Copyright (C) 2014 Daniel Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.icymint.sloth.core.json;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Json object wrapper.
 * 
 * @author Daniel Yu
 */
public class JsonObject {

	private final Object _source;
	private final Map<String, JsonObject> _map;
	private final List<JsonObject> _list;

	/**
	 * Wrapper an json like object.
	 * 
	 * @param source
	 *            target object.
	 */
	public JsonObject(Object source) {
		_map = createMap(source);
		_list = _map == null ? createList(source) : null;
		_source = (_map == null && _list == null) ? source : null;
	}

	/**
	 * 
	 * @return read the wrapped object as integer.
	 * @throws NumberFormatException
	 *             the wrapped object is not an integer type throws exception.
	 */
	public int asInt() throws NumberFormatException {
		Integer a = source(Integer.class);
		if (a != null)
			return a;
		throw new NumberFormatException();
	}

	/**
	 * 
	 * @param defaultValue
	 *            if the wrapped object is not an integer then will return this.
	 * @return read the wrapped object as integer. if not return defaultValue.
	 */
	public int asInt(int defaultValue) {
		Integer a = source(Integer.class);
		if (a != null)
			return a;
		return defaultValue;
	}

	/**
	 * 
	 * @return read the wrapped object as Long integer.
	 * @throws NumberFormatException
	 *             the wrapped object is not a Long integer type throws
	 *             exception.
	 */
	public long asLong() {
		Long a = source(Long.class);
		if (a != null)
			return a;
		throw new NumberFormatException();
	}

	/**
	 * 
	 * @param defaultValue
	 *            if the wrapped object is not an Long integer then will return
	 *            this.
	 * @return read the wrapped object as Long integer. if not return
	 *         defaultValue.
	 */
	public long asLong(long defaultValue) {
		Long a = source(Long.class);
		if (a != null)
			return a;
		return defaultValue;
	}

	/**
	 * 
	 * @return the wrapped object without class cast.
	 */
	public Object asObject() {
		return _source;
	}

	/**
	 * 
	 * @return read the wrapped object as Short integer.
	 * @throws NumberFormatException
	 *             the wrapped object is not a Short integer type throws
	 *             exception.
	 */
	public short asShort() {
		Short a = source(Short.class);
		if (a != null)
			return a;
		throw new NumberFormatException();
	}

	/**
	 * 
	 * @param defaultValue
	 *            if the wrapped object is not an Short integer then will return
	 *            this.
	 * @return read the wrapped object as Short integer. if not return
	 *         defaultValue.
	 */
	public short asShort(short defaultValue) {
		Short a = source(Short.class);
		if (a != null)
			return a;
		return defaultValue;
	}

	/**
	 * 
	 * @return read the wrapped object as String, if this object is not a type
	 *         of String then will return null.
	 */
	public String asString() {
		return source(String.class);
	}

	/**
	 * 
	 * @param defaultValue
	 *            if the wrapped object is not a String then will return this.
	 * @return read the wrapped object as String, if not return defaultValue.
	 */
	public String asString(String defaultValue) {
		String x = asString();
		return x == null ? defaultValue : x;
	}

	private List<JsonObject> createList(Object source) {
		if (List.class.isInstance(source)) {
			@SuppressWarnings("unchecked")
			List<Object> list = List.class.cast(source);
			List<JsonObject> xx = new LinkedList<>();
			list.forEach(obj -> xx.add(new JsonObject(obj)));
			return xx;
		}
		return null;
	}

	private Map<String, JsonObject> createMap(Object source) {
		if (Map.class.isInstance(source)) {
			Map<String, JsonObject> map = new HashMap<>();
			@SuppressWarnings("unchecked")
			Map<String, Object> xx = Map.class.cast(source);
			xx.forEach((k, v) -> map.put(k, new JsonObject(v)));
			return map;
		} else {
			return null;
		}
	}

	/**
	 * Assume the current wrapped object as {@link List} and try to find the
	 * child {@link JsonObject} with certain index index. if the current wrapped
	 * object is not a {@link List} or there is no child JsonObject with the
	 * given index then return null.
	 * 
	 * @param index
	 *            index of the potential child {@link JsonObject}.
	 * @return child {@link JsonObject} with the given index in the wrapped
	 *         {@link List}.
	 */
	public JsonObject get(int index) {
		return _list != null ? _list.get(index) : null;
	}

	/**
	 * Assume the current wrapped object as {@link Map} and try to find the
	 * child JsonObject with certain name. If the current wrapped object is not
	 * a {@link Map} or there is no child {@link JsonObject} with the given name
	 * then return null.
	 * 
	 * @param key
	 *            name of the potential child {@link JsonObject}.
	 * @return child {@link JsonObject} with the given name in the wrapped
	 *         {@link Map}.
	 */
	public JsonObject getValue(String key) {
		return _map != null ? _map.get(key) : null;
	}

	private <T> T source(Class<T> clazz) {
		if (_source != null && clazz != null && clazz.isInstance(_source)) {
			return clazz.cast(_source);
		}
		return null;
	}

	/**
	 * 
	 * @return if keys of the wrapped Map, if it is not map then return a empty
	 *         set and will not return null.
	 */
	public Set<String> keys() {
		return _map != null ? _map.keySet() : new HashSet<>();
	}

	/**
	 * 
	 * @return the list of JsonObject.
	 */
	public List<JsonObject> asList() {
		return _list != null ? _list : new LinkedList<>();
	}
}
