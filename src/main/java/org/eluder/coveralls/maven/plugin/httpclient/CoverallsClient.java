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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Provider;
import java.security.Security;
import java.time.Duration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.CoverallsResponse;

/**
 * The Class CoverallsClient.
 */
public class CoverallsClient {

    static {
        for (final Provider provider : Security.getProviders()) {
            if (provider.getName().startsWith("SunPKCS11")) {
                Security.removeProvider(provider.getName());
            }
        }
    }

    private static final Duration DEFAULT_SOCKET_TIMEOUT = Duration.ofSeconds(60);
    /** The Constant FILE_NAME. */
    private static final String FILE_NAME = "coveralls.json";
    private static final String USER_AGENT_STRING = "coveralls-maven-plugin";

    /** The Constant MIME_TYPE. */
    private static final ContentType MIME_TYPE = ContentType.create("application/json", StandardCharsets.UTF_8);

    /** The coveralls url. */
    private final String coverallsUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Instantiates a new coveralls client.
     *
     * @param coverallsUrl the coveralls url
     */
    public CoverallsClient(final String coverallsUrl) {
        this(coverallsUrl, new HttpClientFactory(coverallsUrl).create(), new ObjectMapper());
    }

    /**
     * Instantiates a new coveralls client.
     *
     * @param coverallsUrl the coveralls url
     * @param httpClient the http client
     * @param objectMapper the object mapper
     */
    public CoverallsClient(final String coverallsUrl, final HttpClient httpClient, final ObjectMapper objectMapper) {
        this.coverallsUrl = coverallsUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Submit.
     *
     * @param file the file
     *
     * @return the coveralls response
     *
     * @throws ProcessingException the processing exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CoverallsResponse submit(final File file) throws ProcessingException, IOException, InterruptedException {
        final Path filePath = file.toPath();

        final Iterable<byte[]> multipartData = List.of("--boundary\r\n".getBytes(),
                "Content-Disposition: form-data; name=\"json_file\"; filename=\"".getBytes(),
                FILE_NAME.getBytes(),
                "\"\r\nContent-Type: application/octet-stream;charset=UTF-8\r\n\r\n".getBytes(),
                Files.readAllBytes(filePath), "\r\n--boundary--\r\n".getBytes());

        final HttpRequest request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(coverallsUrl))
                .timeout(DEFAULT_SOCKET_TIMEOUT)
                .header("User-Agent", USER_AGENT_STRING)
                .POST(HttpRequest.BodyPublishers.ofByteArrays(multipartData)).build();

        final HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        return parseResponse(response);
    }

    private CoverallsResponse parseResponse(final HttpResponse<InputStream> response)
            throws ProcessingException, IOException {
        if (response.statusCode() >= 500) {
            throw new IOException(getResponseErrorMessage(response, "Coveralls API internal error"));
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            CoverallsResponse cr = objectMapper.readValue(reader, CoverallsResponse.class);
            if (cr.isError()) {
                throw new ProcessingException(getResponseErrorMessage(response, cr.getMessage()));
            }
            return cr;
        } catch (final IOException ex) {
            throw new ProcessingException(getResponseErrorMessage(response, ex.getMessage()), ex);
        }
        if (result.errorType == SubmitResult.ErrorType.IO) {
            throw new IOException(result.errorMessage);
        }
        return result.response;
    }

    private String getResponseErrorMessage(final HttpResponse<InputStream> response, final String message) {
        final StringBuilder errorMessage = new StringBuilder("Report submission to Coveralls API failed with HTTP status ")
                .append(response.statusCode());
        if (StringUtils.isNotBlank(message)) {
            errorMessage.append(": ").append(message);
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
