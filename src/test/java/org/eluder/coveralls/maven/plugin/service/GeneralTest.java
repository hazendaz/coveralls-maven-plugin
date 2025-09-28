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

/**
 * The Class GeneralTest.
 */
class GeneralTest {

    /**
     * Env.
     *
     * @return the map
     */
    Map<String, String> env() {
        Map<String, String> env = new HashMap<>();
        env.put("CI_NAME", "ci_service");
        env.put("CI_BUILD_NUMBER", "build123");
        env.put("CI_BUILD_URL", "https://ci.com/build123");
        env.put("CI_BRANCH", "master");
        env.put("CI_PULL_REQUEST", "pull10");
        return env;
    }

    /**
     * Checks if is selected for nothing.
     */
    @Test
    void isSelectedForNothing() {
        assertFalse(new General(new HashMap<>()).isSelected());
    }

    /**
     * Checks if is selected for ci.
     */
    @Test
    void isSelectedForCi() {
        assertTrue(new General(env()).isSelected());
    }

    /**
     * Test get name.
     */
    @Test
    void testGetName() {
        assertEquals("ci_service", new General(env()).getName());
    }

    /**
     * Test get build number.
     */
    @Test
    void testGetBuildNumber() {
        assertEquals("build123", new General(env()).getBuildNumber());
    }

    /**
     * Test get build url.
     */
    @Test
    void testGetBuildUrl() {
        assertEquals("https://ci.com/build123", new General(env()).getBuildUrl());
    }

    /**
     * Test get branch.
     */
    @Test
    void testGetBranch() {
        assertEquals("master", new General(env()).getBranch());
    }

    /**
     * Test get pull request.
     */
    @Test
    void testGetPullRequest() {
        assertEquals("pull10", new General(env()).getPullRequest());
    }
}
