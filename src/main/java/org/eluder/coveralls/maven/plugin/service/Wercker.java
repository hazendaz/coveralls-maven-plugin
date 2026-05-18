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
 * Service implementation for Wercker CI.
 * <p>
 * https://wercker.com/
 */
public class Wercker extends AbstractServiceSetup {

    /** The Constant WERCKER_NAME. */
    public static final String WERCKER_NAME = "wercker";

    /** The Constant WERCKER. */
    public static final String WERCKER = "WERCKER";

    /** The Constant WERCKER_BUILD_ID. */
    public static final String WERCKER_BUILD_ID = "WERCKER_BUILD_ID";

    /** The Constant WERCKER_BUILD_URL. */
    public static final String WERCKER_BUILD_URL = "WERCKER_BUILD_URL";

    /** The Constant WERCKER_BRANCH. */
    public static final String WERCKER_BRANCH = "WERCKER_GIT_BRANCH";

    /** The Constant WERCKER_PULL_REQUEST. */
    public static final String WERCKER_PULL_REQUEST = "WERCKER_PULL_REQUEST";

    /**
     * Instantiates a new wercker.
     *
     * @param env
     *            the env
     */
    public Wercker(final Map<String, String> env) {
        super(env);
    }

    @Override
    public boolean isSelected() {
        return Boolean.parseBoolean(this.getProperty(Wercker.WERCKER));
    }

    @Override
    public String getName() {
        return Wercker.WERCKER_NAME;
    }

    @Override
    public String getJobId() {
        return this.getProperty(Wercker.WERCKER_BUILD_ID);
    }

    @Override
    public String getBuildUrl() {
        return this.getProperty(Wercker.WERCKER_BUILD_URL);
    }

    @Override
    public String getBranch() {
        return this.getProperty(Wercker.WERCKER_BRANCH);
    }

    @Override
    public String getBuildNumber() {
        return this.getProperty(Wercker.WERCKER_BUILD_ID);
    }

    @Override
    public String getPullRequest() {
        return this.getProperty(Wercker.WERCKER_PULL_REQUEST);
    }

    @Override
    public Properties getEnvironment() {
        final var environment = new Properties();
        this.addProperty(environment, "wercker_build_id", this.getProperty(Wercker.WERCKER_BUILD_ID));
        this.addProperty(environment, "wercker_build_url", this.getProperty(Wercker.WERCKER_BUILD_URL));
        this.addProperty(environment, "wercker_branch", this.getProperty(Wercker.WERCKER_BRANCH));
        this.addProperty(environment, "wercker_pull_request", this.getProperty(Wercker.WERCKER_PULL_REQUEST));
        return environment;
    }

}
