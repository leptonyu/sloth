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
package me.icymint.sloth.wechat;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jodd.util.StringTemplateParser;
import me.icymint.sloth.core.defer.Deferred;
import me.icymint.sloth.core.json.JsonObject;
import me.icymint.sloth.core.module.Module;
import me.icymint.sloth.core.module.Plugin;
import me.icymint.sloth.core.module.RequirePlugins;
import me.icymint.sloth.jetty.JettyPlugin;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author Daniel Yu
 */
@RequirePlugins(JettyPlugin.class)
public class WeChatPlugin implements Plugin {
	private final Map<String, Cache<String, ? extends Object>> _map = Maps
			.newConcurrentMap();
	private QueryRunner _dbutil;
	private JettyPlugin jp;

	public QueryRunner dbutil() {
		return _dbutil;
	}

	@SuppressWarnings("unchecked")
	public <V extends Object> Cache<String, V> getCache(String name) {
		Cache<String, ?> cache = _map.get(name);
		if (cache != null) {
			return (Cache<String, V>) cache;
		}
		synchronized (_map) {
			cache = _map.get(name);
			if (cache != null) {
				return (Cache<String, V>) cache;
			}
			Cache<String, V> newmake = CacheBuilder.newBuilder()
					.initialCapacity(1000).expireAfterAccess(1, TimeUnit.HOURS)
					.build();
			_map.put(name, newmake);
			return newmake;
		}
	}

	public JettyPlugin getContext() {
		return jp;
	}

	public String getOption(String key) {
		try {
			return (String) getCache("commons").get(
					key,
					() -> dbutil().query(
							"select value from commons where key=?",
							new ScalarHandler<String>(), key));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void initAndDeferClose(Module module, Deferred deferred)
			throws Exception {
		jp = module.fetch(JettyPlugin.class);
		JsonObject db = jp.configuration().getValue("database");
		File dbfolder = new File(jp.getBaseDirectory(), "db");
		File dbfile = new File(dbfolder, "wechat");
		boolean init = !dbfolder.exists();
		// Datasource
		ComboPooledDataSource ds = new ComboPooledDataSource();
		Map<String, String> map = new HashMap<>();
		map.put("base", jp.getBaseDirectory().getAbsolutePath());
		map.put("db", dbfile.getAbsolutePath());
		StringTemplateParser stp = new StringTemplateParser();
		ds.setDriverClass(db.getValue("driver").asString("org.h2.Driver"));
		String url = stp
				.parse(db.getValue("url").asString(
						"jdbc:h2:" + dbfile.getAbsolutePath()), k -> map.get(k));
		System.out.println(url);
		ds.setJdbcUrl(url);
		deferred.defer(ds::close);
		_dbutil = new QueryRunner(ds);
		if (init) {
			dbfolder.mkdirs();
			JsonObject crt = db.getValue("create");
			if (crt != null) {
				for (String name : crt.keys()) {
					JsonObject table = crt.getValue(name);
					JsonObject vars = table.getValue("vars");
					String sql = null;
					for (String var : vars.keys()) {
						if (sql == null) {
							sql = "";
						} else {
							sql += ",";
						}
						sql += var + " " + vars.getValue(var).asString();
					}
					JsonObject keys = table.getValue("keys");
					if (keys != null)
						sql += ", PRIMARY KEY ("
								+ Joiner.on(",").join(
										Lists.transform(keys.asList(),
												jo -> jo.asString())) + ") ";
					JsonObject fkeys = table.getValue("fkeys");
					if (fkeys != null)
						for (String fk : fkeys.keys()) {
							sql += ","
									+ String.format(
											"FOREIGN KEY (%s) REFERENCES %s ON DELETE CASCADE ON UPDATE CASCADE",
											fk, fkeys.getValue(fk).asString());
						}
					sql = String.format("create table %s (%s)", name, sql);
					System.out.println(sql);
					_dbutil.update(sql);
				}
			}
			JsonObject ist = db.getValue("insert");
			if (ist != null) {
				for (String name : ist.keys()) {
					for (JsonObject data : ist.getValue(name).asList()) {
						List<Object> line = Lists.transform(data.asList(),
								d -> d.asObject());
						String sql = String.format(
								"INSERT INTO %s VALUES (%s)",
								name,
								Joiner.on(",").join(
										Lists.transform(line, o -> "?")));
						System.out.println(sql);
						_dbutil.update(sql, line.toArray());
					}
				}
			}
		}
		WeChatControl wcc = new WeChatControl(this, jp, module);
		wcc.setId(jp.register(0, wcc));
	}

	public void setOption(String key, String value) throws SQLException {
		try {
			dbutil().update("insert into commons values(?,?)", key, value);
		} catch (SQLException e) {
			dbutil().update("update commons set value=? where key=?", value,
					key);
		}
		getCache("commons").put(key, value);
	}
}
