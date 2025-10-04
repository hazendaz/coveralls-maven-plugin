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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.CoverallsResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class CoverallsClientTest.
 */
@ExtendWith(MockitoExtension.class)
class CoverallsClientTest {

    /** The http client mock. */
    @Mock
    HttpClient httpClientMock;

    /** The http response mock. */
    @Mock
    HttpResponse<InputStream> httpResponseMock;

    /** The folder. */
    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    Path folder;

    /** The file. */
    File file;

    /**
     * Inits the Coveralls Client.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void init() throws IOException {
        this.file = Files.createFile(this.folder.resolve("coverallsClientTest.tmp")).toFile();
    }

    /**
     * Constructors.
     */
    @Test
    void constructors() {
        Assertions.assertNotNull(new CoverallsClient("https://test.com/coveralls"));
        Assertions.assertNotNull(
                new CoverallsClient("https://test.com/coveralls", this.httpClientMock, new ObjectMapper()));
    }

    /**
     * Test submit.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws InterruptedException
     *             the interrupted exception
     * @throws ProcessingException
     *             the processing exception
     */
    @Test
    void submit() throws IOException, InterruptedException, ProcessingException {
        Mockito.when(this.httpClientMock.send(ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.any(HttpResponse.BodyHandler.class))).thenReturn(this.httpResponseMock);
        Mockito.when(this.httpResponseMock.statusCode()).thenReturn(200);
        Mockito.when(this.httpResponseMock.body())
                .thenReturn(this.coverallsResponse(new CoverallsResponse("success", false, "")));
        final var client = new CoverallsClient("http://test.com/coveralls", this.httpClientMock, new ObjectMapper());
        client.submit(this.file);
    }

    /**
     * Fail on service error.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws InterruptedException
     *             the interrupted exception
     */
    @Test
    void failOnServiceError() throws IOException, InterruptedException {
        Mockito.when(this.httpClientMock.send(ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.any(HttpResponse.BodyHandler.class))).thenReturn(this.httpResponseMock);
        Mockito.when(this.httpResponseMock.statusCode()).thenReturn(500);
        final var client = new CoverallsClient("http://test.com/coveralls", this.httpClientMock, new ObjectMapper());
        Assertions.assertThrows(IOException.class, () -> client.submit(this.file));
    }

    /**
     * Parses the invalid response.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws InterruptedException
     *             the interrupted exception
     */
    @Test
    void parseInvalidResponse() throws IOException, InterruptedException {
        Mockito.when(this.httpClientMock.send(ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.any(HttpResponse.BodyHandler.class))).thenReturn(this.httpResponseMock);
        Mockito.when(this.httpResponseMock.statusCode()).thenReturn(200);
        Mockito.when(this.httpResponseMock.body())
                .thenReturn(new ByteArrayInputStream("{bogus}".getBytes(StandardCharsets.UTF_8)));
        final var client = new CoverallsClient("http://test.com/coveralls", this.httpClientMock, new ObjectMapper());
        Assertions.assertThrows(ProcessingException.class, () -> client.submit(this.file));
    }

    /**
     * Parses the errorous response.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws InterruptedException
     *             the interrupted exception
     */
    @Test
    void parseErrorousResponse() throws IOException, InterruptedException {
        Mockito.when(this.httpClientMock.send(ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.any(HttpResponse.BodyHandler.class))).thenReturn(this.httpResponseMock);
        Mockito.when(this.httpResponseMock.statusCode()).thenReturn(400);
        Mockito.when(this.httpResponseMock.body())
                .thenReturn(this.coverallsResponse(new CoverallsResponse("failure", true, "submission failed")));
        final var client = new CoverallsClient("http://test.com/coveralls", this.httpClientMock, new ObjectMapper());
        Assertions.assertThrows(ProcessingException.class, () -> client.submit(this.file));
    }

    /**
     * Parses the failing entity.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws InterruptedException
     *             the interrupted exception
     */
    @Test
    void parseFailingEntity() throws IOException, InterruptedException {
        Mockito.when(this.httpClientMock.send(ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.any(HttpResponse.BodyHandler.class))).thenReturn(this.httpResponseMock);
        Mockito.when(this.httpResponseMock.statusCode()).thenReturn(200);
        Mockito.when(this.httpResponseMock.body()).thenReturn(IOUtils.toInputStream("{}", StandardCharsets.UTF_8));
        final var mockMapper = Mockito.mock(ObjectMapper.class); // Jackson's object mapper can potentially throw
        // exception
        Mockito.when(mockMapper.readValue(ArgumentMatchers.any(BufferedReader.class),
                ArgumentMatchers.eq(CoverallsResponse.class))).thenThrow(IOException.class);
        final var client = new CoverallsClient("http://test.com/coveralls", this.httpClientMock, mockMapper);
        Assertions.assertThrows(ProcessingException.class, () -> client.submit(this.file));
    }

    /**
     * Coveralls response.
     *
     * @param coverallsResponse
     *            the coveralls response
     *
     * @return the input stream
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    private InputStream coverallsResponse(final CoverallsResponse coverallsResponse) throws JsonProcessingException {
        final var content = new ObjectMapper().writeValueAsString(coverallsResponse);
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

}
