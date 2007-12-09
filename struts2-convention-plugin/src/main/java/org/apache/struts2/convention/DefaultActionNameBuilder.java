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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.inject.Inject;

/**
 * <p>
 * This class does nothing but strip the word <b>Action</b> from the end
 * of the class name and possible lower case the name as well.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultActionNameBuilder implements ActionNameBuilder {
    private static final String ACTION = "Action";
    private boolean lowerCase;

    @Inject
    public DefaultActionNameBuilder(@Inject(value="struts.convention.action.name.lowercase") String lowerCase) {
        this.lowerCase = Boolean.parseBoolean(lowerCase);
    }

    public String build(String className) {
        String actionName = className;

        // Truncate Action suffix if found
        if (actionName.endsWith(ACTION)) {
            actionName = actionName.substring(0, actionName.length() - ACTION.length());
        }

        // Force initial letter of action to lowercase, if desired
        if ((lowerCase) && (actionName.length() > 1)) {
            int lowerPos = actionName.lastIndexOf('/') + 1;
            StringBuilder sb = new StringBuilder();
            sb.append(actionName.substring(0, lowerPos));
            sb.append(Character.toLowerCase(actionName.charAt(lowerPos)));
            sb.append(actionName.substring(lowerPos + 1));
            actionName = sb.toString();
        }

        return actionName;
    }
}