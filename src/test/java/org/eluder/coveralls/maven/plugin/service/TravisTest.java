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
 * The Class TravisTest.
 */
class TravisTest {

    /**
     * Env.
     *
     * @return the map
     */
    Map<String, String> env() {
        final Map<String, String> env = new HashMap<>();
        env.put("TRAVIS", "true");
        env.put("TRAVIS_JOB_ID", "job123");
        env.put("TRAVIS_BRANCH", "master");
        env.put("TRAVIS_PULL_REQUEST", "pull10");
        return env;
    }

    /**
     * Checks if is selected for nothing.
     */
    @Test
    void isSelectedForNothing() {
        Assertions.assertFalse(new Travis(new HashMap<>()).isSelected());
    }

    /**
     * Checks if is selected for travis.
     */
    @Test
    void isSelectedForTravis() {
        Assertions.assertTrue(new Travis(this.env()).isSelected());
    }

    /**
     * Test get name.
     */
    @Test
    void name() {
        Assertions.assertEquals("travis-ci", new Travis(this.env()).getName());
    }

    /**
     * Test get job id.
     */
    @Test
    void jobId() {
        Assertions.assertEquals("job123", new Travis(this.env()).getJobId());
    }

    /**
     * Test get branch.
     */
    @Test
    void branch() {
        Assertions.assertEquals("master", new Travis(this.env()).getBranch());
    }

    /**
     * Test get pull request.
     */
    @Test
    void pullRequest() {
        Assertions.assertEquals("pull10", new Travis(this.env()).getPullRequest());
    }

    /**
     * Test get environment.
     */
    @Test
    void environment() {
        final var properties = new Travis(this.env()).getEnvironment();
        Assertions.assertEquals(2, properties.size());
        Assertions.assertEquals("job123", properties.getProperty("travis_job_id"));
        Assertions.assertEquals("pull10", properties.getProperty("travis_pull_request"));
    }

}
