package me.icymint.sloth.wechat;

import java.net.URL;
import java.nio.charset.Charset;

import me.icymint.sloth.core.json.JsonObject;
import me.icymint.sloth.core.module.Module;
import me.icymint.sloth.jetty.JettyPlugin;

import org.junit.Test;

import com.google.common.io.Resources;

/**
 * @author Daniel Yu
 */
public class WeChatTest {
	@Test
	public void testWechatPluginWorks() throws Exception {
		Module module = Module.create(WeChatPlugin.class);
		module.init();
		try {
			JettyPlugin jp = module.fetch(JettyPlugin.class);
			WeChatPlugin wechat = module.fetch(WeChatPlugin.class);
			long id = jp.register(1, new WeChatApiV1(wechat));
			JsonObject config = jp.configuration().getValue("jetty");
			String baseurl = "http://127.0.0.1:"
					+ config.getValue("port").asInt(8080)
					+ config.getValue("api").asString("/api");
			System.out.println(Resources.toString(new URL(baseurl + "/v1"),
					Charset.forName("UTF-8")));
			jp.unregister(id);
		} finally {
			module.close();
		}
	}
}
