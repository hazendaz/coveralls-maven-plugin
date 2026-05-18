/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2025 The Coveralls Maven Plugin Project Contributors:
 *     https://github.com/hazendaz/coveralls-maven-plugin/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eluder.coveralls.maven.plugin.service;

import java.util.Map;
import java.util.Properties;

/**
 * Convenient base class for service setups.
 */
public abstract class AbstractServiceSetup implements ServiceSetup {

    /** The env. */
    private final Map<String, String> env;

    /**
     * Instantiates a new abstract service setup.
     *
     * @param env
     *            the env
     */
    protected AbstractServiceSetup(final Map<String, String> env) {
        this.env = env;
    }

    @Override
    public String getJobId() {
        return null;
    }

    @Override
    public String getBuildNumber() {
        return null;
    }

    @Override
    public String getBuildUrl() {
        return null;
    }

    @Override
    public String getBranch() {
        return null;
    }

    @Override
    public String getPullRequest() {
        return null;
    }

    @Override
    public Properties getEnvironment() {
        return null;
    }

    /**
     * Gets the property.
     *
     * @param name
     *            the name
     *
     * @return the property
     */
    protected final String getProperty(final String name) {
        return this.env.get(name);
    }

    /**
     * Adds the property.
     *
     * @param properties
     *            the properties
     * @param name
     *            the name
     * @param value
     *            the value
     */
    protected final void addProperty(final Properties properties, final String name, final String value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must be defined");
        }
        if (value != null && !value.isBlank()) {
            properties.setProperty(name, value);
        }
    }
}
