/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2026 The Coveralls Maven Plugin Project Contributors:
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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class WerckerTest.
 */
class WerckerTest {

    /**
     * Env.
     *
     * @return the map
     */
    Map<String, String> env() {
        final Map<String, String> env = new HashMap<>();
        env.put("WERCKER", "true");
        env.put("WERCKER_BUILD_URL", "https://app.wercker.com/build/123456789");
        env.put("WERCKER_BUILD_ID", "123456789");
        env.put("WERCKER_GIT_BRANCH", "master");
        return env;
    }

    /**
     * Checks if is selected for nothing.
     */
    @Test
    void isSelectedForNothing() {
        Assertions.assertFalse(new Wercker(new HashMap<>()).isSelected());
    }

    /**
     * Checks if is selected for wercker.
     */
    @Test
    void isSelectedForWercker() {
        Assertions.assertTrue(new Wercker(this.env()).isSelected());
    }

    /**
     * Test get name.
     */
    @Test
    void name() {
        Assertions.assertEquals("wercker", new Wercker(this.env()).getName());
    }

    /**
     * Test get job id.
     */
    @Test
    void jobId() {
        Assertions.assertEquals("123456789", new Wercker(this.env()).getJobId());
    }

    /**
     * Test get build url.
     */
    @Test
    void buildUrl() {
        Assertions.assertEquals("https://app.wercker.com/build/123456789", new Wercker(this.env()).getBuildUrl());
    }

    /**
     * Test get branch.
     */
    @Test
    void branch() {
        Assertions.assertEquals("master", new Wercker(this.env()).getBranch());
    }

    /**
     * Test get build number.
     */
    @Test
    void buildNumber() {
        Assertions.assertEquals("123456789", new Wercker(this.env()).getBuildNumber());
    }

    /**
     * Test get pull request (null when not set).
     */
    @Test
    void pullRequest() {
        Assertions.assertNull(new Wercker(this.env()).getPullRequest());
    }

    /**
     * Test get pull request when set.
     */
    @Test
    void pullRequestSet() {
        final Map<String, String> env = new HashMap<>(this.env());
        env.put("WERCKER_PULL_REQUEST", "42");
        Assertions.assertEquals("42", new Wercker(env).getPullRequest());
    }

    /**
     * Test get environment.
     */
    @Test
    void environment() {
        final var env = new Wercker(this.env()).getEnvironment();
        Assertions.assertEquals("123456789", env.getProperty("wercker_build_id"));
        Assertions.assertEquals("https://app.wercker.com/build/123456789", env.getProperty("wercker_build_url"));
        Assertions.assertEquals("master", env.getProperty("wercker_branch"));
    }

}
