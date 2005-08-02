/*
 * $Id: ServletRequestHandler.java 170184 2005-05-14 23:54:24Z martinc $
 *
 * Copyright 2000-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ti.processor.chain;

import org.apache.commons.chain.web.*;
import org.apache.commons.chain.*;
import org.apache.commons.logging.*;
import org.apache.ti.config.mapper.*;
import org.apache.ti.config.*;
import org.apache.ti.processor.*;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.*;

import java.util.*;


/**
 *  Creates an ActionProxy instance
 */
public class CreateActionProxy implements Command {
    
    protected static final Log log = LogFactory.getLog(CreateActionProxy.class);
   
    public boolean execute(Context origctx) {
        WebContext ctx = (WebContext)origctx;
        
        ActionMapping mapping = (ActionMapping)ctx.get("actionMapping");
        ActionProxy proxy = getActionProxy(ctx, mapping);

        ctx.put("actionProxy", proxy);
        return false;
    }
 
    protected ActionProxy getActionProxy(WebContext ctx, ActionMapping mapping) {
        
        // request map wrapping the http request objects
        Map requestMap = ctx.getRequestScope();

        // parameters map wrapping the http paraneters.
        Map params = mapping.getParams();
        Map requestParams = ctx.getParamValues();
        if (params != null) {
            params.putAll(requestParams);
        } else {
            params = requestParams;
        }

        HashMap extraContext = createContextMap(requestMap, params, ctx.getSessionScope(), ctx.getApplicationScope(), ctx);

        // If there was a previous value stack, then create a new copy and pass it in to be used by the new Action
        //OgnlValueStack stack = (OgnlValueStack) requestMap.get(ServletActionContext.WEBWORK_VALUESTACK_KEY);
        //if (stack != null) {
        //    extraContext.put(ActionContext.VALUE_STACK, new OgnlValueStack(stack));
        //}
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(mapping.getNamespace(), mapping.getName(), extraContext);
            //request.setAttribute(ServletActionContext.WEBWORK_VALUESTACK_KEY, proxy.getInvocation().getStack());
            return proxy;
        } catch (ConfigurationException e) {
            log.error("Could not find action", e);
            throw new ProcessorException(e);
        } catch (Exception e) {
            log.error("Could not execute action", e);
            throw new ProcessorException(e);
        }
    }
        
    /**
     * Merges all application and servlet attributes into a single <tt>HashMap</tt> to represent the entire
     * <tt>Action</tt> context.
     *
     * @param requestMap     a Map of all request attributes.
     * @param parameterMap   a Map of all request parameters.
     * @param sessionMap     a Map of all session attributes.
     * @param applicationMap a Map of all servlet context attributes.
     * @return a HashMap representing the <tt>Action</tt> context.
     */
    public HashMap createContextMap(Map requestMap,
                                    Map parameterMap,
                                    Map sessionMap,
                                    Map applicationMap,
                                    WebContext ctx) {
        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, new HashMap(parameterMap));
        extraContext.put(ActionContext.SESSION, sessionMap);
        extraContext.put(ActionContext.APPLICATION, applicationMap);
        //extraContext.put(ActionContext.LOCALE, (locale == null) ? request.getLocale() : locale);

        extraContext.put("webContext", ctx);

        // helpers to get access to request/session/application scope
        extraContext.put("request", requestMap);
        extraContext.put("session", sessionMap);
        extraContext.put("application", applicationMap);
        extraContext.put("parameters", parameterMap);

        return extraContext;
    }

   
    
}
