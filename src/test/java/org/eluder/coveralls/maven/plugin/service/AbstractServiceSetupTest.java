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

class AbstractServiceSetupTest {

    @Test
    void getMissingProperty() {
        AbstractServiceSetup serviceSetup = create(new HashMap<String, String>());
        assertNull(serviceSetup.getProperty("property"));
    }

    @Test
    void testGetProperty() {
        Map<String, String> env = new HashMap<String, String>();
        env.put("CI_NAME", "bamboo");
        assertEquals("bamboo", create(env).getProperty("CI_NAME"));
    }

    @Test
    void addPropertyWithoutName() {
        assertThrows(IllegalArgumentException.class, () -> {
            create(new HashMap<String, String>()).addProperty(new Properties(), null, "value");
        });
    }

    @Test
    void addPropertyWithoutValue() {
        Properties properties = new Properties();
        create(new HashMap<String, String>()).addProperty(properties, "prop", " ");
        assertNull(properties.getProperty("prop"));
    }

    @Test
    void addPropertyWithValue() {
        Properties properties = new Properties();
        create(new HashMap<String, String>()).addProperty(properties, "prop", "value");
        assertEquals("value", properties.getProperty("prop"));
    }

    @Test
    void getDefaultValues() {
        AbstractServiceSetup serviceSetup = create(new HashMap<String, String>());
        assertNull(serviceSetup.getName());
        assertNull(serviceSetup.getJobId());
        assertNull(serviceSetup.getBuildNumber());
        assertNull(serviceSetup.getBuildUrl());
        assertNull(serviceSetup.getBranch());
        assertNull(serviceSetup.getPullRequest());
        assertNull(serviceSetup.getEnvironment());
    }

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
