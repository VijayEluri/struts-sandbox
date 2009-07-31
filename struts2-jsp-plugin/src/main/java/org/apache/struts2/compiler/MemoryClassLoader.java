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
package org.apache.struts2.compiler;

import javax.tools.JavaFileObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryClassLoader extends ClassLoader {
    private Map<String, MemoryJavaFileObject> cachedObjects = new ConcurrentHashMap<String, MemoryJavaFileObject>();

    @Override
    protected Class<?> findClass(String name) throws
            ClassNotFoundException {
        MemoryJavaFileObject fileObject = cachedObjects.get(name);
        if (fileObject != null) {
            byte[] bytes = fileObject.toByteArray();
            return defineClass(name, bytes, 0, bytes.length);
        }
        return super.findClass(name);
    }

    public void addMemoryJavaFileObject(String jsp, MemoryJavaFileObject memoryJavaFileObject) {
        cachedObjects.put(jsp, memoryJavaFileObject);
    }
}