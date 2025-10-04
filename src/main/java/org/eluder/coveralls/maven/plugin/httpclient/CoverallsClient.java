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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Provider;
import java.security.Security;
import java.time.Duration;
import java.util.List;

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

    /** The Constant DEFAULT_SOCKET_TIMEOUT. */
    private static final Duration DEFAULT_SOCKET_TIMEOUT = Duration.ofSeconds(60);

    /** The Constant FILE_NAME. */
    private static final String FILE_NAME = "coveralls.json";

    /** The Constant USER_AGENT_STRING. */
    private static final String USER_AGENT_STRING = "coveralls-maven-plugin";

    /** The coveralls url. */
    private final String coverallsUrl;

    /** The http client. */
    private final HttpClient httpClient;

    /** The object mapper. */
    private final ObjectMapper objectMapper;

    /**
     * Instantiates a new Coveralls Client.
     *
     * @param coverallsUrl
     *            The base url for the Coveralls API. This should generally be set to
     *
     *            <pre>
     * https://coveralls.io/api/v1/jobs
     *            </pre>
     */
    public CoverallsClient(final String coverallsUrl) {
        this(coverallsUrl, new HttpClientFactory(coverallsUrl).create(), new ObjectMapper());
    }

    /**
     * Instantiates a new Coveralls Client.
     *
     * @param coverallsUrl
     *            The base url for the Coveralls API. This should generally be set to
     *
     *            <pre>
     * https://coveralls.io/api/v1/jobs
     *            </pre>
     *
     * @param httpClient
     *            An implementation of {@link HttpClient}
     * @param objectMapper
     *            A Jackson {@link ObjectMapper}
     */
    public CoverallsClient(final String coverallsUrl, final HttpClient httpClient, final ObjectMapper objectMapper) {
        this.coverallsUrl = coverallsUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Submit a coveralls json file to the API.
     *
     * @param file
     *            A coveralls report that can be submitted to the jobs API
     *
     * @return An API response body deserialized to a {@link CoverallsResponse}
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws InterruptedException
     *             the interrupted exception
     */
    public CoverallsResponse submit(final File file) throws ProcessingException, IOException, InterruptedException {
        final var filePath = file.toPath();

        final Iterable<byte[]> multipartData = List.of("--boundary\r\n".getBytes(),
                "Content-Disposition: form-data; name=\"json_file\"; filename=\"".getBytes(),
                CoverallsClient.FILE_NAME.getBytes(),
                "\"\r\nContent-Type: application/json;charset=UTF-8\r\n\r\n".getBytes(),
                Files.readAllBytes(filePath), "\r\n--boundary--\r\n".getBytes());

        final var request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(this.coverallsUrl)).timeout(CoverallsClient.DEFAULT_SOCKET_TIMEOUT)
                .header("User-Agent", CoverallsClient.USER_AGENT_STRING)
                .POST(HttpRequest.BodyPublishers.ofByteArrays(multipartData)).build();

        final HttpResponse<InputStream> response = this.httpClient.send(request,
                HttpResponse.BodyHandlers.ofInputStream());
        return this.parseResponse(response);
    }

    /**
     * Parses the response.
     *
     * @param response
     *            the response
     *
     * @return the coveralls response
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private CoverallsResponse parseResponse(final HttpResponse<InputStream> response)
            throws ProcessingException, IOException {
        if (response.statusCode() >= 500) {
            throw new IOException(this.getResponseErrorMessage(response, "Coveralls API internal error"));
        }

        try (var reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            final var cr = this.objectMapper.readValue(reader, CoverallsResponse.class);
            if (cr.isError()) {
                throw new ProcessingException(this.getResponseErrorMessage(response, cr.getMessage()));
            }
            return cr;
        } catch (final IOException e) {
            throw new ProcessingException(this.getResponseErrorMessage(response, e.getMessage()), e);
        }
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
    private String getResponseErrorMessage(final HttpResponse<InputStream> response, final String message) {
        final var errorMessage = new StringBuilder("Report submission to Coveralls API failed with HTTP status ")
                .append(response.statusCode());
        if (message != null && !message.isBlank()) {
            errorMessage.append(": ").append(message);
        }
        return errorMessage.toString();
    }
}
