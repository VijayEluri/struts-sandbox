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

import static javax.servlet.http.HttpServletResponse.*;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default implementation of rest info that uses fluent-style construction
 */
public class DefaultRestInfo implements RestInfo {
    String resultCode;
    int status = SC_OK;
    Object etag;
    Object locationId;
    String location;
    boolean disableCaching;
    Date lastModified;
    
    public DefaultRestInfo renderResult(String code) {
        this.resultCode = code;
        return this;
    }
    
    public DefaultRestInfo withStatus(int code) {
        this.status = code;
        return this;
    }
    
    public DefaultRestInfo withETag(Object etag) {
        this.etag = etag;
        return this;
    }
    
    public DefaultRestInfo setLocationId(Object id) {
        this.locationId = id;
        return this;
    }
    
    public DefaultRestInfo setLocation(String loc) {
        this.location = loc;
        return this;
    }
    
    public DefaultRestInfo lastModified(Date date) {
        this.lastModified = date;
        return this;
    }
    
    public DefaultRestInfo disableCaching() {
        this.disableCaching = true;
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts2.rest.RestInfo#apply(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    public String apply(HttpServletRequest request, HttpServletResponse response, Object target) {
        response.setStatus(status);
        if (disableCaching) {
            response.setHeader("Cache-Control", "no-cache");
        } else if (lastModified != null) {
            response.setDateHeader("LastModified", lastModified.getTime());
        } else {
            if (etag == null) {
                etag = String.valueOf(target.hashCode());
            }
            response.setHeader("ETag", etag.toString());
        }
        if (locationId != null) {
            String url = request.getRequestURL().toString();
            int lastSlash = url.lastIndexOf("/");
            int lastDot = url.lastIndexOf(".");
            if (lastDot > lastSlash && lastDot > -1) {
                url = url.substring(0, lastDot)+locationId+url.substring(lastDot);
            } else {
                url += locationId;
            }
            response.setHeader("Location", url);
        } else if (location != null) {
            response.setHeader("Location", location);
        }
        return resultCode;
    }

    public int getStatus() {
        return status;
    }
    
    
    
    
}