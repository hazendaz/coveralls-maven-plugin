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
package org.eluder.coveralls.maven.plugin.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Test;

class UrlUtilsTest {

    @Test
    void createInvalidUrl() {
        assertThrows(IllegalArgumentException.class, () -> {
            UrlUtils.create("sdfds");
        });
    }

    @Test
    void createValidUrl() throws URISyntaxException {
        assertEquals("http://example.org", UrlUtils.create("http://example.org").toURI().toASCIIString());
    }

    @Test
    void invalidUrlToUri() {
        assertThrows(IllegalArgumentException.class, () -> {
            UrlUtils.toUri(new URL("http://google.com?q=s|r"));
        });
    }

    @Test
    void validUrlToUri() throws MalformedURLException, URISyntaxException {
        var uri = UrlUtils.toUri(new URL("http://google.com"));
        assertEquals(new URI("http://google.com"), uri);
    }
}
