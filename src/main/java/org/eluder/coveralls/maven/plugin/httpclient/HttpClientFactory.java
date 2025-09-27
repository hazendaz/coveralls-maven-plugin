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
package org.eluder.coveralls.maven.plugin.httpclient;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.settings.Proxy;
import org.eluder.coveralls.maven.plugin.util.UrlUtils;
import org.eluder.coveralls.maven.plugin.util.Wildcards;

/**
 * A factory for creating HttpClient objects.
 */
class HttpClientFactory {

    /** The Constant DEFAULT_CONNECTION_REQUEST_TIMEOUT. */
    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(10);

    /** The Constant DEFAULT_SOCKET_TIMEOUT. */
    private static final Timeout DEFAULT_SOCKET_TIMEOUT = Timeout.ofSeconds(60);

    /** The target url. */
    private final String targetUrl;

    private final HttpClient.Builder hcb = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.ALWAYS).connectTimeout(DEFAULT_CONNECTION_TIMEOUT);

    /** The rcb. */
    private final RequestConfig.Builder rcb = RequestConfig.custom()
            .setConnectionRequestTimeout(HttpClientFactory.DEFAULT_CONNECTION_REQUEST_TIMEOUT)
            .setResponseTimeout(HttpClientFactory.DEFAULT_SOCKET_TIMEOUT);

    /**
     * Instantiates a new http client factory.
     *
     * @param targetUrl
     *            the target url
     */
    HttpClientFactory(final String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Proxy.
     *
     * @param proxy
     *            the proxy
     *
     * @return the http client factory
     */
    public HttpClientFactory proxy(final Proxy proxy) {
        if (proxy != null && isProxied(targetUrl, proxy)) {
            hcb.proxy(ProxySelector.of(new InetSocketAddress(proxy.getHost(), proxy.getPort())));

            if (StringUtils.isNotBlank(proxy.getUsername())) {
                final Authenticator authenticator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxy.getUsername(), proxy.getPassword().toCharArray());
                    }
                };
                hcb.authenticator(authenticator);
            }
        }
        return this;
    }

    /**
     * Creates a new instance of HttpClient
     *
     * @return a new instance of HttpClient
     */
    public HttpClient create() {
        return hcb.build();
    }

    /**
     * Checks if is proxied.
     *
     * @param url
     *            the url
     * @param proxy
     *            the proxy
     *
     * @return true, if is proxied
     */
    private boolean isProxied(final String url, final Proxy proxy) {
        if (StringUtils.isNotBlank(proxy.getNonProxyHosts())) {
            final var host = UrlUtils.create(url).getHost();
            final var excludes = proxy.getNonProxyHosts().split("\\|", -1);
            for (final String exclude : excludes) {
                if (exclude != null && !exclude.isBlank() && Wildcards.matches(host, exclude.trim())) {
                    return false;
                }
            }
        }
        return true;
    }
}
