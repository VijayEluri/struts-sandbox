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

package org.apache.tiles;

import java.io.Serializable;
import java.util.Map;

/**
 * Common implementation of attribute definition.
 *
 * @version $Rev$ $Date$
 */
public class ComponentAttribute implements Serializable {

    /**
     * Attribute of type definition.
     */
    public static final String DEFINITION = "definition";

    /**
     * Attribute of type template.
     */
    public static final String TEMPLATE = "template";

    /**
     * Attribute of type string.
     */
    public static final String STRING = "string";

    /**
     * Role associated to this attribute.
     */
    protected String role = null;

    protected Object value = null;

    private String type = null;

    private String name = null;

    private Map<String, ComponentAttribute> attributes;

    /**
     * Constructor.
     *
     */
    public ComponentAttribute() {
    }

    /**
     * Constructor.
     *
     * @param value Object to store.
     */
    public ComponentAttribute(Object value) {
        this.value = value;
    }

    /**
     * Constructor.
     *
     * @param name name of the attribute
     * @param value Object to store.
     */
    public ComponentAttribute(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Constructor.
     *
     * @param value Object to store.
     * @param role  Asociated role.
     */
    public ComponentAttribute(Object value, String role) {
        this.value = value;
        this.role = role;
    }

    /**
     * Constructor.
     *
     * @param value Object to store.
     * @param role  Asociated role.
     * @param type  Attribute type.
     */
    public ComponentAttribute(Object value, String role, String type) {
        this.value = value;
        this.role = role;
        this.type = type;
    }

    /**
     * Get role.
     * @return the name of the required role(s)
     */
    public String getRole() {
        return role;
    }

    /**
     * Set role.
     *
     * @param role Associated role.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Get value.
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set value.
     *
     * @param value New value.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Get String representation of this object.
     */
    public String toString() {
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Map<String, ComponentAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, ComponentAttribute> attributes) {
        this.attributes = attributes;
    }

    public void setBody(String body) {
        if (body != null && body.length() != 0) {
            setValue(body);
        }
    }
}
