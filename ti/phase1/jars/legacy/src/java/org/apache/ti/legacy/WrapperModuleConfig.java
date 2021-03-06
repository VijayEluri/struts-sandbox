/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.legacy;

import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ControllerConfig;
import org.apache.struts.config.DataSourceConfig;
import org.apache.struts.config.ExceptionConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.MessageResourcesConfig;
import org.apache.struts.config.PlugInConfig;
import org.apache.struts.config.ActionConfig;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Wrapper for a Struts 1.x ModuleConfig based on an XWork PackageConfig.  Using a wrapper object
 * allows us to be explicit about what is and isn't implemented.
 */
class WrapperModuleConfig implements ModuleConfig {

    private PackageConfig delegate;
    private Map _actionMappings;
    private Map _exceptionConfigs;
    private Map _actionForwards;

    public WrapperModuleConfig(String packageName) {
        delegate = ConfigurationManager.getConfiguration().getPackageConfig(packageName);
    }

    /**
     * Add Struts ActionMappings (from XWork ExceptionConfigs).
     */
    private void initActionMappings() {

        if (_actionMappings == null) {
            _actionMappings = new HashMap();
            StrutsFactory strutsFactory = StrutsFactory.getStrutsFactory();
            for (Iterator i = delegate.getActionConfigs().entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String actionPath = '/' + (String) entry.getKey();
                com.opensymphony.xwork.config.entities.ActionConfig actionConfig =
                        (com.opensymphony.xwork.config.entities.ActionConfig) entry.getValue();
                _actionMappings.put(actionPath, strutsFactory.createActionMapping(actionConfig, actionPath, this));
            }
        }
    }

    /**
     * Add Struts ExceptionConfigs (from XWork ExceptionMappingConfigs).
     */
    private void initExceptionConfigs() {
        if (_exceptionConfigs == null) {
            _exceptionConfigs = new HashMap();
            StrutsFactory strutsFactory = StrutsFactory.getStrutsFactory();
            for (Iterator i = delegate.getGlobalExceptionMappingConfigs().iterator(); i.hasNext();) {
                ExceptionMappingConfig config = (ExceptionMappingConfig) i.next();
                _exceptionConfigs.put(config.getExceptionClassName(), strutsFactory.createExceptionConfig(config));
            }
        }
    }

    /**
     * Add Struts ActionForwards (from XWork ResultConfigs).
     */
    private void initActionForwards() {
        if (_actionForwards == null) {
            _actionForwards = new HashMap();
            StrutsFactory strutsFactory = StrutsFactory.getStrutsFactory();
            for (Iterator i = delegate.getGlobalResultConfigs().entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String name = (String) entry.getKey();
                ResultConfig config = (ResultConfig) entry.getValue();
                _actionForwards.put(name, strutsFactory.createActionForward(config));
            }
        }
    }

    public String getPrefix() {
        return delegate.getNamespace();
    }

    public void setPrefix(String prefix) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public boolean getConfigured() {
        return true;
    }

    public ControllerConfig getControllerConfig() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setControllerConfig(ControllerConfig cc) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getActionFormBeanClass() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setActionFormBeanClass(String actionFormBeanClass) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getActionMappingClass() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setActionMappingClass(String actionMappingClass) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void addActionConfig(ActionConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void addDataSourceConfig(DataSourceConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void addExceptionConfig(ExceptionConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void addFormBeanConfig(FormBeanConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getActionForwardClass() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setActionForwardClass(String actionForwardClass) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void addForwardConfig(ForwardConfig config) {
        throw new UnsupportedOperationException("NYI");
    }

    public void addMessageResourcesConfig(MessageResourcesConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void addPlugInConfig(PlugInConfig plugInConfig) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public ActionConfig findActionConfig(String path) {
        initActionMappings();
        return (ActionConfig) _actionMappings.get(path);
    }

    public ActionConfig[] findActionConfigs() {
        initActionMappings();
        return (ActionConfig[]) _actionMappings.values().toArray(new ActionConfig[_actionMappings.size()]);
    }

    public DataSourceConfig findDataSourceConfig(String key) {
        throw new UnsupportedOperationException("NYI");
    }

    public DataSourceConfig[] findDataSourceConfigs() {
        throw new UnsupportedOperationException("NYI");
    }

    public ExceptionConfig findExceptionConfig(String type) {
        initExceptionConfigs();
        return (ExceptionConfig) _exceptionConfigs.get(type);
    }

    public ExceptionConfig[] findExceptionConfigs() {
        initExceptionConfigs();
        return (ExceptionConfig[]) _exceptionConfigs.values().toArray(new ExceptionConfig[_exceptionConfigs.size()]);
    }

    public FormBeanConfig findFormBeanConfig(String name) {
        throw new UnsupportedOperationException("NYI");
    }

    public FormBeanConfig[] findFormBeanConfigs() {
        throw new UnsupportedOperationException("NYI");
    }

    public ForwardConfig findForwardConfig(String name) {
        initActionForwards();
        return (ForwardConfig) _actionForwards.get(name);
    }

    public ForwardConfig[] findForwardConfigs() {
        initActionForwards();
        return (ForwardConfig[]) _actionForwards.values().toArray(new ForwardConfig[_actionForwards.size()]);
    }

    public MessageResourcesConfig findMessageResourcesConfig(String key) {
        throw new UnsupportedOperationException("NYI");
    }

    public MessageResourcesConfig[] findMessageResourcesConfigs() {
        throw new UnsupportedOperationException("NYI");
    }

    public PlugInConfig[] findPlugInConfigs() {
        throw new UnsupportedOperationException("NYI");
    }

    public void freeze() {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void removeActionConfig(ActionConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void removeExceptionConfig(ExceptionConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void removeDataSourceConfig(DataSourceConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void removeFormBeanConfig(FormBeanConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void removeForwardConfig(ForwardConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void removeMessageResourcesConfig(MessageResourcesConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }
}
