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
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class AbstractServiceSetupTest.
 */
class AbstractServiceSetupTest {

    /**
     * Gets the missing property.
     */
    @Test
    void getMissingProperty() {
        final var serviceSetup = this.create(new HashMap<>());
        Assertions.assertNull(serviceSetup.getProperty("property"));
    }

    /**
     * Test get property.
     */
    @Test
    void testGetProperty() {
        final Map<String, String> env = new HashMap<>();
        env.put("CI_NAME", "bamboo");
        Assertions.assertEquals("bamboo", this.create(env).getProperty("CI_NAME"));
    }

    /**
     * Adds the property without name.
     */
    @Test
    void addPropertyWithoutName() {
        final var setup = this.create(new HashMap<>());
        final var properties = new Properties();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            setup.addProperty(properties, null, "value");
        });
    }

    /**
     * Adds the property without value.
     */
    @Test
    void addPropertyWithoutValue() {
        final var properties = new Properties();
        this.create(new HashMap<>()).addProperty(properties, "prop", " ");
        Assertions.assertNull(properties.getProperty("prop"));
    }

    /**
     * Adds the property with value.
     */
    @Test
    void addPropertyWithValue() {
        final var properties = new Properties();
        this.create(new HashMap<>()).addProperty(properties, "prop", "value");
        Assertions.assertEquals("value", properties.getProperty("prop"));
    }

    /**
     * Gets the default values.
     */
    @Test
    void getDefaultValues() {
        final var serviceSetup = this.create(new HashMap<>());
        Assertions.assertNull(serviceSetup.getName());
        Assertions.assertNull(serviceSetup.getJobId());
        Assertions.assertNull(serviceSetup.getBuildNumber());
        Assertions.assertNull(serviceSetup.getBuildUrl());
        Assertions.assertNull(serviceSetup.getBranch());
        Assertions.assertNull(serviceSetup.getPullRequest());
        Assertions.assertNull(serviceSetup.getEnvironment());
    }

    /**
     * Creates the.
     *
     * @param env
     *            the env
     */
    private AbstractServiceSetup create(final Map<String, String> env) {
        return new AbstractServiceSetup(env) {
            @Override
            public boolean isSelected() {
                return true;
            }

            @Override
            public String getName() {
                return this.getProperty("CI_NAME");
            }
        };
    }

}
