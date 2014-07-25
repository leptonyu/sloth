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

import java.net.URL;
import java.util.Map;

import jodd.util.StringTemplateParser;
import me.icymint.sloth.core.json.JsonObject;
import me.icymint.sloth.jetty.HttpMethods;
import me.icymint.sloth.jetty.HttpSupport;

import org.eclipse.jetty.http.HttpMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

/**
 * @author Daniel Yu
 */
@HttpSupport("/v1")
public class WeChatApiV1 implements WeChatApi {
	private final WeChatPlugin _wechat;
	private final Map<String, String> _map;
	private final StringTemplateParser _stp = new StringTemplateParser();
	private static final String author = "https://api.weixin.qq.com/cgi-bin/token?grant_type=${grant_type}&appid=${appid}&secret=${secret}";

	public WeChatApiV1(WeChatPlugin wechat) {
		_wechat = wechat;
		JsonObject conf = _wechat.getContext().configuration()
				.getValue("wechat");
		_map = Maps.newConcurrentMap();
		_map.put("grant_type", conf.getValue("grant_type").asString(""));
		_map.put("appid", conf.getValue("appid").asString());
		_map.put("secret", conf.getValue("secret").asString(""));
	}

	@Override
	@HttpMethods(path = "", value = { HttpMethod.GET })
	public String get() {
		return "hello";
	}

	public void updateAuthority() throws Exception {
		ObjectNode node = new ObjectMapper()
				.readValue(new URL(_stp.parse(author, k -> _map.get(k))),
						ObjectNode.class);
		JsonNode access_token = node.get("access_token");
		if (access_token != null) {
			_wechat.setOption("access_token", access_token.asText());
			_wechat.setOption("expires_in", node.get("expires_in").asText());
			return;
		}
		throw new Exception();
	}
}
