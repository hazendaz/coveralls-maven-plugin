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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ShippableTest {

    Map<String, String> env() {
        Map<String, String> env = new HashMap<>();
        env.put(Shippable.SHIPPABLE, "true");
        env.put(Shippable.SHIPPABLE_BUILD_ID, "54de3316c44f");
        env.put(Shippable.SHIPPABLE_BUILD_NUMBER, "431.1");
        env.put(Shippable.SHIPPABLE_BRANCH, "master");
        env.put(Shippable.SHIPPABLE_COMMIT, "a3562fgcd2");
        env.put(Shippable.SHIPPABLE_PULL_REQUEST, "10");
        return env;
    }

    @Test
    void isSelectedForNothing() {
        assertFalse(new Shippable(new HashMap<>()).isSelected());
    }

    @Test
    void isSelectedForShippable() {
        assertTrue(new Shippable(env()).isSelected());
    }

    @Test
    void testGetName() {
        assertEquals("shippable", new Shippable(env()).getName());
    }

    @Test
    void testGetBuildNumber() {
        assertEquals("431.1", new Shippable(env()).getBuildNumber());
    }

    @Test
    void testGetBuildUrl() {
        assertEquals("https://app.shippable.com/builds/54de3316c44f", new Shippable(env()).getBuildUrl());
    }

    @Test
    void testGetBranch() {
        assertEquals("master", new Shippable(env()).getBranch());
    }

    @Test
    void pullRequest() {
        assertEquals("10", new Shippable(env()).getPullRequest());
    }

    @Test
    void pullRequestFalse() {
        var env = env();
        env.put(Shippable.SHIPPABLE_PULL_REQUEST, "false");
        assertNull(new Shippable(env).getPullRequest());
    }

    @Test
    void testGetEnvironment() {
        var properties = new Shippable(env()).getEnvironment();
        assertEquals(5, properties.size());
        assertEquals("431.1", properties.getProperty("shippable_build_number"));
        assertEquals("54de3316c44f", properties.getProperty("shippable_build_id"));
        assertEquals("https://app.shippable.com/builds/54de3316c44f", properties.getProperty("shippable_build_url"));
        assertEquals("master", properties.getProperty("branch"));
        assertEquals("a3562fgcd2", properties.getProperty("commit_sha"));
    }
}
