package me.icymint.sloth.wechat;

import me.icymint.sloth.core.module.Module;
import me.icymint.sloth.jetty.HttpMethods;
import me.icymint.sloth.jetty.HttpSupport;
import me.icymint.sloth.jetty.JettyPlugin;

import org.eclipse.jetty.http.HttpMethod;

/**
 * @author Daniel Yu
 */
@HttpSupport("/admin")
public class WeChatControl {

	private final WeChatPlugin _wechat;
	private final JettyPlugin _jetty;
	private final Module _module;
	private volatile long _id = -1;

	protected WeChatControl(WeChatPlugin weChatPlugin, JettyPlugin jp,
			Module module) {
		_wechat = weChatPlugin;
		_jetty = jp;
		_module = module;
	}

	@HttpMethods(path = "/restart", value = { HttpMethod.PUT })
	public void restart() {
		_jetty.unregister(_id);
		WeChatControl wcc = new WeChatControl(_wechat, _jetty, _module);
		wcc.setId(_jetty.register(0, wcc));
	}

	void setId(long id) {
		_id = id;
	}
}
