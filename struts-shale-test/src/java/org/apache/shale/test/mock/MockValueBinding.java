/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.shale.test.mock;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.component.StateHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;


/**
 * <p>Mock implementation of <code>ValueBinding</code>.</p>
 *
 * <p>This implementation is subject to the following restrictions:</p>
 * <ul>
 * <li>Looking up the first name via the configured <code>VariableResolver</code>
 *     (which is also limited in capability).</li>
 * <li>Resolving the "." operator via the configured
 *     <code>PropertyResolver</code>.</li>
 * <li>Supports only <code>getValue()</code> and <code>setValue()</code>.</li>
 * </ul>
 */

public class MockValueBinding extends ValueBinding implements StateHolder {


    // ------------------------------------------------------------ Constructors


    public MockValueBinding() {

	this(null, null);

    }


    public MockValueBinding(Application application, String ref) {

        this.application = application;
	if (ref != null) {
	    if (ref.startsWith("#{") && ref.endsWith("}")) {
		ref = ref.substring(2, ref.length() - 1);
	    }
	}
        this.ref = ref;

    }


    // ----------------------------------------------------- Mock Object Methods


    public String ref(){

        return this.ref;

    }


    // ------------------------------------------------------ Instance Variables


    private transient Application application; // Restored as necessary
    private String ref;


    // ---------------------------------------------------- ValueBinding Methods


    public Object getValue(FacesContext context)
        throws EvaluationException, PropertyNotFoundException {

        if (context == null) {
            throw new NullPointerException();
        }
        List names = parse(ref);

        // Resolve the variable name
        VariableResolver vr = application().getVariableResolver();
        String name = (String) names.get(0);
        Object base = vr.resolveVariable(context, name);
        if (names.size() < 2) {
            return (base);
        }

        // Resolve the property names
        PropertyResolver pr = application().getPropertyResolver();
        for (int i = 1; i < names.size(); i++) {
            base = pr.getValue(base, (String) names.get(i));
        }

        // Return the resolved value
        return (base);

    }


    public void setValue(FacesContext context, Object value)
        throws EvaluationException, PropertyNotFoundException {

        if (context == null) {
            throw new NullPointerException();
        }
        List names = parse(ref);

        // Resolve the variable name
        VariableResolver vr = application().getVariableResolver();
        String name = (String) names.get(0);
        Object base = vr.resolveVariable(context, name);
        if (names.size() < 2) {
            if ("applicationScope".equals(name) ||
                "requestScope".equals(name) ||
                "sessionScope".equals(name)) {
                throw new ReferenceSyntaxException("Cannot set '" +
                                                   name + "'");
            }
            Map map = econtext().getRequestMap();
            if (map.containsKey(name)) {
                map.put(name, value);
                return;
            }
            map = econtext().getSessionMap();
            if ((map != null) && (map.containsKey(name))) {
                map.put(name, value);
                return;
            }
            map = econtext().getApplicationMap();
            if (map.containsKey(name)) {
                map.put(name, value);
                return;
            }
            econtext().getRequestMap().put(name, value);
            return;
        }

        // Resolve the property names
        PropertyResolver pr = application().getPropertyResolver();
        for (int i = 1; i < (names.size() - 1); i++) {
            // System.out.println("  property=" + names.get(i));
            base = pr.getValue(base, (String) names.get(i));
        }

        // Update the last property
        pr.setValue(base, (String) names.get(names.size() - 1), value);

    }


    public boolean isReadOnly(FacesContext context)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }


    public Class getType(FacesContext context)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }

    public String getExpressionString() {
	return "#{" + ref + "}";
    }


    // ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {

        Object values[] = new Object[1];
	values[0] = ref;
	return values;

    }


    public void restoreState(FacesContext context, Object state) {

        Object values[] = (Object[]) state;
        ref = (String) values[0];

    }


    private boolean transientFlag = false;


    public boolean isTransient() {

        return this.transientFlag;

    }


    public void setTransient(boolean transientFlag) {

        this.transientFlag = transientFlag;

    }


    // --------------------------------------------------------- Private Methods


    private Application application() {

	if (application == null) {
	    application = FacesContext.getCurrentInstance().getApplication();
	}
	return (application);

    }


    private ExternalContext econtext() {

        return (FacesContext.getCurrentInstance().getExternalContext());

    }


    private List parse(String ref) {

        String expr = ref;
        List names = new ArrayList();
        while (expr.length() > 0) {
            int period = expr.indexOf(".");
            if (period >= 0) {
                names.add(expr.substring(0, period));
                expr = expr.substring(period + 1);
            } else {
                names.add(expr);
                expr = "";
            }
        }
        if (names.size() < 1) {
            throw new ReferenceSyntaxException("No expression in '" +
                                               ref + "'");
        }
        for (int i = 0; i < names.size(); i++) {
            String name = (String) names.get(i);
            if (name.length() < 1) {
                throw new ReferenceSyntaxException("Invalid expression '" +
                                                   ref + "'");
            }
        }
        return (names);

    }


}
