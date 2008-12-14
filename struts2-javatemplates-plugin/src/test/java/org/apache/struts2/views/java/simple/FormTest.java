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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.views.java.simple;

import org.apache.struts2.components.Select;
import org.apache.struts2.components.Form;
import org.apache.struts2.components.UrlRenderer;
import org.easymock.EasyMock;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormTest extends AbstractTestCase {
    private FormEx tag;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tag = new FormEx(stack, request, response);
        UrlRenderer renderer = EasyMock.createNiceMock(UrlRenderer.class);
        EasyMock.replay(renderer);
        tag.setUrlRenderer(renderer);
    }

    public void testRenderForm() {
        tag.setName("name_");
        tag.setDisabled("true");
        tag.setTabindex("1");
        tag.setId("id_");
        tag.setCssClass("class_");
        tag.setCssStyle("style_");
        tag.setTitle("title");
        tag.setAcceptcharset("charset_");
        tag.setAction("action_");
        tag.setOnsubmit("submit");
        tag.setOnreset("reset");
        tag.setTarget("target_");
        tag.setEnctype("enc");
        tag.setMethod("post");

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("form", context);
        String output = writer.getBuffer().toString();
        String expected = s("<form name='name_' id='id_' onsubmit='submit' onreset='reset' target='target_' enctype='enc' class='class_' style='style_' title='title' accept-charset='charset_' method='post'></form>");
        assertEquals(expected, output);
    }

    public void testDefaultMethod() {
        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("form", context);
        String output = writer.getBuffer().toString();
        String expected = s("<form method='post'></form>");
        assertEquals(expected, output);
    }


    public void testRenderScriptingAttrs() {

        applyScriptingAttrs(tag);

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("form", context);
        String output = writer.getBuffer().toString();

        assertScriptingAttrs(output);
    }

    public void testRenderCommonAttrs() {

        applyCommonAttrs(tag);

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("form", context);
        String output = writer.getBuffer().toString();

        assertCommongAttrs(output);
    }

    class FormEx extends Form {
        public FormEx(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
            super(stack, request, response);
        }

        public void processParams() {
            //these methods are protected
            evaluateParams();
        }

        public boolean altSyntax() {
            return true;
        }
    }
}
