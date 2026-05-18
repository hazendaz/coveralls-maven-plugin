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
package org.eluder.coveralls.maven.plugin.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eluder.coveralls.maven.plugin.CoverageParser;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;

/**
 * The Class AbstractXmlEventParser.
 */
public abstract class AbstractXmlEventParser implements CoverageParser {

    /** The coverage file. */
    private final File coverageFile;

    /** The source loader. */
    private final SourceLoader sourceLoader;

    /**
     * Instantiates a new abstract xml event parser.
     *
     * @param coverageFile
     *            the coverage file
     * @param sourceLoader
     *            the source loader
     */
    protected AbstractXmlEventParser(final File coverageFile, final SourceLoader sourceLoader) {
        this.coverageFile = coverageFile;
        this.sourceLoader = sourceLoader;
    }

    @Override
    public final void parse(final SourceCallback callback) throws ProcessingException, IOException {
        XMLStreamReader xml = null;
        try (var is = Files.newInputStream(this.coverageFile.toPath());
                var bis = new BufferedInputStream(is)) {
            xml = this.createEventReader(bis);
            while (xml.hasNext()) {
                xml.next();
                this.onEvent(xml, callback);
            }
        } catch (final XMLStreamException e) {
            throw new ProcessingException(e);
        } finally {
            this.close(xml);
        }
    }

    @Override
    public final File getCoverageFile() {
        return this.coverageFile;
    }

    /**
     * Creates the event reader.
     *
     * @param inputStream
     *            the input stream
     *
     * @return the XML stream reader
     *
     * @throws ProcessingException
     *             the processing exception
     */
    protected XMLStreamReader createEventReader(final InputStream inputStream) throws ProcessingException {
        try {
            final var xmlif = XMLInputFactory.newInstance();
            xmlif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            xmlif.setProperty(XMLInputFactory.IS_VALIDATING, false);
            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            return xmlif.createXMLStreamReader(inputStream);
        } catch (final FactoryConfigurationError e) {
            throw new IllegalArgumentException(e);
        } catch (final XMLStreamException e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * Close.
     *
     * @param xml
     *            the xml
     *
     * @throws ProcessingException
     *             the processing exception
     */
    private void close(final XMLStreamReader xml) throws ProcessingException {
        if (xml != null) {
            try {
                xml.close();
            } catch (final XMLStreamException e) {
                throw new ProcessingException(e);
            }
        }
    }

    /**
     * On event.
     *
     * @param xml
     *            the xml
     * @param callback
     *            the callback
     *
     * @throws XMLStreamException
     *             the XML stream exception
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected abstract void onEvent(final XMLStreamReader xml, SourceCallback callback)
            throws XMLStreamException, ProcessingException, IOException;

    /**
     * Load source.
     *
     * @param sourceFile
     *            the source file
     *
     * @return the source
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected final Source loadSource(final String sourceFile) throws IOException {
        return this.sourceLoader.load(sourceFile);
    }

    /**
     * Checks if is start element.
     *
     * @param xml
     *            the xml
     * @param name
     *            the name
     *
     * @return true, if is start element
     */
    protected final boolean isStartElement(final XMLStreamReader xml, final String name) {
        return XMLStreamConstants.START_ELEMENT == xml.getEventType() && xml.getLocalName().equals(name);
    }

    /**
     * Checks if is end element.
     *
     * @param xml
     *            the xml
     * @param name
     *            the name
     *
     * @return true, if is end element
     */
    protected final boolean isEndElement(final XMLStreamReader xml, final String name) {
        return XMLStreamConstants.END_ELEMENT == xml.getEventType() && xml.getLocalName().equals(name);
    }
}
