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
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.CoverallsResponse;

/**
 * The Class CoverallsClient.
 */
public class CoverallsClient {

    static {
        for (Provider provider : Security.getProviders()) {
            if (provider.getName().startsWith("SunPKCS11")) {
                Security.removeProvider(provider.getName());
            }
        }
    }

    /** The Constant FILE_NAME. */
    private static final String FILE_NAME = "coveralls.json";

    /** The Constant MIME_TYPE. */
    private static final ContentType MIME_TYPE = ContentType.create("application/octet-stream", StandardCharsets.UTF_8);

    /** The coveralls url. */
    private final String coverallsUrl;

    /** The http client. */
    private final CloseableHttpClient httpClient;

    /** The object mapper. */
    private final ObjectMapper objectMapper;

    /**
     * Instantiates a new coveralls client.
     *
     * @param coverallsUrl
     *            the coveralls url
     */
    public CoverallsClient(final String coverallsUrl) {
        this(coverallsUrl, new HttpClientFactory(coverallsUrl).create(), new ObjectMapper());
    }

    /**
     * Instantiates a new coveralls client.
     *
     * @param coverallsUrl
     *            the coveralls url
     * @param httpClient
     *            the http client
     * @param objectMapper
     *            the object mapper
     */
    public CoverallsClient(final String coverallsUrl, final CloseableHttpClient httpClient,
            final ObjectMapper objectMapper) {
        this.coverallsUrl = coverallsUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Submit.
     *
     * @param file
     *            the file
     *
     * @return the coveralls response
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public CoverallsResponse submit(final File file) throws ProcessingException, IOException {
        HttpEntity entity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.STRICT)
                .addBinaryBody("json_file", file, MIME_TYPE, FILE_NAME).build();
        HttpPost post = new HttpPost(coverallsUrl);
        post.setEntity(entity);
        SubmitResult result = httpClient.execute(post, response -> {
            HttpEntity responseEntity = response.getEntity();
            if (response.getCode() >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                return new SubmitResult(getResponseErrorMessage(response, "Coveralls API internal error"),
                        SubmitResult.ErrorType.IO);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(responseEntity.getContent(), StandardCharsets.UTF_8))) {
                CoverallsResponse cr = objectMapper.readValue(reader, CoverallsResponse.class);
                if (cr.isError()) {
                    return new SubmitResult(getResponseErrorMessage(response, cr.getMessage()),
                            SubmitResult.ErrorType.PROCESSING);
                }
                return new SubmitResult(cr);
            } catch (JsonProcessingException ex) {
                return new SubmitResult(getResponseErrorMessage(response, ex.getMessage()), ex,
                        SubmitResult.ErrorType.PROCESSING);
            }
        });
        if (result.errorType == SubmitResult.ErrorType.PROCESSING) {
            throw new ProcessingException(result.errorMessage, result.errorCause);
        } else if (result.errorType == SubmitResult.ErrorType.IO) {
            throw new IOException(result.errorMessage);
        }
        return result.response;
    }

    /**
     * Gets the response error message.
     *
     * @param response
     *            the response
     * @param message
     *            the message
     *
     * @return the response error message
     */
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

    /**
     * The Class SubmitResult.
     */
    private static class SubmitResult {

        /**
         * The Enum ErrorType.
         */
        enum ErrorType {

            /** The none. */
            NONE,
            /** The io. */
            IO,
            /** The processing. */
            PROCESSING
        }

        /** The response. */
        final CoverallsResponse response;

        /** The error message. */
        final String errorMessage;

        /** The error cause. */
        final Throwable errorCause;

        /** The error type. */
        final ErrorType errorType;

        /**
         * Instantiates a new submit result.
         *
         * @param response
         *            the response
         */
        SubmitResult(CoverallsResponse response) {
            this.response = response;
            this.errorMessage = null;
            this.errorCause = null;
            this.errorType = ErrorType.NONE;
        }

        /**
         * Instantiates a new submit result.
         *
         * @param errorMessage
         *            the error message
         * @param errorType
         *            the error type
         */
        SubmitResult(String errorMessage, ErrorType errorType) {
            this.response = null;
            this.errorMessage = errorMessage;
            this.errorCause = null;
            this.errorType = errorType;
        }

        /**
         * Instantiates a new submit result.
         *
         * @param errorMessage
         *            the error message
         * @param errorCause
         *            the error cause
         * @param errorType
         *            the error type
         */
        SubmitResult(String errorMessage, Throwable errorCause, ErrorType errorType) {
            this.response = null;
            this.errorMessage = errorMessage;
            this.errorCause = errorCause;
            this.errorType = errorType;
        }
    }

}
