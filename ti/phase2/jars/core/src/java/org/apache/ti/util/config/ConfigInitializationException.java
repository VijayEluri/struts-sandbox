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
package org.apache.ti.util.config;


/**
 * <p/>
 * Exception thrown when an error occurs loading the NetUI configuration file
 * into a web application.
 * </p>
 */
public class ConfigInitializationException
        extends RuntimeException {
    /**
     * Default constructor.
     */
    public ConfigInitializationException() {
        super();
    }

    /**
     * @param message
     */
    public ConfigInitializationException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ConfigInitializationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ConfigInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}