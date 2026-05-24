/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2026 The Coveralls Maven Plugin Project Contributors:
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
package org.eluder.coveralls.maven.plugin.parser;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;
import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for AbstractXmlEventParser error paths.
 */
class AbstractXmlEventParserTest {

    /**
     * Parse malformed XML should throw ProcessingException wrapping XMLStreamException.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void parseMalformedXmlThrowsProcessingException() throws IOException {
        final var sourceLoader = Mockito.mock(SourceLoader.class);
        final var callback = Mockito.mock(SourceCallback.class);

        final var parser = new JaCoCoParser(TestIoUtil.getFile("malformed.xml"), sourceLoader);

        Assertions.assertThrows(ProcessingException.class, () -> parser.parse(callback));
    }

    /**
     * Parser that throws XMLStreamException from onEvent covers the XMLStreamException catch in parse().
     */
    @Test
    void parseThrowsProcessingExceptionOnXmlStreamError() throws IOException {
        final var sourceLoader = Mockito.mock(SourceLoader.class);
        final var callback = Mockito.mock(SourceCallback.class);

        final var parser = new AbstractXmlEventParser(TestIoUtil.getFile("jacoco1.xml"), sourceLoader) {
            @Override
            protected void onEvent(final XMLStreamReader xml, final SourceCallback cb)
                    throws XMLStreamException, ProcessingException, IOException {
                throw new XMLStreamException("Simulated XMLStreamException");
            }
        };

        Assertions.assertThrows(ProcessingException.class, () -> parser.parse(callback));
    }

    /**
     * Parse with non-existent file throws IOException (xml remains null, close(null) is called).
     */
    @Test
    void parseNonExistentFileThrowsIoException() {
        final var sourceLoader = Mockito.mock(SourceLoader.class);
        final var callback = Mockito.mock(SourceCallback.class);

        final var nonExistentFile = new File("/non/existent/path/coverage.xml");
        final var parser = new AbstractXmlEventParser(nonExistentFile, sourceLoader) {
            @Override
            protected void onEvent(final XMLStreamReader xml, final SourceCallback cb)
                    throws XMLStreamException, ProcessingException, IOException {
                // never called
            }
        };

        Assertions.assertThrows(IOException.class, () -> parser.parse(callback));
    }

}
