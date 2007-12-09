/*
 * Copyright (c) 2001-2007, Inversoft, All Rights Reserved
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * This class is a copy from the Java.net Commons repository. The unit tests
 * are in that package and modifications to this class should be migrated back
 * to that repository.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractClassLoaderResolver<T> {
    private static final Logger logger = Logger.getLogger(AbstractClassLoaderResolver.class.getName());

    /**
     * The set of matches being accumulated.
     */
    private Set<T> matches = new HashSet<T>();

    /**
     * The ClassLoader to use when looking for classes. If null then the ClassLoader returned
     * by Thread.currentThread().getContextClassLoader() will be used.
     */
    private ClassLoader classloader;

    /**
     * Provides access to the matches discovered so far. If no calls have been made to any of the
     * {@code find()} methods, this set will be empty.
     *
     * @return  The set of matches that have been discovered.
     */
    public Set<T> getMatches() {
        return matches;
    }

    /**
     * Returns the classloader that will be used for scanning for resources. If no explicit ClassLoader
     * has been set by the calling, the context class loader will be used.
     *
     * @return  The ClassLoader that will be used to scan for resources.
     */
    public ClassLoader getClassLoader() {
        return classloader == null ? Thread.currentThread().getContextClassLoader() : classloader;
    }

    /**
     * Sets an explicit ClassLoader that should be used when scanning for resources. If none
     * is set then the context classloader will be used.
     *
     * @param   classloader A ClassLoader to use when scanning for resources
     */
    public void setClassLoader(ClassLoader classloader) {
        this.classloader = classloader;
    }

    /**
     * Resets the matches set.
     */
    public void reset() {
        this.matches.clear();
    }

    /**
     * Attempts to discover resources that pass the test. Accumulated resources can be accessed by
     * calling {@link #getMatches()}.
     *
     * @param   test The test implementation to determine matching resources.
     * @param   recursive If true, this will recurse into sub-directories. If false, this will only
     *          look in the directories given.
     * @param   dirNames One or more directory names to scan for resources.
     */
    public void find(Test<T> test, boolean recursive, String... dirNames) {
        if (dirNames == null) {
            return;
        }

        for (String dir : dirNames) {
            if (dir.endsWith("/")) {
                dir = dir.substring(0, dir.length() - 1);
            }

            findInDirectory(test, recursive, dir);
        }
    }

    /**
     * Attempts to discover resources that pass the test. Accumulated resources can be accessed by
     * calling {@link #getMatches()}. This method uses the same search as the {@link #find(Test, boolean, String[])}
     * method but locates the directory names to use by first locating all of the directories and JAR
     * file paths that end with one of the locator Strings given.
     *
     * <p>
     * Examples:
     * </p>
     *
     * <pre>
     * locators: foo bar
     *
     * JAR file in classpath
     * ----------------------
     * com/example/foo/sub/File.class
     *
     * Directory in classpath (/opt/classpath)
     * ----------------------
     * /opt/classpath/com/example/bar/sub/File2.class
     *
     * This will find directories com/example/foo and com/example/bar
     * </pre>
     *
     * @param   test The test implementation to determine matching resources.
     * @param   recursive If true, this will recurse into sub-directories. If false, this will only
     *          look in the directories given.
     * @param   exlcusions A list of fully qualified directories to exclude from the results.
     * @param   locators A list of locators that are used to locate directories to find resources in.
     */
    public void findByLocators(Test<T> test, boolean recursive, String[] exlcusions, String... locators) {
        List<URL> urls = getURLs("", "META-INF");
        if (urls == null) {
            return;
        }

        Set<String> packages = new HashSet<String>();
        for (URL url : urls) {
            try {
                String urlPath = urlToFilePath(test, url);

                File file = new File(urlPath);
                if (file.isDirectory()) {
                    Queue<File> files = new LinkedList<File>(Arrays.asList(file.listFiles()));
                    while (files.peek() != null) {
                        File f = files.poll();
                        if (f.isDirectory()) {
                            boolean matched = false;
                            for (String locator : locators) {
                                if (locator.equals(f.getName())) {
                                    String pkg = f.getAbsolutePath().substring(file.getAbsolutePath().length() + 1).replace('/', '.');
                                    packages.add(pkg);
                                    matched = true;
                                }
                            }

                            if (!matched) {
                                File[] children = f.listFiles();
                                for (File child : children) {
                                    files.offer(child);
                                }
                            }
                        }
                    }
                } else {
                    JarEntry entry;
                    JarInputStream jarStream = new JarInputStream(new FileInputStream(file));

                    while ((entry = jarStream.getNextJarEntry()) != null) {
                        String name = entry.getName();
                        if (entry.isDirectory()) {
                            for (String locator : locators) {
                                if (name.endsWith(locator + "/")) {
                                    String pkg = name.substring(0, name.length() - 1).replace('/', '.');
                                    packages.add(pkg);
                                }
                            }
                        }
                    }
                }
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Could not read ClassPath entries", ioe);
            }
        }

        if (exlcusions != null) {
            for (String exlcusion : exlcusions) {
                packages.remove(exlcusion);
            }
        }

        find(test, recursive, packages.toArray(new String[packages.size()]));
    }

    /**
     * Scans for resources starting at the directory provided and descending into subpackages (if
     * the recursive boolean is true). Each resource is offered up to the Test as it is discovered,
     * and if the Test returns true the resource is retained. Accumulated resources can be fetched by
     * calling {@link #getMatches()}.
     *
     * @param   test An instance of {@link Test} that will be used to filter resources.
     * @param   recursive If true, this will recurse into sub-directories. If false, this will only
     *          look in the directories given.
     * @param   dirName The name of the directory from which to start scanning for resources.
     */
    protected void findInDirectory(Test<T> test, boolean recursive, String dirName) {
        // Normalize the name just in case they passed in a package name
        dirName = dirName.replace('.', '/');

        List<URL> urls = getURLs(dirName);
        if (urls == null) {
            return;
        }

        for (URL url : urls) {
            try {
                String urlStr = url.toString();
                String baseURLSpec = urlStr.substring(0, urlStr.indexOf(dirName));
                String urlPath = urlToFilePath(test, url);

                File file = new File(urlPath);
                if (file.isDirectory()) {
                    loadResourcesInDirectory(test, recursive, baseURLSpec, dirName, dirName, file);
                } else {
                    loadResourcesInJar(test, recursive, baseURLSpec, dirName, file);
                }
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Could not read ClassPath entries", ioe);
            }
        }
    }

    private List<URL> getURLs(String... dirNames) {
        ClassLoader loader = getClassLoader();
        List<URL> urls = new ArrayList<URL>();
        for (String dirName : dirNames) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Looking up resources in directory [" + dirName + "]");
            }

            try {
                Enumeration<URL> classLoaderURLs = loader.getResources(dirName);
                while (classLoaderURLs.hasMoreElements()) {
                    URL url = classLoaderURLs.nextElement();
                    urls.add(url);
                }
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Could not read driectory [" + dirName + "]", ioe);
            }
        }

        return urls;
    }

    private String urlToFilePath(Test<T> test, URL url) throws UnsupportedEncodingException {
        String urlPath = url.getFile();
        urlPath = URLDecoder.decode(urlPath, "UTF-8");

        // If it's a file in a directory, trim the stupid file: spec
        if (urlPath.startsWith("file:")) {
            urlPath = urlPath.substring(5);
        }

        // Else it's in a JAR, grab the path to the jar
        int index = urlPath.indexOf('!');
        if (index >= 0) {
            urlPath = urlPath.substring(0, index);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Scanning for resources in [" + urlPath + "] matching criteria [" +
                test + "]");
        }

        return urlPath;
    }

    /**
     * Finds matches in a physical directory on a filesystem.  Examines all files within a directory.
     * If the File object is not a directory, it is passed to the Test class. In order to support
     * handling Classes, this first calls the {@link #prepare(String, String, String)} to prepare the
     * resource to be sent to the Test.
     *
     * @param   test A Test used to filter the resources that are discovered.
     * @param   baseURLSpec The base URL specification that might reference a JAR file or a File path.
     *          This does not include the dirName or the file name, but it will include a ! for JAR
     *          URLs.
     * @param   dirName The directory that contains the resources. This is the original value passed
     *          into the find method.
     * @param   recursive If true, this will recurse into sub-directories. If false, this will only
     *          look in the directories given.
     * @param   parent The parent directory of the directory that this test is in.  E.g. if
     *          /classes is in the classpath and we wish to examine files in /classes/org/apache then
     *          the values of <i>parent</i> would be <i>org/apache</i>.
     * @param   location A File object representing a directory.
     */
    protected void loadResourcesInDirectory(Test<T> test, boolean recursive, String baseURLSpec, String dirName,
            String parent, File location) {
        File[] files = location.listFiles();
        for (File file : files) {
            if (file.isDirectory() && recursive) {
                loadResourcesInDirectory(test, recursive, baseURLSpec, dirName, parent + "/" + file.getName(), file);
            } else if (!file.isDirectory()) {
                addIfMatching(test, baseURLSpec, parent, file.getName());
            }
        }
    }

    /**
     * Finds matching resources within a jar file that contains a folder structure matching the
     * directory structure. If the File is not a JarFile or does not exist a warning will be logged,
     * but no error will be raised.
     *
     * @param   test A Test used to filter the resource that are discovered.
     * @param   baseURLSpec The base URL specification that might reference a JAR file or a File path.
     *          This does not include the dirName or the file name, but it will include a ! for JAR
     *          URLs.
     * @param   recursive If true, this will recurse into sub-directories. If false, this will only
     *          look in the directories given.
     * @param   dirName The directory that contains the resources. This is the original value passed
     *          into the find method.
     * @param   jarfile The jar file to be examined for resources.
     */
    protected void loadResourcesInJar(Test<T> test, boolean recursive, String baseURLSpec, String dirName,
            File jarfile) {
        try {
            JarEntry entry;
            JarInputStream jarStream = new JarInputStream(new FileInputStream(jarfile));

            while ((entry = jarStream.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Checking JAR entry [" + name + "] against [" + dirName +
                        "] where index of / is [" + name.indexOf('/', dirName.length() + 1) +
                        "] starting from [" + (dirName.length() + 1) + "]");
                }

                if (!entry.isDirectory() && (recursive && name.startsWith(dirName)) ||
                        (!recursive && name.startsWith(dirName) && name.indexOf('/', dirName.length() + 1) == -1)) {
                    int index = name.lastIndexOf('/');
                    index = (index > 0) ? index : 0;
                    String dir = name.substring(0, index);
                    String file = name.substring(index + 1);
                    addIfMatching(test, baseURLSpec, dir, file);
                }
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Could not search jar file [" + jarfile + "] for classes " +
                "matching criteria [" + test + "] due to an IOException", ioe);
        }
    }

    /**
     * Add the resource designated by the fully qualified path provided to the set of resolved
     * resources if and only if it is approved by the Test supplied.
     *
     * @param   test The test used to determine if the resource matches.
     * @param   baseURLSpec The base URL specification that might reference a JAR file or a File path.
     *          This does not include the dirName or the file name, but it will include a ! for JAR
     *          URLs.
     * @param   dirName The directory that contains the resources. This is the original value passed
     *          into the find method.
     * @param   path The relative path to the resource.
     */
    protected void addIfMatching(Test<T> test, String baseURLSpec, String dirName, String path) {
        T t = prepare(baseURLSpec, dirName, path);
        if (t != null && test.test(t)) {
            matches.add(t);
        }
    }

    /**
     * Prepares the path so that it can be sent to the tests.
     *
     * @param   baseURLSpec The base URL specification that might reference a JAR file or a File path.
     *          This does not include the dirName or the file name, but it will include a ! for JAR
     *          URLs.
     * @param   dirName The part of the URL that is the directory from some base to the file.
     * @param   file The file t be prepared.
     * @return  The prepared Object.
     */
    protected abstract T prepare(String baseURLSpec, String dirName, String file);

    /**
     * This is the testing interface that is used to accept or reject resources.
     */
    public static interface Test<T> {
        /**
         * The test method.
         *
         * @param   t The resource object to test.
         * @return  True if the resource should be accepted, false otherwise.
         */
        public boolean test(T t);
    }
}