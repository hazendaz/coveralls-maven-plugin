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

/**
 * General implementation for any continuous integration service that provides the required environment properties.
 */
public class General extends AbstractServiceSetup {

    /** The Constant CI_NAME. */
    public static final String CI_NAME = "CI_NAME";

    /** The Constant CI_BUILD_NUMBER. */
    public static final String CI_BUILD_NUMBER = "CI_BUILD_NUMBER";

    /** The Constant CI_BUILD_URL. */
    public static final String CI_BUILD_URL = "CI_BUILD_URL";

    /** The Constant CI_BRANCH. */
    public static final String CI_BRANCH = "CI_BRANCH";

    /** The Constant CI_PULL_REQUEST. */
    public static final String CI_PULL_REQUEST = "CI_PULL_REQUEST";

    /**
     * Instantiates a new general.
     *
     * @param env
     *            the env
     */
    public General(final Map<String, String> env) {
        super(env);
    }

    @Override
    public boolean isSelected() {
        return this.getProperty(General.CI_NAME) != null;
    }

    @Override
    public String getName() {
        return this.getProperty(General.CI_NAME);
    }

    @Override
    public String getBuildNumber() {
        return this.getProperty(General.CI_BUILD_NUMBER);
    }

    @Override
    public String getBuildUrl() {
        return this.getProperty(General.CI_BUILD_URL);
    }

    @Override
    public String getBranch() {
        return this.getProperty(General.CI_BRANCH);
    }

    @Override
    public String getPullRequest() {
        return this.getProperty(General.CI_PULL_REQUEST);
    }
}
