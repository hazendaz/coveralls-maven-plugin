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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.CoverallsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class CoverallsClientTest.
 */
@ExtendWith(MockitoExtension.class)
class CoverallsClientTest {

    /** The http client mock. */
    @Mock
    CloseableHttpClient httpClientMock;

    /** The http entity mock. */
    @Mock
    HttpEntity httpEntityMock;

    /** The http response mock. */
    @Mock
    ClassicHttpResponse httpResponseMock;

    /** The folder. */
    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    Path folder;

    /** The file. */
    File file;

    /**
     * Inits the.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void init() throws IOException {
        file = Files.createFile(folder.resolve("coverallsClientTest.tmp")).toFile();
    }

    /**
     * Constructors.
     */
    @Test
    void constructors() {
        assertNotNull(new CoverallsClient("http://test.com/coveralls"));
        assertNotNull(new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper()));
    }

    /**
     * Test submit.
     *
     * @throws UnsupportedOperationException
     *             the unsupported operation exception
     * @throws Exception
     *             the exception
     */
    @Test
    void testSubmit() throws UnsupportedOperationException, Exception {
        when(httpClientMock.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenAnswer(invocation -> {
                    HttpClientResponseHandler<?> handler = invocation.getArgument(1);
                    return handler.handleResponse(httpResponseMock);
                });
        when(httpResponseMock.getCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent()).thenReturn(coverallsResponse(new CoverallsResponse("success", false, "")));
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        client.submit(file);
    }

    /**
     * Fail on service error.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void failOnServiceError() throws IOException {
        when(httpClientMock.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenAnswer(invocation -> {
                    HttpClientResponseHandler<?> handler = invocation.getArgument(1);
                    return handler.handleResponse(httpResponseMock);
                });
        when(httpResponseMock.getCode()).thenReturn(500);
        when(httpResponseMock.getReasonPhrase()).thenReturn("Internal Error");
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(IOException.class, () -> {
            client.submit(file);
        });
    }

    /**
     * Parses the invalid response.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void parseInvalidResponse() throws IOException {
        when(httpClientMock.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenAnswer(invocation -> {
                    HttpClientResponseHandler<?> handler = invocation.getArgument(1);
                    return handler.handleResponse(httpResponseMock);
                });
        when(httpResponseMock.getCode()).thenReturn(200);
        when(httpResponseMock.getReasonPhrase()).thenReturn("OK");
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent())
                .thenReturn(new ByteArrayInputStream("{bogus}".getBytes(StandardCharsets.UTF_8)));
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(ProcessingException.class, () -> {
            client.submit(file);
        });
    }

    /**
     * Parses the errorous response.
     *
     * @throws UnsupportedOperationException
     *             the unsupported operation exception
     * @throws Exception
     *             the exception
     */
    @Test
    void parseErrorousResponse() throws UnsupportedOperationException, Exception {
        when(httpClientMock.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenAnswer(invocation -> {
                    HttpClientResponseHandler<?> handler = invocation.getArgument(1);
                    return handler.handleResponse(httpResponseMock);
                });
        when(httpResponseMock.getCode()).thenReturn(400);
        when(httpResponseMock.getReasonPhrase()).thenReturn("Bad Request");
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent())
                .thenReturn(coverallsResponse(new CoverallsResponse("failure", true, "submission failed")));
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(ProcessingException.class, () -> {
            client.submit(file);
        });
    }

    /**
     * Parses the failing entity.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void parseFailingEntity() throws IOException {
        when(httpClientMock.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenAnswer(invocation -> {
                    HttpClientResponseHandler<?> handler = invocation.getArgument(1);
                    return handler.handleResponse(httpResponseMock);
                });
        when(httpResponseMock.getCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent()).thenThrow(IOException.class);
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(IOException.class, () -> {
            client.submit(file);
        });
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
    InputStream coverallsResponse(final CoverallsResponse coverallsResponse) throws JsonProcessingException {
        var content = new ObjectMapper().writeValueAsString(coverallsResponse);
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

}
