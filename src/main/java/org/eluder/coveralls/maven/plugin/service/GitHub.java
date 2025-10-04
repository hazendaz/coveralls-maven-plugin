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
import java.util.regex.Pattern;

/**
 * The Class GitHub.
 */
public class GitHub extends AbstractServiceSetup {

    /** The Constant GITHUB_PR. */
    private static final Pattern GITHUB_PR = Pattern.compile("refs/pull/(\\d+)/merge");

    /** The Constant GITHUB. */
    public static final String GITHUB = "github";

    /** The Constant GITHUB_ACTIONS. */
    public static final String GITHUB_ACTIONS = "GITHUB_ACTIONS";

    /** The Constant GITHUB_REF_NAME. */
    public static final String GITHUB_REF_NAME = "GITHUB_REF_NAME";

    /** The Constant GITHUB_REPOSITORY. */
    public static final String GITHUB_REPOSITORY = "GITHUB_REPOSITORY";

    /** The Constant GITHUB_RUN_ID. */
    public static final String GITHUB_RUN_ID = "GITHUB_RUN_ID";

    /** The Constant GITHUB_RUN_NUMBER. */
    public static final String GITHUB_RUN_NUMBER = "GITHUB_RUN_NUMBER";

    /** The Constant GITHUB_SERVER_URL. */
    public static final String GITHUB_SERVER_URL = "GITHUB_SERVER_URL";

    /** The Constant GITHUB_REF. */
    public static final String GITHUB_REF = "GITHUB_REF";

    /**
     * Instantiates a new git hub.
     *
     * @param env
     *            the env
     */
    public GitHub(Map<String, String> env) {
        super(env);
    }

    @Override
    public boolean isSelected() {
        return Boolean.parseBoolean(this.getProperty(GitHub.GITHUB_ACTIONS));
    }

    @Override
    public String getName() {
        return GitHub.GITHUB;
    }

    @Override
    public String getJobId() {
        return this.getProperty(GitHub.GITHUB_RUN_ID);
    }

    @Override
    public String getBuildNumber() {
        return this.getProperty(GitHub.GITHUB_RUN_NUMBER);
    }

    @Override
    public String getBuildUrl() {
        return String.format("%s/%s/actions/runs/%s", this.getProperty(GitHub.GITHUB_SERVER_URL),
                this.getProperty(GitHub.GITHUB_REPOSITORY), this.getProperty(GitHub.GITHUB_RUN_ID));
    }

    @Override
    public String getPullRequest() {
        final var ref = this.getProperty(GitHub.GITHUB_REF);
        if (ref != null) {
            final var matcher = GitHub.GITHUB_PR.matcher(ref);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    @Override
    public String getBranch() {
        return this.getProperty(GitHub.GITHUB_REF_NAME);
    }

    @Override
    public Properties getEnvironment() {
        final var environment = new Properties();
        this.addProperty(environment, "github_run_id", this.getProperty(GitHub.GITHUB_RUN_ID));
        this.addProperty(environment, "github_run_number", this.getProperty(GitHub.GITHUB_RUN_NUMBER));
        this.addProperty(environment, "github_repository", this.getProperty(GitHub.GITHUB_REPOSITORY));
        this.addProperty(environment, "github_ref_name", this.getProperty(GitHub.GITHUB_REF_NAME));
        this.addProperty(environment, "github_server_url", this.getProperty(GitHub.GITHUB_SERVER_URL));
        return environment;
    }

}
