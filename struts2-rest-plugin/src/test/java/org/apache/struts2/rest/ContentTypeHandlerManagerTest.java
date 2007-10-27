/*
 * $Id: Restful2ActionMapper.java 540819 2007-05-23 02:48:36Z mrdon $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.rest;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.inject.Container;
import junit.framework.TestCase;
import org.apache.struts2.rest.handler.ContentTypeHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class ContentTypeHandlerManagerTest extends TestCase {

    public void testHandlerOverride() {
        Mock mockHandlerXml = new Mock(ContentTypeHandler.class);
        mockHandlerXml.matchAndReturn("getExtension", "xml");
        mockHandlerXml.matchAndReturn("toString", "xml");
        Mock mockHandlerJson = new Mock(ContentTypeHandler.class);
        mockHandlerJson.matchAndReturn("getExtension", "json");
        mockHandlerJson.matchAndReturn("toString", "json");
        Mock mockHandlerXmlOverride = new Mock(ContentTypeHandler.class);
        mockHandlerXmlOverride.matchAndReturn("getExtension", "xml");
        mockHandlerXmlOverride.matchAndReturn("toString", "xmlOverride");

        Mock mockContainer = new Mock(Container.class);
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(ContentTypeHandler.class), C.eq("xmlOverride")), mockHandlerXmlOverride.proxy());
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(ContentTypeHandler.class), C.eq("xml")), mockHandlerXml.proxy());
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(ContentTypeHandler.class), C.eq("json")), mockHandlerJson.proxy());
        mockContainer.expectAndReturn("getInstanceNames", C.args(C.eq(ContentTypeHandler.class)), new HashSet(Arrays.asList("xml", "xmlOverride", "json")));

        mockContainer.matchAndReturn("getInstance", C.args(C.eq(String.class),
                C.eq(ContentTypeHandlerManager.STRUTS_REST_HANDLER_OVERRIDE_PREFIX+"xml")), "xmlOverride");
        mockContainer.expectAndReturn("getInstance", C.args(C.eq(String.class),
                C.eq(ContentTypeHandlerManager.STRUTS_REST_HANDLER_OVERRIDE_PREFIX+"json")), null);
        
        ContentTypeHandlerManager mgr = new ContentTypeHandlerManager();
        mgr.setContainer((Container) mockContainer.proxy());

        Map<String,ContentTypeHandler> handlers = mgr.handlers;
        assertNotNull(handlers);
        assertEquals(2, handlers.size());
        assertEquals(mockHandlerXmlOverride.proxy(), handlers.get("xml"));
        assertEquals(mockHandlerJson.proxy(), handlers.get("json"));
    }
}
