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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
        var serviceSetup = create(new HashMap<>());
        assertNull(serviceSetup.getProperty("property"));
    }

    /**
     * Test get property.
     */
    @Test
    void testGetProperty() {
        Map<String, String> env = new HashMap<>();
        env.put("CI_NAME", "bamboo");
        assertEquals("bamboo", create(env).getProperty("CI_NAME"));
    }

    /**
     * Adds the property without name.
     */
    @Test
    void addPropertyWithoutName() {
        assertThrows(IllegalArgumentException.class, () -> {
            create(new HashMap<>()).addProperty(new Properties(), null, "value");
        });
    }

    /**
     * Adds the property without value.
     */
    @Test
    void addPropertyWithoutValue() {
        var properties = new Properties();
        create(new HashMap<>()).addProperty(properties, "prop", " ");
        assertNull(properties.getProperty("prop"));
    }

    /**
     * Adds the property with value.
     */
    @Test
    void addPropertyWithValue() {
        var properties = new Properties();
        create(new HashMap<>()).addProperty(properties, "prop", "value");
        assertEquals("value", properties.getProperty("prop"));
    }

    /**
     * Gets the default values.
     */
    @Test
    void getDefaultValues() {
        var serviceSetup = create(new HashMap<>());
        assertNull(serviceSetup.getName());
        assertNull(serviceSetup.getJobId());
        assertNull(serviceSetup.getBuildNumber());
        assertNull(serviceSetup.getBuildUrl());
        assertNull(serviceSetup.getBranch());
        assertNull(serviceSetup.getPullRequest());
        assertNull(serviceSetup.getEnvironment());
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
                return getProperty("CI_NAME");
            }
        };
    }

}
