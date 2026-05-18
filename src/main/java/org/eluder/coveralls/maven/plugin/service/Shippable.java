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
 * Service implementation for Shippable.
 * <p>
 * https://shippable.com/
 */
public class Shippable extends AbstractServiceSetup {

    /** The Constant SHIPPABLE_NAME. */
    public static final String SHIPPABLE_NAME = "shippable";

    /** The Constant SHIPPABLE. */
    public static final String SHIPPABLE = "SHIPPABLE";

    /** The Constant SHIPPABLE_BUILD_NUMBER. */
    public static final String SHIPPABLE_BUILD_NUMBER = "SHIPPABLE_BUILD_NUMBER";

    /** The Constant SHIPPABLE_BUILD_ID. */
    public static final String SHIPPABLE_BUILD_ID = "SHIPPABLE_BUILD_ID";

    /** The Constant SHIPPABLE_BRANCH. */
    public static final String SHIPPABLE_BRANCH = "BRANCH";

    /** The Constant SHIPPABLE_COMMIT. */
    public static final String SHIPPABLE_COMMIT = "COMMIT";

    /** The Constant SHIPPABLE_PULL_REQUEST. */
    public static final String SHIPPABLE_PULL_REQUEST = "PULL_REQUEST";

    /**
     * Instantiates a new shippable.
     *
     * @param env
     *            the env
     */
    public Shippable(final Map<String, String> env) {
        super(env);
    }

    @Override
    public boolean isSelected() {
        return Boolean.parseBoolean(this.getProperty(Shippable.SHIPPABLE));
    }

    @Override
    public String getName() {
        return Shippable.SHIPPABLE_NAME;
    }

    @Override
    public String getBuildNumber() {
        return this.getProperty(Shippable.SHIPPABLE_BUILD_NUMBER);
    }

    @Override
    public String getBuildUrl() {
        return "https://app.shippable.com/builds/" + this.getProperty(Shippable.SHIPPABLE_BUILD_ID);
    }

    @Override
    public String getBranch() {
        return this.getProperty(Shippable.SHIPPABLE_BRANCH);
    }

    @Override
    public String getPullRequest() {
        final var pullRequest = this.getProperty(Shippable.SHIPPABLE_PULL_REQUEST);
        if ("false".equals(pullRequest)) {
            return null;
        }
        return pullRequest;
    }

    @Override
    public Properties getEnvironment() {
        final var environment = new Properties();
        this.addProperty(environment, "shippable_build_number", this.getProperty(Shippable.SHIPPABLE_BUILD_NUMBER));
        this.addProperty(environment, "shippable_build_id", this.getProperty(Shippable.SHIPPABLE_BUILD_ID));
        this.addProperty(environment, "shippable_build_url", this.getBuildUrl());
        this.addProperty(environment, "branch", this.getProperty(Shippable.SHIPPABLE_BRANCH));
        this.addProperty(environment, "commit_sha", this.getProperty(Shippable.SHIPPABLE_COMMIT));
        return environment;
    }

}
