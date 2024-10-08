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

class AppveyorTest {

    Map<String, String> env() {
        Map<String, String> env = new HashMap<>();
        env.put(Appveyor.APPVEYOR, "true");
        env.put(Appveyor.APPVEYOR_BUILD_ID, "54de3316c44f");
        env.put(Appveyor.APPVEYOR_BUILD_NUMBER, "77");
        env.put(Appveyor.APPVEYOR_BRANCH, "master");
        env.put(Appveyor.APPVEYOR_COMMIT, "a3562fgcd2");
        env.put(Appveyor.APPVEYOR_PULL_REQUEST, "10");
        env.put(Appveyor.APPVEYOR_REPO_NAME, "owner/project");
        return env;
    }

    @Test
    void isSelectedForNothing() {
        assertFalse(new Appveyor(new HashMap<>()).isSelected());
    }

    @Test
    void isSelectedForAppveyor() {
        assertTrue(new Appveyor(env()).isSelected());
    }

    @Test
    void testGetName() {
        assertEquals("Appveyor", new Appveyor(env()).getName());
    }

    @Test
    void testGetBuildNumber() {
        assertEquals("77", new Appveyor(env()).getBuildNumber());
    }

    @Test
    void testGetBuildUrl() {
        assertEquals("https://ci.appveyor.com/project/owner/project/build/77", new Appveyor(env()).getBuildUrl());
    }

    @Test
    void testGetBranch() {
        assertEquals("master", new Appveyor(env()).getBranch());
    }

    @Test
    void pullRequest() {
        assertEquals("10", new Appveyor(env()).getPullRequest());
    }

    @Test
    void testGetJobId() {
        assertEquals("54de3316c44f", new Appveyor(env()).getJobId());
    }
}
