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

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;
import org.apache.maven.settings.Proxy;
import org.eluder.coveralls.maven.plugin.util.UrlUtils;
import org.eluder.coveralls.maven.plugin.util.Wildcards;

class HttpClientFactory {

    private static final Timeout DEFAULT_CONNECTION_TIMEOUT = Timeout.of(Duration.ofSeconds(10));
    private static final Timeout DEFAULT_SOCKET_TIMEOUT = Timeout.of(Duration.ofSeconds(60));

    private final String targetUrl;

    private final HttpClientBuilder hcb = HttpClientBuilder.create();
    private final RequestConfig.Builder rcb = RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
            .setResponseTimeout(DEFAULT_SOCKET_TIMEOUT);

    HttpClientFactory(final String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public HttpClientFactory proxy(final Proxy proxy) {
        if (proxy != null && isProxied(targetUrl, proxy)) {
            rcb.setProxy(new HttpHost(proxy.getProtocol(), proxy.getHost(), proxy.getPort()));
            if (StringUtils.isNotBlank(proxy.getUsername())) {
                BasicCredentialsProvider cp = new BasicCredentialsProvider();
                cp.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword().toCharArray()));
                hcb.setDefaultCredentialsProvider(cp);
            }
        }
        return this;
    }

    public CloseableHttpClient create() {
        return hcb.setDefaultRequestConfig(rcb.build()).build();
    }

    private boolean isProxied(final String url, final Proxy proxy) {
        if (StringUtils.isNotBlank(proxy.getNonProxyHosts())) {
            String host = UrlUtils.create(url).getHost();
            String[] excludes = proxy.getNonProxyHosts().split("\\|", -1);
            for (String exclude : excludes) {
                if (StringUtils.isNotBlank(exclude) && Wildcards.matches(host, exclude.trim())) {
                    return false;
                }
            }
        }
        return true;
    }
}
