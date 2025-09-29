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
 * The Class CircleTest.
 */
class CircleTest {

    /**
     * Env.
     *
     * @return the map
     */
    Map<String, String> env() {
        final Map<String, String> env = new HashMap<>();
        env.put("CIRCLECI", "true");
        env.put("CIRCLE_BUILD_NUM", "build123");
        env.put("CIRCLE_BRANCH", "master");
        env.put("CIRCLE_SHA1", "a3562fgcd2");
        return env;
    }

    /**
     * Checks if is selected for nothing.
     */
    @Test
    void isSelectedForNothing() {
        Assertions.assertFalse(new Circle(new HashMap<>()).isSelected());
    }

    /**
     * Checks if is selected for circle.
     */
    @Test
    void isSelectedForCircle() {
        Assertions.assertTrue(new Circle(this.env()).isSelected());
    }

    /**
     * Test get name.
     */
    @Test
    void testGetName() {
        Assertions.assertEquals("circleci", new Circle(this.env()).getName());
    }

    /**
     * Test get build number.
     */
    @Test
    void testGetBuildNumber() {
        Assertions.assertEquals("build123", new Circle(this.env()).getBuildNumber());
    }

    /**
     * Test get branch.
     */
    @Test
    void testGetBranch() {
        Assertions.assertEquals("master", new Circle(this.env()).getBranch());
    }

    /**
     * Test get environment.
     */
    @Test
    void testGetEnvironment() {
        final var properties = new Circle(this.env()).getEnvironment();
        Assertions.assertEquals(3, properties.size());
        Assertions.assertEquals("build123", properties.getProperty("circleci_build_num"));
        Assertions.assertEquals("master", properties.getProperty("branch"));
        Assertions.assertEquals("a3562fgcd2", properties.getProperty("commit_sha"));
    }

}
