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
 * The Class BambooTest.
 */
class BambooTest {

    /**
     * Env.
     *
     * @return the map
     */
    Map<String, String> env() {
        final Map<String, String> env = new HashMap<>();
        env.put("bamboo.buildNumber", "build123");
        env.put("bamboo.buildResultsUrl", "https://company.com/bamboo/build123");
        env.put("bamboo.repository.git.branch", "master");
        return env;
    }

    /**
     * Checks if is selected for nothing.
     */
    @Test
    void isSelectedForNothing() {
        Assertions.assertFalse(new Bamboo(new HashMap<>()).isSelected());
    }

    /**
     * Checks if is selected for bamboo.
     */
    @Test
    void isSelectedForBamboo() {
        Assertions.assertTrue(new Bamboo(this.env()).isSelected());
    }

    /**
     * Test get name.
     */
    @Test
    void name() {
        Assertions.assertEquals("bamboo", new Bamboo(this.env()).getName());
    }

    /**
     * Test get build number.
     */
    @Test
    void buildNumber() {
        Assertions.assertEquals("build123", new Bamboo(this.env()).getBuildNumber());
    }

    /**
     * Test get build url.
     */
    @Test
    void buildUrl() {
        Assertions.assertEquals("https://company.com/bamboo/build123", new Bamboo(this.env()).getBuildUrl());
    }

    /**
     * Test get branch.
     */
    @Test
    void branch() {
        Assertions.assertEquals("master", new Bamboo(this.env()).getBranch());
    }

}
