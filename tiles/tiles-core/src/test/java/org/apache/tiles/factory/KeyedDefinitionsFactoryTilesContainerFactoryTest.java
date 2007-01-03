/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.tiles.factory;

import junit.framework.TestCase;

import javax.servlet.ServletContext;

import org.easymock.EasyMock;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.impl.KeyedDefinitionsFactoryTilesContainer;

import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * @version $Rev$ $Date$
 */
public class KeyedDefinitionsFactoryTilesContainerFactoryTest extends TestCase {

    private ServletContext context;
    
    private Map<String, String> defaults;

    public void setUp() {
        context = EasyMock.createMock(ServletContext.class);
        defaults = new HashMap<String, String>();
        defaults.put(TilesContainerFactory.CONTAINER_FACTORY_INIT_PARAM,
                KeyedDefinitionsFactoryTilesContainerFactory.class.getName());
    }

    public void testGetFactory() throws TilesException {
        Vector<String> v = new Vector<String>();

        EasyMock.expect(context.getInitParameterNames()).andReturn(v.elements());
        EasyMock.expect(context.getInitParameter(TilesContainerFactory.CONTAINER_FACTORY_INIT_PARAM)).andReturn(null);
        EasyMock.replay(context);
        TilesContainerFactory factory = TilesContainerFactory.getFactory(context,
                defaults);
        assertNotNull(factory);
        assertEquals(KeyedDefinitionsFactoryTilesContainerFactory.class,
                factory.getClass());
    }

    public void testCreateContainer() throws TilesException, MalformedURLException {
        Vector enumeration = new Vector();
        EasyMock.expect(context.getInitParameter(TilesContainerFactory.CONTAINER_FACTORY_INIT_PARAM)).andReturn(null);
        EasyMock.expect(context.getInitParameter(TilesContainerFactory.CONTEXT_FACTORY_INIT_PARAM)).andReturn(null);
        EasyMock.expect(context.getInitParameter(TilesContainerFactory.DEFINITIONS_FACTORY_INIT_PARAM)).andReturn(null);
        EasyMock.expect(context.getInitParameter(
                KeyedDefinitionsFactoryTilesContainerFactory.CONTAINER_KEYS_INIT_PARAM))
                .andReturn("one,two").anyTimes();
        EasyMock.expect(context.getInitParameter(BasicTilesContainer.DEFINITIONS_CONFIG + "@one"))
                .andReturn("/WEB-INF/tiles-one.xml").anyTimes();
        EasyMock.expect(context.getInitParameter(BasicTilesContainer.DEFINITIONS_CONFIG + "@two"))
                .andReturn("/WEB-INF/tiles-two.xml").anyTimes();
        EasyMock.expect(context.getInitParameter(EasyMock.isA(String.class))).andReturn(null).anyTimes();
        EasyMock.expect(context.getInitParameterNames()).andReturn(enumeration.elements()).anyTimes();
        URL url = getClass().getResource("test-defs.xml");
        EasyMock.expect(context.getResource("/WEB-INF/tiles.xml")).andReturn(url);
        url = getClass().getResource("test-defs-key-one.xml");
        EasyMock.expect(context.getResource("/WEB-INF/tiles-one.xml")).andReturn(url);
        url = getClass().getResource("test-defs-key-two.xml");
        EasyMock.expect(context.getResource("/WEB-INF/tiles-two.xml")).andReturn(url);
        EasyMock.replay(context);

        TilesContainerFactory factory = TilesContainerFactory.getFactory(context, defaults);
        TilesContainer container = factory.createContainer(context);

        assertNotNull(container);
        assertTrue("The container is not an instance of KeyedDefinitionsFactoryTilesContainer",
                container instanceof KeyedDefinitionsFactoryTilesContainer);
        KeyedDefinitionsFactoryTilesContainer keyedContainer =
            (KeyedDefinitionsFactoryTilesContainer) container;
        assertNotNull(keyedContainer.getDefinitionsFactory());
        assertNotNull(keyedContainer.getDefinitionsFactory("one"));
        assertNotNull(keyedContainer.getDefinitionsFactory("two"));
        //now make sure it's initialized
        try {
            container.init(new HashMap<String, String>());
            fail("Container should have already been initialized");
        }
        catch (IllegalStateException te) {
        }

    }
}
