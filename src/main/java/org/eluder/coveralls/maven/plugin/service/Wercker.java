/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 - 2023, Tapio Rautonen
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
        return Boolean.parseBoolean(getProperty(Wercker.WERCKER));
    }

    @Override
    public String getName() {
        return Wercker.WERCKER_NAME;
    }

    @Override
    public String getJobId() {
        return getProperty(Wercker.WERCKER_BUILD_ID);
    }

    @Override
    public String getBuildUrl() {
        return getProperty(Wercker.WERCKER_BUILD_URL);
    }

    @Override
    public String getBranch() {
        return getProperty(Wercker.WERCKER_BRANCH);
    }
}
