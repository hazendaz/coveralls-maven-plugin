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
 * Service implementation for Appveyor.
 * <p>
 * https://appveyor.com/
 */
public class Appveyor extends AbstractServiceSetup {

    /** The Constant APPVEYOR_NAME. */
    public static final String APPVEYOR_NAME = "Appveyor";

    /** The Constant APPVEYOR. */
    public static final String APPVEYOR = "APPVEYOR";

    /** The Constant APPVEYOR_BUILD_NUMBER. */
    public static final String APPVEYOR_BUILD_NUMBER = "APPVEYOR_BUILD_NUMBER";

    /** The Constant APPVEYOR_BUILD_ID. */
    public static final String APPVEYOR_BUILD_ID = "APPVEYOR_BUILD_ID";

    /** The Constant APPVEYOR_BRANCH. */
    public static final String APPVEYOR_BRANCH = "APPVEYOR_REPO_BRANCH";

    /** The Constant APPVEYOR_COMMIT. */
    public static final String APPVEYOR_COMMIT = "APPVEYOR_REPO_COMMIT";

    /** The Constant APPVEYOR_PULL_REQUEST. */
    public static final String APPVEYOR_PULL_REQUEST = "APPVEYOR_PULL_REQUEST_NUMBER";

    /** The Constant APPVEYOR_REPO_NAME. */
    public static final String APPVEYOR_REPO_NAME = "APPVEYOR_REPO_NAME";

    /**
     * Instantiates a new appveyor.
     *
     * @param env
     *            the env
     */
    public Appveyor(final Map<String, String> env) {
        super(env);
    }

    @Override
    public boolean isSelected() {
        return Boolean.parseBoolean(this.getProperty(Appveyor.APPVEYOR));
    }

    @Override
    public String getName() {
        return Appveyor.APPVEYOR_NAME;
    }

    @Override
    public String getBuildNumber() {
        return this.getProperty(Appveyor.APPVEYOR_BUILD_NUMBER);
    }

    @Override
    public String getBuildUrl() {
        return "https://ci.appveyor.com/project/" + this.getProperty(Appveyor.APPVEYOR_REPO_NAME) + "/build/"
                + this.getProperty(Appveyor.APPVEYOR_BUILD_NUMBER);
    }

    @Override
    public String getBranch() {
        return this.getProperty(Appveyor.APPVEYOR_BRANCH);
    }

    @Override
    public String getPullRequest() {
        return this.getProperty(Appveyor.APPVEYOR_PULL_REQUEST);
    }

    @Override
    public String getJobId() {
        return this.getProperty(Appveyor.APPVEYOR_BUILD_ID);
    }

}
