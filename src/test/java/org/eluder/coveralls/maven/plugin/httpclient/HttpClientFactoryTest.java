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
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import java.io.IOException;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.maven.settings.Proxy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class HttpClientFactoryTest {

    private final int PROXY_PORT = 9797;
    private final int TARGET_PORT = 9696;
    private final String TARGET_URL = "http://localhost:" + TARGET_PORT;

    @RegisterExtension
    WireMockExtension targetServer = WireMockExtension.newInstance()
        .options(wireMockConfig().httpsPort(TARGET_PORT).dynamicPort()
        .notifier(new ConsoleNotifier(true))).build();

    @RegisterExtension
    WireMockExtension proxyServer = WireMockExtension.newInstance()
        .options(wireMockConfig().httpsPort(PROXY_PORT).dynamicPort()
        .notifier(new ConsoleNotifier(true))).proxyMode(true).build();

    @Test
    void testSimpleRequest() throws ParseException, ClientProtocolException, IOException {
        targetServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello World!")));

        CloseableHttpClient client = new HttpClientFactory(TARGET_URL).create();
        String body = EntityUtils.toString(client.execute(new HttpGet(TARGET_URL)).getEntity());

        Assertions.assertEquals("Hello World!", body);
    }

    @Test
    void testUnAuthorizedProxyRequest() throws ParseException, ClientProtocolException, IOException {
        targetServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello World!")));

        proxyServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello Proxy!")));

        Proxy proxy = new Proxy();
        proxy.setHost("localhost");
        proxy.setPort(PROXY_PORT);
        proxy.setProtocol("http");

        CloseableHttpClient client = new HttpClientFactory(TARGET_URL).proxy(proxy).create();
        String body = EntityUtils.toString(client.execute(new HttpGet(TARGET_URL)).getEntity());

        Assertions.assertEquals("Hello Proxy!", body);
    }

    @Test
    void testAuthorixedProxyRequest() throws ParseException, ClientProtocolException, IOException {
        targetServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello World!")));

        proxyServer.stubFor(get(urlMatching(".*")).withHeader("Proxy-Authorization", matching("Basic Zm9vOmJhcg=="))
                .willReturn(aResponse().withBody("Hello Proxy!"))
                .atPriority(1));
        proxyServer.stubFor(any(urlMatching(".*"))
                .willReturn(aResponse().withStatus(407).withHeader("Proxy-Authenticate", "Basic"))
                .atPriority(2));

        Proxy proxy = new Proxy();
        proxy.setHost("localhost");
        proxy.setPort(PROXY_PORT);
        proxy.setProtocol("http");
        proxy.setUsername("foo");
        proxy.setPassword("bar");

        CloseableHttpClient client = new HttpClientFactory(TARGET_URL).proxy(proxy).create();
        String body = EntityUtils.toString(client.execute(new HttpGet(TARGET_URL)).getEntity());

        Assertions.assertEquals("Hello Proxy!", body);
    }

    @Test
    void testNonProxiedHostRequest() throws ParseException, ClientProtocolException, IOException {
        targetServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello World!")));

        proxyServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withBody("Hello Proxy!")));

        Proxy proxy = new Proxy();
        proxy.setHost("localhost");
        proxy.setPort(PROXY_PORT);
        proxy.setProtocol("http");
        proxy.setNonProxyHosts("localhost|example.com");

        CloseableHttpClient client = new HttpClientFactory(TARGET_URL).proxy(proxy).create();
        String body = EntityUtils.toString(client.execute(new HttpGet(TARGET_URL)).getEntity());

        Assertions.assertEquals("Hello World!", body);
    }
}
