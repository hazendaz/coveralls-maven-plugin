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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.maven.settings.Proxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class HttpClientFactoryTest {

    static final int PROXY_PORT = 9797;
    static final int TARGET_PORT = 9696;
    static final String TARGET_URL = "http://localhost:" + TARGET_PORT;

    @RegisterExtension
    static WireMockExtension targetServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(TARGET_PORT).dynamicHttpsPort())
            .configureStaticDsl(true).build();

    @RegisterExtension
    static WireMockExtension proxyServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(PROXY_PORT).dynamicHttpsPort())
            .configureStaticDsl(true).build();

    @Test
    void simpleRequest() throws IOException, ParseException {
        targetServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello World!")));

        var client = new HttpClientFactory(TARGET_URL).create();
        var body = EntityUtils.toString(client.execute(new HttpGet(TARGET_URL)).getEntity());

        assertEquals("Hello World!", body);
    }

    @Test
    void unAuthorizedProxyRequest() throws IOException, ParseException {
        targetServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello World!")));

        proxyServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello Proxy!")));

        var proxy = new Proxy();
        proxy.setHost("localhost");
        proxy.setPort(PROXY_PORT);
        proxy.setProtocol("http");

        var client = new HttpClientFactory(TARGET_URL).proxy(proxy).create();
        var body = EntityUtils.toString(client.execute(new HttpGet(TARGET_URL)).getEntity());

        assertEquals("Hello Proxy!", body);
    }

    @Test
    void authorixedProxyRequest() throws IOException, ParseException {
        targetServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello World!")));

        proxyServer.stubFor(get(urlMatching(".*")).withHeader("Proxy-Authorization", matching("Basic Zm9vOmJhcg=="))
                .willReturn(aResponse().withBody("Hello Proxy!")).atPriority(1));
        proxyServer.stubFor(any(urlMatching(".*"))
                .willReturn(aResponse().withStatus(407).withHeader("Proxy-Authenticate", "Basic")).atPriority(2));

        var proxy = new Proxy();
        proxy.setHost("localhost");
        proxy.setPort(PROXY_PORT);
        proxy.setProtocol("http");
        proxy.setUsername("foo");
        proxy.setPassword("bar");

        var client = new HttpClientFactory(TARGET_URL).proxy(proxy).create();
        var body = EntityUtils.toString(client.execute(new HttpGet(TARGET_URL)).getEntity());

        assertEquals("Hello Proxy!", body);
    }

    @Test
    void nonProxiedHostRequest() throws IOException, ParseException {
        targetServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello World!")));

        proxyServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello Proxy!")));

        var proxy = new Proxy();
        proxy.setHost("localhost");
        proxy.setPort(PROXY_PORT);
        proxy.setProtocol("http");
        proxy.setNonProxyHosts("localhost|example.com");

        var client = new HttpClientFactory(TARGET_URL).proxy(proxy).create();
        var body = EntityUtils.toString(client.execute(new HttpGet(TARGET_URL)).getEntity());

        assertEquals("Hello World!", body);
    }
}
