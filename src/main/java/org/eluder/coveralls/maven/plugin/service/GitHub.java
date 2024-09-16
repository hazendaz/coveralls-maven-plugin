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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHub extends AbstractServiceSetup {

    private static final Pattern GITHUB_PR = Pattern.compile("(\\d+)/merge");

    public static final String GITHUB = "github";
    public static final String GITHUB_ACTIONS = "GITHUB_ACTIONS";
    public static final String GITHUB_REF_NAME = "GITHUB_REF_NAME";
    public static final String GITHUB_REPOSITORY = "GITHUB_REPOSITORY";
    public static final String GITHUB_RUN_ID = "GITHUB_RUN_ID";
    public static final String GITHUB_RUN_NUMBER = "GITHUB_RUN_NUMBER";
    public static final String GITHUB_SERVER_URL = "GITHUB_SERVER_URL";

    public GitHub(Map<String, String> env) {
        super(env);
    }

    @Override
    public boolean isSelected() {
        return Boolean.parseBoolean(getProperty(GITHUB_ACTIONS));
    }

    @Override
    public String getName() {
        return GITHUB;
    }

    @Override
    public String getJobId() {
        return getProperty(GITHUB_RUN_ID);
    }

    @Override
    public String getBuildNumber() {
        return getProperty(GITHUB_RUN_NUMBER);
    }

    @Override
    public String getBuildUrl() {
        return String.format("%s/%s/actions/runs/%s", getProperty(GITHUB_SERVER_URL), getProperty(GITHUB_REPOSITORY),
                getProperty(GITHUB_RUN_ID));
    }

    @Override
    public String getPullRequest() {
        Matcher matcher = GITHUB_PR.matcher(getProperty(GITHUB_REF_NAME));
        return matcher.matches() ? matcher.group(1) : null;
    }
}
