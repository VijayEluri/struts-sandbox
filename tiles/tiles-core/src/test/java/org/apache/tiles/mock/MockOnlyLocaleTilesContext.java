/*
 * $Id: MockOnlyLocaleTilesContext.java 350273 2005-12-01 16:50:58Z greddin $
 *
 * Copyright 1999-2006 The Apache Software Foundation.
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

package org.apache.tiles.mock;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.TilesContext;

/**
 * Creates a TilesContext that contains only a Locale
 * 
 * @version $Rev: 350273 $ $Date: 2005-12-01 17:50:58 +0100 (gio, 01 dic 2005) $
 */
public class MockOnlyLocaleTilesContext implements TilesContext {

    /**
     * The locale object.
     */
    private Locale locale;

    /** Creates a new instance of MockOnlyLocaleTilesContext.
     * 
     * @param locale The locale object to use.
     */
    public MockOnlyLocaleTilesContext(Locale locale) {
        this.locale = locale;
    }

    /**
     * Returns the locale specified in the constructor.
     * 
     * @see org.apache.tiles.TilesContext#getRequestLocale()
     */
    public Locale getRequestLocale() {
        return locale;
    }

    // The rest of the implemented methods has a "dummy" behaviour, doing
    // nothing or returning null, because they are not needed at all in tests
    // that use this class.
    
    public void dispatch(String path) throws IOException, Exception {
    }

    public Map getApplicationScope() {
        return null;
    }

    public Map getHeader() {
        return null;
    }

    public Map getHeaderValues() {
        return null;
    }

    public Map getInitParams() {
        return null;
    }

    public Map getParam() {
        return null;
    }

    public Map getParamValues() {
        return null;
    }

    public Map getRequestScope() {
        return null;
    }

    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

    public Map getSessionScope() {
        return null;
    }

    public void include(String path) throws IOException, Exception {
    }
}
