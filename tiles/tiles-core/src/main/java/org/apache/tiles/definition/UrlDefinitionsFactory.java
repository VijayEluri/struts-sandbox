/*
 * $Id$
 *
 * Copyright 1999-2004 The Apache Software Foundation.
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
package org.apache.tiles.definition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.TilesRequestContext;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.util.RequestUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * {@link DefinitionsFactory DefinitionsFactory} implementation
 * that manages ComponentDefinitions configuration data from URLs.
 * <p/>
 * <p>The ComponentDefinition objects are read from the
 * {@link org.apache.tiles.definition.digester.DigesterDefinitionsReader DigesterDefinitionsReader}
 * class unless another implementation is specified.</p>
 *
 * @version $Rev$ $Date$
 */
public class UrlDefinitionsFactory
    implements DefinitionsFactory, ReloadableDefinitionsFactory {

    /**
     * LOG instance for all UrlDefinitionsFactory instances.
     */
    private static final Log LOG = LogFactory.getLog(UrlDefinitionsFactory.class);

    /**
     * Contains the URL objects identifying where configuration data is found.
     */
    protected List<Object> sources;

    /**
     * Reader used to get definitions from the sources.
     */
    protected DefinitionsReader reader;

    /**
     * Contains the dates that the URL sources were last modified.
     */
    protected Map<String, Long> lastModifiedDates;

    /**
     * Contains a list of locales that have been processed.
     */
    private List<Locale> processedLocales;


    private ComponentDefinitions definitions;

    /**
     * Creates a new instance of UrlDefinitionsFactory
     */
    public UrlDefinitionsFactory() {
        sources = new ArrayList<Object>();
        lastModifiedDates = new HashMap<String, Long>();
        processedLocales = new ArrayList<Locale>();
    }

    /**
     * Initializes the DefinitionsFactory and its subcomponents.
     * <p/>
     * Implementations may support configuration properties to be passed in via
     * the params Map.
     *
     * @param params The Map of configuration properties.
     * @throws DefinitionsFactoryException if an initialization error occurs.
     */
    public void init(Map<String, String> params) throws DefinitionsFactoryException {
        String readerClassName =
            params.get(DefinitionsFactory.READER_IMPL_PROPERTY);

        if (readerClassName != null) {
            createReader(readerClassName);
        } else {
            reader = new DigesterDefinitionsReader();
        }
        reader.init(params);
    }

    private ComponentDefinitions getComponentDefinitions()
        throws DefinitionsFactoryException {
        if (definitions == null) {
            definitions = readDefinitions();
        }
        return definitions;
    }


    /**
     * Returns a ComponentDefinition object that matches the given name and
     * Tiles context
     *
     * @param name         The name of the ComponentDefinition to return.
     * @param tilesContext The Tiles context to use to resolve the definition.
     * @return the ComponentDefinition matching the given name or null if none
     *         is found.
     * @throws DefinitionsFactoryException if an error occurs reading definitions.
     */
    public ComponentDefinition getDefinition(String name,
                                             TilesRequestContext tilesContext)
        throws DefinitionsFactoryException {

        ComponentDefinitions definitions = getComponentDefinitions();
        Locale locale = tilesContext.getRequestLocale();
        if (!isLocaleProcessed(tilesContext)) {
            synchronized (definitions) {
                addDefinitions(definitions, tilesContext);
            }
        }

        return definitions.getDefinition(name, locale);
    }

    /**
     * Adds a source where ComponentDefinition objects are stored.
     * <p/>
     * Implementations should publish what type of source object they expect.
     * The source should contain enough information to resolve a configuration
     * source containing definitions.  The source should be a "base" source for
     * configurations.  Internationalization and Localization properties will be
     * applied by implementations to discriminate the correct data sources based
     * on locale.
     *
     * @param source The configuration source for definitions.
     * @throws DefinitionsFactoryException if an invalid source is passed in or
     *                                     an error occurs resolving the source to an actual data store.
     */
    public void addSource(Object source) throws DefinitionsFactoryException {
        if (source == null) {
            throw new DefinitionsFactoryException(
                "Source object must not be null");
        }

        if (!(source instanceof URL)) {
            throw new DefinitionsFactoryException(
                "Source object must be an URL");
        }

        sources.add(source);
    }

    /**
     * Appends locale-specific {@link ComponentDefinition} objects to an existing
     * {@link ComponentDefinitions} set by reading locale-specific versions of
     * the applied sources.
     *
     * @param definitions  The ComponentDefinitions object to append to.
     * @param tilesContext The requested locale.
     * @throws DefinitionsFactoryException if an error occurs reading definitions.
     */
    protected void addDefinitions(ComponentDefinitions definitions,
                                  TilesRequestContext tilesContext)
        throws DefinitionsFactoryException {

        Locale locale = tilesContext.getRequestLocale();
        List<String> postfixes = calculatePostixes(locale);

        if (isLocaleProcessed(tilesContext)) {
            return;
        } else {
            processedLocales.add(locale);
        }

        for (Object source : sources) {
            URL url = (URL) source;
            String path = url.toExternalForm();

            for (Object postfixe : postfixes) {
                String newPath = concatPostfix(path, (String) postfixe);
                try {
                    URL newUrl = new URL(newPath);
                    URLConnection connection = newUrl.openConnection();
                    connection.connect();
                    lastModifiedDates.put(newUrl.toExternalForm(),
                        connection.getLastModified());
                    Map defsMap = reader.read(connection.getInputStream());
                    definitions.addDefinitions(defsMap,
                        tilesContext.getRequestLocale());
                } catch (FileNotFoundException e) {
                    // File not found. continue.
                } catch (IOException e) {
                    throw new DefinitionsFactoryException(
                        "I/O error processing configuration.");
                }
            }
        }
    }

    /**
     * Creates and returns a {@link ComponentDefinitions} set by reading
     * configuration data from the applied sources.
     *
     * @throws DefinitionsFactoryException if an error occurs reading the
     *                                     sources.
     */
    public ComponentDefinitions readDefinitions()
        throws DefinitionsFactoryException {
        ComponentDefinitions definitions = new ComponentDefinitionsImpl();
        try {
            for (Object source1 : sources) {
                URL source = (URL) source1;
                URLConnection connection = source.openConnection();
                connection.connect();
                lastModifiedDates.put(source.toExternalForm(),
                    connection.getLastModified());
                Map defsMap = reader.read(connection.getInputStream());
                definitions.addDefinitions(defsMap);
            }
        } catch (IOException e) {
            throw new DefinitionsFactoryException("I/O error accessing source.", e);
        }
        return definitions;
    }

    /**
     * Indicates whether a given locale has been processed or not.
     * <p/>
     * This method can be used to avoid unnecessary synchronization of the
     * DefinitionsFactory in multi-threaded situations.  Check the return of
     * isLoacaleProcessed before synchronizing the object and reading
     * locale-specific definitions.
     *
     * @param tilesContext The Tiles context to check.
     * @return true if the given lcoale has been processed and false otherwise.
     */
    protected boolean isLocaleProcessed(TilesRequestContext tilesContext) {
        return processedLocales.contains(tilesContext.getRequestLocale());
    }

    /**
     * Concat postfix to the name. Take care of existing filename extension.
     * Transform the given name "name.ext" to have "name" + "postfix" + "ext".
     * If there is no ext, return "name" + "postfix".
     *
     * @param name    Filename.
     * @param postfix Postfix to add.
     * @return Concatenated filename.
     */
    protected String concatPostfix(String name, String postfix) {
        if (postfix == null) {
            return name;
        }

        // Search file name extension.
        // take care of Unix files starting with .
        int dotIndex = name.lastIndexOf(".");
        int lastNameStart = name.lastIndexOf(java.io.File.pathSeparator);
        if (dotIndex < 1 || dotIndex < lastNameStart) {
            return name + postfix;
        }

        String ext = name.substring(dotIndex);
        name = name.substring(0, dotIndex);
        return name + postfix + ext;
    }

    /**
     * Calculate the postixes along the search path from the base bundle to the
     * bundle specified by baseName and locale.
     * Method copied from java.util.ResourceBundle
     *
     * @param locale the locale
     * @return a list of
     */
    protected static List<String> calculatePostixes(Locale locale) {
        final List<String> result = new ArrayList<String>();
        final String language = locale.getLanguage();
        final int languageLength = language.length();
        final String country = locale.getCountry();
        final int countryLength = country.length();
        final String variant = locale.getVariant();
        final int variantLength = variant.length();

        if (languageLength + countryLength + variantLength == 0) {
            //The locale is "", "", "".
            return result;
        }

        final StringBuffer temp = new StringBuffer();
        temp.append('_');
        temp.append(language);

        if (languageLength > 0)
            result.add(temp.toString());

        if (countryLength + variantLength == 0)
            return result;

        temp.append('_');
        temp.append(country);

        if (countryLength > 0)
            result.add(temp.toString());

        if (variantLength == 0) {
            return result;
        } else {
            temp.append('_');
            temp.append(variant);
            result.add(temp.toString());
            return result;
        }
    }


    public void refresh() throws DefinitionsFactoryException {
        LOG.debug("Updating Tiles definitions. . .");
        synchronized (definitions) {
            ComponentDefinitions newDefs = readDefinitions();
            definitions.reset();
            definitions.addDefinitions(newDefs.getBaseDefinitions());
        }
    }


    /**
     * Indicates whether the DefinitionsFactory is out of date and needs to be
     * reloaded.
     */
    public boolean refreshRequired() {
        boolean status = false;

        Set<String> urls = lastModifiedDates.keySet();

        try {
            for (String urlPath : urls) {
                Long lastModifiedDate = lastModifiedDates.get(urlPath);
                URL url = new URL(urlPath);
                URLConnection connection = url.openConnection();
                connection.connect();
                long newModDate = connection.getLastModified();
                if (newModDate != lastModifiedDate) {
                    status = true;
                    break;
                }
            }
        } catch (Exception e) {
            LOG.warn("Exception while monitoring update times.", e);
            return true;
        }
        return status;
    }

    private void createReader(String readerClassName) throws DefinitionsFactoryException {
        try {
            Class readerClass =
                RequestUtils.applicationClass(readerClassName);
            reader = (DefinitionsReader) readerClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new DefinitionsFactoryException(
                "Cannot find reader class '" + readerClassName + "'.", e);
        } catch (InstantiationException e) {
            throw new DefinitionsFactoryException(
                "Unable to instantiate reader class '" + readerClassName + "'.", e);
        } catch (IllegalAccessException e) {
            throw new DefinitionsFactoryException(
                "Unable to access reader class '" + readerClassName + "'.", e);
        }
    }
}
