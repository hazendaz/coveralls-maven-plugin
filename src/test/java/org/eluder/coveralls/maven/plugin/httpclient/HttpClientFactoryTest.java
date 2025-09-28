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
package org.eluder.coveralls.maven.plugin.httpclient;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.maven.settings.Proxy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * The Class HttpClientFactoryTest.
 */
class HttpClientFactoryTest {

    /** The Constant PROXY_PORT. */
    static final int PROXY_PORT = 9797;

    /** The Constant TARGET_PORT. */
    static final int TARGET_PORT = 9696;

    /** The Constant TARGET_URL. */
    static final String TARGET_URL = "http://localhost:" + HttpClientFactoryTest.TARGET_PORT;

    /** The target server. */
    @RegisterExtension
    static WireMockExtension targetServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(HttpClientFactoryTest.TARGET_PORT).dynamicHttpsPort())
            .configureStaticDsl(true).build();

    /** The proxy server. */
    @RegisterExtension
    static WireMockExtension proxyServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(HttpClientFactoryTest.PROXY_PORT).dynamicHttpsPort())
            .configureStaticDsl(true).build();

    /**
     * Simple request.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ParseException
     *             the parse exception
     */
    @Test
    void simpleRequest() throws IOException, ParseException {
        HttpClientFactoryTest.targetServer.stubFor(
                WireMock.get(WireMock.urlMatching(".*")).willReturn(WireMock.aResponse().withBody("Hello World!")));

        final var client = new HttpClientFactory(HttpClientFactoryTest.TARGET_URL).create();
        final var body = client.execute(new HttpGet(HttpClientFactoryTest.TARGET_URL),
                response -> EntityUtils.toString(response.getEntity()));

        Assertions.assertEquals("Hello World!", body);
    }

    /**
     * Un authorized proxy request.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ParseException
     *             the parse exception
     */
    @Test
    void unAuthorizedProxyRequest() throws IOException, ParseException {
        HttpClientFactoryTest.targetServer.stubFor(
                WireMock.get(WireMock.urlMatching(".*")).willReturn(WireMock.aResponse().withBody("Hello World!")));

        HttpClientFactoryTest.proxyServer.stubFor(
                WireMock.get(WireMock.urlMatching(".*")).willReturn(WireMock.aResponse().withBody("Hello Proxy!")));

        final var proxy = new Proxy();
        proxy.setHost("localhost");
        proxy.setPort(HttpClientFactoryTest.PROXY_PORT);
        proxy.setProtocol("http");

        final var client = new HttpClientFactory(HttpClientFactoryTest.TARGET_URL).proxy(proxy).create();
        final var body = client.execute(new HttpGet(HttpClientFactoryTest.TARGET_URL),
                response -> EntityUtils.toString(response.getEntity()));

        Assertions.assertEquals("Hello Proxy!", body);
    }

    /**
     * Authorixed proxy request.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ParseException
     *             the parse exception
     */
    @Test
    void authorixedProxyRequest() throws IOException, ParseException {
        HttpClientFactoryTest.targetServer.stubFor(
                WireMock.get(WireMock.urlMatching(".*")).willReturn(WireMock.aResponse().withBody("Hello World!")));

        HttpClientFactoryTest.proxyServer.stubFor(WireMock.get(WireMock.urlMatching(".*"))
                .withHeader("Proxy-Authorization", WireMock.matching("Basic Zm9vOmJhcg=="))
                .willReturn(WireMock.aResponse().withBody("Hello Proxy!")).atPriority(1));
        HttpClientFactoryTest.proxyServer.stubFor(WireMock.any(WireMock.urlMatching(".*"))
                .willReturn(WireMock.aResponse().withStatus(407).withHeader("Proxy-Authenticate", "Basic"))
                .atPriority(2));

        final var proxy = new Proxy();
        proxy.setHost("localhost");
        proxy.setPort(HttpClientFactoryTest.PROXY_PORT);
        proxy.setProtocol("http");
        proxy.setUsername("foo");
        proxy.setPassword("bar");

        final var client = new HttpClientFactory(HttpClientFactoryTest.TARGET_URL).proxy(proxy).create();
        final var body = client.execute(new HttpGet(HttpClientFactoryTest.TARGET_URL),
                response -> EntityUtils.toString(response.getEntity()));

        Assertions.assertEquals("Hello Proxy!", body);
    }

    /**
     * Non proxied host request.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ParseException
     *             the parse exception
     */
    @Test
    void nonProxiedHostRequest() throws IOException, ParseException {
        HttpClientFactoryTest.targetServer.stubFor(
                WireMock.get(WireMock.urlMatching(".*")).willReturn(WireMock.aResponse().withBody("Hello World!")));

        HttpClientFactoryTest.proxyServer.stubFor(
                WireMock.get(WireMock.urlMatching(".*")).willReturn(WireMock.aResponse().withBody("Hello Proxy!")));

        final var proxy = new Proxy();
        proxy.setHost("localhost");
        proxy.setPort(HttpClientFactoryTest.PROXY_PORT);
        proxy.setProtocol("http");
        proxy.setNonProxyHosts("localhost|example.com");

        final var client = new HttpClientFactory(HttpClientFactoryTest.TARGET_URL).proxy(proxy).create();
        final var body = client.execute(new HttpGet(HttpClientFactoryTest.TARGET_URL),
                response -> EntityUtils.toString(response.getEntity()));

        Assertions.assertEquals("Hello World!", body);
    }
}
