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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.Security;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.CoverallsResponse;

public class CoverallsClient {

    static {
        for (Provider provider : Security.getProviders()) {
            if (provider.getName().startsWith("SunPKCS11")) {
                Security.removeProvider(provider.getName());
            }
        }
    }

    private static final String FILE_NAME = "coveralls.json";
    private static final ContentType MIME_TYPE = ContentType.create("application/octet-stream", StandardCharsets.UTF_8);

    private final String coverallsUrl;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CoverallsClient(final String coverallsUrl) {
        this(coverallsUrl, new HttpClientFactory(coverallsUrl).create(), new ObjectMapper());
    }

    public CoverallsClient(final String coverallsUrl, final CloseableHttpClient httpClient,
            final ObjectMapper objectMapper) {
        this.coverallsUrl = coverallsUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public CoverallsResponse submit(final File file) throws ProcessingException, IOException {
        HttpEntity entity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.STRICT)
                .addBinaryBody("json_file", file, MIME_TYPE, FILE_NAME).build();
        HttpPost post = new HttpPost(coverallsUrl);
        post.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(post);
        return parseResponse(response);
    }

    private CoverallsResponse parseResponse(final CloseableHttpResponse response)
            throws ProcessingException, IOException {
        HttpEntity entity = response.getEntity();
        if (response.getCode() >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            throw new IOException(getResponseErrorMessage(response, "Coveralls API internal error"));
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8))) {
            CoverallsResponse cr = objectMapper.readValue(reader, CoverallsResponse.class);
            if (cr.isError()) {
                throw new ProcessingException(getResponseErrorMessage(response, cr.getMessage()));
            }
            return cr;
        } catch (JsonProcessingException ex) {
            throw new ProcessingException(getResponseErrorMessage(response, ex.getMessage()), ex);
        }
    }

    private String getResponseErrorMessage(final HttpResponse response, final String message) {
        int status = response.getCode();
        String reason = response.getReasonPhrase();
        StringBuilder errorMessage = new StringBuilder("Report submission to Coveralls API failed with HTTP status ")
                .append(status).append(":");
        if (StringUtils.isNotBlank(reason)) {
            errorMessage.append(" ").append(reason);
        }
        if (StringUtils.isNotBlank(message)) {
            errorMessage.append(" (").append(message).append(")");
        }
        return errorMessage.toString();
    }
}
