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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TravisTest {

    private Map<String, String> env() {
        Map<String, String> env = new HashMap<>();
        env.put("TRAVIS", "true");
        env.put("TRAVIS_JOB_ID", "job123");
        env.put("TRAVIS_BRANCH", "master");
        env.put("TRAVIS_PULL_REQUEST", "pull10");
        return env;
    }

    @Test
    void isSelectedForNothing() {
        assertFalse(new Travis(new HashMap<>()).isSelected());
    }

    @Test
    void isSelectedForTravis() {
        assertTrue(new Travis(env()).isSelected());
    }

    @Test
    void testGetName() {
        assertEquals("travis-ci", new Travis(env()).getName());
    }

    @Test
    void testGetJobId() {
        assertEquals("job123", new Travis(env()).getJobId());
    }

    @Test
    void testGetBranch() {
        assertEquals("master", new Travis(env()).getBranch());
    }

    @Test
    void testGetPullRequest() {
        assertEquals("pull10", new Travis(env()).getPullRequest());
    }

    @Test
    void testGetEnvironment() {
        var properties = new Travis(env()).getEnvironment();
        assertEquals(2, properties.size());
        assertEquals("job123", properties.getProperty("travis_job_id"));
        assertEquals("pull10", properties.getProperty("travis_pull_request"));
    }
}
