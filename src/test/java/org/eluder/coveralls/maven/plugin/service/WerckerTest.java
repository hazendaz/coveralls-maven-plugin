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
    void testGetName() {
        Assertions.assertEquals("wercker", new Wercker(this.env()).getName());
    }

    /**
     * Test get job id.
     */
    @Test
    void testGetJobId() {
        Assertions.assertEquals("123456789", new Wercker(this.env()).getJobId());
    }

    /**
     * Test get build url.
     */
    @Test
    void testGetBuildUrl() {
        Assertions.assertEquals("https://app.wercker.com/build/123456789", new Wercker(this.env()).getBuildUrl());
    }

    /**
     * Test get branch.
     */
    @Test
    void testGetBranch() {
        Assertions.assertEquals("master", new Wercker(this.env()).getBranch());
    }
}
