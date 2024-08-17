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
import static org.mockito.Mockito.mock;
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

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicStatusLine;
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
    HttpClient httpClientMock;

    @Mock
    HttpResponse httpResponseMock;

    @Mock
    HttpEntity httpEntityMock;

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    Path folder;

    File file;

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
    void testSubmit() throws UnsupportedOperationException, Exception {
        StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK");
        when(httpResponseMock.getStatusLine()).thenReturn(statusLine);
        when(httpClientMock.execute(any(HttpUriRequest.class))).thenReturn(httpResponseMock);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent()).thenReturn(coverallsResponse(new CoverallsResponse("success", false, "")));
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        client.submit(file);
    }

    @Test
    void failOnServiceError() throws ClientProtocolException, IOException {
        StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 500, "Internal Error");
        when(httpClientMock.execute(any(HttpUriRequest.class))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(statusLine);
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(IOException.class, () -> {
            client.submit(file);
        });
    }

    @Test
    void parseInvalidResponse() throws ClientProtocolException, IOException {
        StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK");
        when(httpClientMock.execute(any(HttpUriRequest.class))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(statusLine);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent())
                .thenReturn(new ByteArrayInputStream("{bogus}".getBytes(StandardCharsets.UTF_8)));
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(ProcessingException.class, () -> {
            client.submit(file);
        });
    }

    @Test
    void parseErrorousResponse() throws UnsupportedOperationException, Exception {
        StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 400, "Bad Request");
        when(httpClientMock.execute(any(HttpUriRequest.class))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(statusLine);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent())
                .thenReturn(coverallsResponse(new CoverallsResponse("failure", true, "submission failed")));
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(ProcessingException.class, () -> {
            client.submit(file);
        });
    }

    @Test
    void parseFailingEntity() throws ClientProtocolException, IOException {
        StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK");
        when(httpClientMock.execute(any(HttpUriRequest.class))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(statusLine);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent()).thenThrow(IOException.class);
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(IOException.class, () -> {
            client.submit(file);
        });
    }

    @Test
    void parseEntityWithoutContentType() throws ClientProtocolException, IOException {
        StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 400, "Bad Request");
        when(httpResponseMock.getStatusLine()).thenReturn(statusLine);
        when(httpClientMock.execute(any(HttpUriRequest.class))).thenReturn(httpResponseMock);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        var header = mock(Header.class);
        var element = mock(HeaderElement.class);
        when(element.getName()).thenReturn("HeaderName");
        var pair = mock(NameValuePair.class);
        when(pair.getName()).thenReturn("name");
        when(element.getParameters()).thenReturn(new NameValuePair[] { pair });
        when(header.getElements()).thenReturn(new HeaderElement[] { element });
        when(httpEntityMock.getContentType()).thenReturn(header);
        var client = new CoverallsClient("http://test.com/coveralls", httpClientMock, new ObjectMapper());
        assertThrows(ProcessingException.class, () -> {
            client.submit(file);
        });
    }

    InputStream coverallsResponse(final CoverallsResponse coverallsResponse) throws JsonProcessingException {
        var content = new ObjectMapper().writeValueAsString(coverallsResponse);
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

}
