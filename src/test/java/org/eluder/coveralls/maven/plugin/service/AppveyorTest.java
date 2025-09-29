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
 * The Class AppveyorTest.
 */
class AppveyorTest {

    /**
     * Env.
     *
     * @return the map
     */
    Map<String, String> env() {
        final Map<String, String> env = new HashMap<>();
        env.put(Appveyor.APPVEYOR, "true");
        env.put(Appveyor.APPVEYOR_BUILD_ID, "54de3316c44f");
        env.put(Appveyor.APPVEYOR_BUILD_NUMBER, "77");
        env.put(Appveyor.APPVEYOR_BRANCH, "master");
        env.put(Appveyor.APPVEYOR_COMMIT, "a3562fgcd2");
        env.put(Appveyor.APPVEYOR_PULL_REQUEST, "10");
        env.put(Appveyor.APPVEYOR_REPO_NAME, "owner/project");
        return env;
    }

    /**
     * Checks if is selected for nothing.
     */
    @Test
    void isSelectedForNothing() {
        Assertions.assertFalse(new Appveyor(new HashMap<>()).isSelected());
    }

    /**
     * Checks if is selected for appveyor.
     */
    @Test
    void isSelectedForAppveyor() {
        Assertions.assertTrue(new Appveyor(this.env()).isSelected());
    }

    /**
     * Test get name.
     */
    @Test
    void testGetName() {
        Assertions.assertEquals("Appveyor", new Appveyor(this.env()).getName());
    }

    /**
     * Test get build number.
     */
    @Test
    void testGetBuildNumber() {
        Assertions.assertEquals("77", new Appveyor(this.env()).getBuildNumber());
    }

    /**
     * Test get build url.
     */
    @Test
    void testGetBuildUrl() {
        Assertions.assertEquals("https://ci.appveyor.com/project/owner/project/build/77",
                new Appveyor(this.env()).getBuildUrl());
    }

    /**
     * Test get branch.
     */
    @Test
    void testGetBranch() {
        Assertions.assertEquals("master", new Appveyor(this.env()).getBranch());
    }

    /**
     * Pull request.
     */
    @Test
    void pullRequest() {
        Assertions.assertEquals("10", new Appveyor(this.env()).getPullRequest());
    }

    /**
     * Test get job id.
     */
    @Test
    void testGetJobId() {
        Assertions.assertEquals("54de3316c44f", new Appveyor(this.env()).getJobId());
    }

}
