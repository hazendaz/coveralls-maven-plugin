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

class JenkinsTest {

    Map<String, String> env() {
        Map<String, String> env = new HashMap<>();
        env.put("JENKINS_URL", "http://company.com/jenkins");
        env.put("BUILD_NUMBER", "build123");
        env.put("BUILD_URL", "http://company.com/jenkins/build123");
        env.put("GIT_BRANCH", "master");
        env.put("GIT_COMMIT", "a3562fgcd2");
        return env;
    }

    @Test
    void isSelectedForNothing() {
        assertFalse(new Jenkins(new HashMap<>()).isSelected());
    }

    @Test
    void isSelectedForJenkins() {
        assertTrue(new Jenkins(env()).isSelected());
    }

    @Test
    void testGetName() {
        assertEquals("jenkins", new Jenkins(env()).getName());
    }

    @Test
    void testGetBuildNumber() {
        assertEquals("build123", new Jenkins(env()).getBuildNumber());
    }

    @Test
    void testGetBuildUrl() {
        assertEquals("http://company.com/jenkins/build123", new Jenkins(env()).getBuildUrl());
    }

    @Test
    void testGetBranch() {
        assertEquals("master", new Jenkins(env()).getBranch());
    }

    @Test
    void testGetEnvironment() {
        var properties = new Jenkins(env()).getEnvironment();
        assertEquals(4, properties.size());
        assertEquals("build123", properties.getProperty("jenkins_build_num"));
        assertEquals("http://company.com/jenkins/build123", properties.getProperty("jenkins_build_url"));
        assertEquals("master", properties.getProperty("branch"));
        assertEquals("a3562fgcd2", properties.getProperty("commit_sha"));
    }
}
