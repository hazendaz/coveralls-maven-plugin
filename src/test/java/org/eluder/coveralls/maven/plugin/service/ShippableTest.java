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
 * The Class ShippableTest.
 */
class ShippableTest {

    /**
     * Env.
     *
     * @return the map
     */
    Map<String, String> env() {
        final Map<String, String> env = new HashMap<>();
        env.put(Shippable.SHIPPABLE, "true");
        env.put(Shippable.SHIPPABLE_BUILD_ID, "54de3316c44f");
        env.put(Shippable.SHIPPABLE_BUILD_NUMBER, "431.1");
        env.put(Shippable.SHIPPABLE_BRANCH, "master");
        env.put(Shippable.SHIPPABLE_COMMIT, "a3562fgcd2");
        env.put(Shippable.SHIPPABLE_PULL_REQUEST, "10");
        return env;
    }

    /**
     * Checks if is selected for nothing.
     */
    @Test
    void isSelectedForNothing() {
        Assertions.assertFalse(new Shippable(new HashMap<>()).isSelected());
    }

    /**
     * Checks if is selected for shippable.
     */
    @Test
    void isSelectedForShippable() {
        Assertions.assertTrue(new Shippable(this.env()).isSelected());
    }

    /**
     * Test get name.
     */
    @Test
    void testGetName() {
        Assertions.assertEquals("shippable", new Shippable(this.env()).getName());
    }

    /**
     * Test get build number.
     */
    @Test
    void testGetBuildNumber() {
        Assertions.assertEquals("431.1", new Shippable(this.env()).getBuildNumber());
    }

    /**
     * Test get build url.
     */
    @Test
    void testGetBuildUrl() {
        Assertions.assertEquals("https://app.shippable.com/builds/54de3316c44f",
                new Shippable(this.env()).getBuildUrl());
    }

    /**
     * Test get branch.
     */
    @Test
    void testGetBranch() {
        Assertions.assertEquals("master", new Shippable(this.env()).getBranch());
    }

    /**
     * Pull request.
     */
    @Test
    void pullRequest() {
        Assertions.assertEquals("10", new Shippable(this.env()).getPullRequest());
    }

    /**
     * Pull request false.
     */
    @Test
    void pullRequestFalse() {
        final var env = this.env();
        env.put(Shippable.SHIPPABLE_PULL_REQUEST, "false");
        Assertions.assertNull(new Shippable(env).getPullRequest());
    }

    /**
     * Test get environment.
     */
    @Test
    void testGetEnvironment() {
        final var properties = new Shippable(this.env()).getEnvironment();
        Assertions.assertEquals(5, properties.size());
        Assertions.assertEquals("431.1", properties.getProperty("shippable_build_number"));
        Assertions.assertEquals("54de3316c44f", properties.getProperty("shippable_build_id"));
        Assertions.assertEquals("https://app.shippable.com/builds/54de3316c44f",
                properties.getProperty("shippable_build_url"));
        Assertions.assertEquals("master", properties.getProperty("branch"));
        Assertions.assertEquals("a3562fgcd2", properties.getProperty("commit_sha"));
    }
}
