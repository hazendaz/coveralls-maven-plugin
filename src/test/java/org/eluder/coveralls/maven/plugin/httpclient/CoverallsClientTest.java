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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.CoverallsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoverallsClientTest {

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<InputStream> httpResponseMock;

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    private Path folder;

    private File file;

    @BeforeEach
    void init() throws IOException {
        file = Files.createFile(folder.resolve("coverallsClientTest.tmp")).toFile();
    }

    @Test
    void constructors() {
        assertNotNull(new CoverallsClient("http://test.com/coveralls"));
        assertNotNull(new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper()));
    }

    @Test
    void testSubmit() throws Exception {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(coverallsResponse(new CoverallsResponse("success", false, "")));
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        client.submit(file);
    }

    @Test
    void failOnServiceError() throws Exception {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(500);
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(IOException.class, () -> client.submit(file));
    }

    @Test
    void parseInvalidResponse() throws Exception {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(new ByteArrayInputStream("{bogus}".getBytes(StandardCharsets.UTF_8)));
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(ProcessingException.class, () -> client.submit(file));
    }

    @Test
    void parseErrorousResponse() throws Exception {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(400);
        when(httpResponseMock.body())
                .thenReturn(coverallsResponse(new CoverallsResponse("failure", true, "submission failed")));
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(ProcessingException.class, () -> client.submit(file));
    }

    @Test
    void parseFailingEntity() throws Exception {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(IOUtils.toInputStream("{}", StandardCharsets.UTF_8));
        final ObjectMapper mockMapper = mock(ObjectMapper.class); // Jackson's object mapper can potentially throw
                                                                  // exception
        when(mockMapper.readValue(any(BufferedReader.class), eq(CoverallsResponse.class))).thenThrow(IOException.class);
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, mockMapper);
        assertThrows(ProcessingException.class, () -> client.submit(file));
    }

    private InputStream coverallsResponse(final CoverallsResponse coverallsResponse) throws JsonProcessingException {
        var content = new ObjectMapper().writeValueAsString(coverallsResponse);
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

}
