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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class GitHubTest.
 */
class GitHubTest {

    /**
     * Env.
     *
     * @return the map
     */
    Map<String, String> env() {
        final Map<String, String> env = new HashMap<>();
        env.put("GITHUB_ACTIONS", "true");
        env.put("GITHUB_REPOSITORY", "hazendaz/coveralls-maven-plugin");
        env.put("GITHUB_REF", "refs/pull/1/merge");
        env.put("GITHUB_REF_NAME", "main");
        env.put("GITHUB_RUN_ID", "12345");
        env.put("GITHUB_RUN_NUMBER", "1");
        env.put("GITHUB_SERVER_URL", "https://github.com");
        return env;
    }

    /**
     * Checks if is selected for nothing.
     */
    @Test
    void isSelectedForNothing() {
        Assertions.assertFalse(new GitHub(new HashMap<>()).isSelected());
    }

    /**
     * Checks if is selected for jenkins.
     */
    @Test
    void isSelectedForJenkins() {
        Assertions.assertTrue(new GitHub(this.env()).isSelected());
    }

    /**
     * Test get name.
     */
    @Test
    void testGetName() {
        Assertions.assertEquals("github", new GitHub(this.env()).getName());
    }

    /**
     * Test get build number.
     */
    @Test
    void testGetBuildNumber() {
        Assertions.assertEquals("1", new GitHub(this.env()).getBuildNumber());
    }

    /**
     * Test get build url.
     */
    @Test
    void testGetBuildUrl() {
        Assertions.assertEquals("https://github.com/hazendaz/coveralls-maven-plugin/actions/runs/12345",
                new GitHub(this.env()).getBuildUrl());
    }

    /**
     * Test get pull request.
     */
    @Test
    void testGetPullRequest() {
        Assertions.assertEquals("1", new GitHub(this.env()).getPullRequest());
    }

    /**
     * Test get job id.
     */
    @Test
    void testGetJobId() {
        Assertions.assertEquals("12345", new GitHub(this.env()).getJobId());
    }

}
