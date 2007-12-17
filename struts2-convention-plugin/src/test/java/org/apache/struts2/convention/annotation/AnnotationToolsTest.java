/*
 * Copyright (c) 2007, Inversoft and Texturemedia, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.apache.struts2.convention.annotation;

import junit.framework.TestCase;

import org.apache.struts2.convention.actions.DefaultResultPathAction;
import org.apache.struts2.convention.actions.namespace.ActionLevelNamespaceAction;

/**
 * <p>
 * This class tests the annotation tools.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class AnnotationToolsTest extends TestCase {
    public void testFindAnnotationOnClass() {
        ResultPath rl = AnnotationTools.findAnnotation(DefaultResultPathAction.class, ResultPath.class);
        assertNotNull(rl);
        assertEquals(rl.value(), "/WEB-INF/location");
    }

    public void testFindAnnotationOnPackage() {
        Namespace ns = AnnotationTools.findAnnotation(ActionLevelNamespaceAction.class, Namespace.class);
        assertNotNull(ns);
        assertEquals(ns.value(), "/package-level");
    }
}