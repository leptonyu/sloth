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
package me.icymint.sloth.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

/**
 * Simple Handler from
 * {@link Handler#handle(String, Request, HttpServletRequest, HttpServletResponse)}
 * 
 * @author Daniel Yu
 */
public interface SimpleHandler {
	/**
	 * Handle a request.
	 * 
	 * @param target
	 *            The target of the request - either a URI or a name.
	 * @param baseRequest
	 *            The original unwrapped request object.
	 * @param request
	 *            The request either as the {@link Request} object or a wrapper
	 *            of that request. The
	 *            {@link HttpChannel#getCurrentHttpChannel()} method can be used
	 *            access the Request object if required.
	 * @param response
	 *            The response as the {@link Response} object or a wrapper of
	 *            that request. The {@link HttpChannel#getCurrentHttpChannel()}
	 *            method can be used access the Response object if required.
	 * @throws IOException
	 *             io exception
	 * @throws ServletException
	 *             servlet exception
	 */
	void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException,
			Exception;
}
