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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class UrlUtilsTest.
 */
class UrlUtilsTest {

    /**
     * Creates the illegal argument exception.
     */
    @Test
    void createIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UrlUtils.create("sdfds");
        });
    }

    /**
     * Creates the malformed url exception.
     */
    @Test
    void createMalformedUrlException() {
        final var thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UrlUtils.create("abc://example.org");
        });
        // Assert that the cause is MalformedURLException
        Assertions.assertEquals(MalformedURLException.class, thrown.getCause().getClass());
    }

    /**
     * Creates the uri syntax exception.
     */
    @Test
    void createUriSynatxException() {
        final var thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UrlUtils.create("https://invalid|url");
        });
        // Assert that the cause is URISyntaxException
        Assertions.assertEquals(URISyntaxException.class, thrown.getCause().getClass());
    }

    /**
     * Creates the valid url.
     *
     * @throws URISyntaxException
     *             the URI syntax exception
     */
    @Test
    void createValidUrl() throws URISyntaxException {
        Assertions.assertEquals("https://example.org", UrlUtils.create("https://example.org").toURI().toASCIIString());
    }

    /**
     * Invalid url to uri.
     *
     * @throws MalformedURLException
     *             the malformed URL exception
     */
    @Test
    void invalidUrlToUri() throws MalformedURLException {
        final var url = new URL("https://google.com?q=s|r");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UrlUtils.toUri(url);
        });
    }

    /**
     * Valid url to uri.
     *
     * @throws MalformedURLException
     *             the malformed URL exception
     * @throws URISyntaxException
     *             the URI syntax exception
     */
    @Test
    void validUrlToUri() throws MalformedURLException, URISyntaxException {
        final var uri = UrlUtils.toUri(new URL("https://google.com"));
        Assertions.assertEquals(new URI("https://google.com"), uri);
    }
}
