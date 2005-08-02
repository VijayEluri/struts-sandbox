/*
 * $Id: StrutsTiServlet.java 170184 2005-05-14 23:54:24Z martinc $
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

package org.apache.ti.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.MissingResourceException;

import org.apache.commons.chain.*;
import org.apache.commons.chain.web.*;
import org.apache.commons.chain.web.servlet.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.springframework.beans.factory.xml.*;
import org.springframework.beans.factory.*;
import org.springframework.core.io.*;

import org.apache.commons.digester.*;

import org.apache.ti.processor.RequestProcessor;

/**
 * <p><strong>StrutsTiServlet</strong> is the entry point into Struts Ti.</p>
 * @version $Rev: 170184 $ $Date: 2005-05-14 16:54:24 -0700 (Sat, 14 May 2005) $
 */
public class StrutsTiServlet extends HttpServlet {
    
    public static final String SERVLET_MAPPINGS_KEY = "servletMappings";
    
    protected String springConfig = "org/apache/ti/config/spring-config-servlet.xml";
    
    protected static Log log = LogFactory.getLog(StrutsTiServlet.class);
    
    protected BeanFactory beanFactory = null;
    protected List servletMappings = new ArrayList();
    protected RequestProcessor processor = null;
    
    public void destroy() {
        
        processor.destroy();
        beanFactory = null;
        processor = null;
    }



    /**
     * <p>Initialize this servlet.  Most of the processing has been factored
     * into support methods so that you can override particular functionality
     * at a fairly granular level.</p>
     *
     * @exception ServletException if we cannot configure ourselves correctly
     */
    public void init() throws ServletException {
        super.init();
        
        initSpring();
        initServlet();
        
        Map initParameters = new HashMap();
        String key;
        for (Enumeration e = getInitParameterNames(); e.hasMoreElements(); ) {
            key = (String) e.nextElement();
            initParameters.put(key, getInitParameter(key));
        }
        initParameters.put(SERVLET_MAPPINGS_KEY, servletMappings);
        
        processor = (RequestProcessor) beanFactory.getBean("requestProcessor");
        processor.init(initParameters, new ServletWebContext(getServletContext(), null, null));
    }
    
    protected void initSpring() throws ServletException {
        // Parse the configuration file specified by path or resource
        try {
            String paths = getInitParameter("springConfig");
            if (paths != null) {
                springConfig = paths;
            }

            URL resource = resolve(springConfig);
            log.info("Loading spring configuration from " + resource);
            beanFactory = new XmlBeanFactory(new UrlResource(resource));
        } catch (Exception e) {
            String msg = "Exception loading spring configuration";
            log.error(msg, e);
            throw new UnavailableException(msg);
        }   
    }
    

    /**
     * <p>Perform the standard request processing for this request, and create
     * the corresponding response.</p>
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception is thrown
     */
    public void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        processor.process(new ServletWebContext(getServletContext(), request, response));
    }
    
    
    protected URL resolve(String path) throws ServletException {
        URL resource = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = this.getClass().getClassLoader();
        }
        
        try {
            if (path.charAt(0) == '/') {
                resource = getServletContext().getResource(path);
            }
    
            if (resource == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to locate " + path
                            + " in the servlet context, "
                            + "trying classloader.");
                }
                Enumeration e = loader.getResources(path);
                if (!e.hasMoreElements()) {
                    String msg = "Resource not found: "+path;
                    log.error(msg);
                    throw new UnavailableException(msg);
                } else {
                    resource = (URL) e.nextElement();
                    if (e.hasMoreElements()) {
                        log.warn("Found more than one resource at "+path
                            +", only using the first");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw new UnavailableException("Unable to load resource at "+path);
        }
        
        return resource;
    }
    
    /**
     * <p>Remember a servlet mapping from our web application deployment
     * descriptor, if it is for this servlet.</p>
     *
     * @param servletName The name of the servlet being mapped
     * @param urlPattern The URL pattern to which this servlet is mapped
     */
    public void addServletMapping(String servletName, String urlPattern) {

        if (log.isDebugEnabled()) {
            log.debug("Process servletName=" + servletName
                    + ", urlPattern=" + urlPattern);
        }
        String myServletName = getServletConfig().getServletName();
        
        if (servletName != null && servletName.equals(myServletName)) {
            servletMappings.add(urlPattern);
        }

    }
    
    /**
     * <p>Initialize the servlet mapping under which our controller servlet
     * is being accessed.  This will be used in the <code>&html:form&gt;</code>
     * tag to generate correct destination URLs for form submissions.</p>
     *
     * @throws ServletException if error happens while scanning web.xml
     */
    protected void initServlet() throws ServletException {

        // Prepare a Digester to scan the web application deployment descriptor
        Digester digester = new Digester();
        digester.push(this);
        digester.setNamespaceAware(true);
        digester.setValidating(false);

        // Configure the processing rules that we need
        digester.addCallMethod("web-app/servlet-mapping",
                               "addServletMapping", 2);
        digester.addCallParam("web-app/servlet-mapping/servlet-name", 0);
        digester.addCallParam("web-app/servlet-mapping/url-pattern", 1);

        // Process the web application deployment descriptor
        if (log.isDebugEnabled()) {
            log.debug("Scanning web.xml for controller servlet mapping");
        }

        InputStream input =
            getServletContext().getResourceAsStream("/WEB-INF/web.xml");

        String err = "Unable to process web.xml";    
        if (input == null) {
            throw new ServletException(err);
        }

        try {
            digester.parse(input);

        } catch (IOException e) {
            log.error(err, e);
            throw new ServletException(e);

        } catch (SAXException e) {
            log.error(err, e);
            throw new ServletException(e);

        } finally {
            try {
                input.close();
            } catch (IOException e) {
                log.error(err, e);
                throw new ServletException(e);
            }
        }
    }

}
