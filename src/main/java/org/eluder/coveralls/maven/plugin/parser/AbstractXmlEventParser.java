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
package org.eluder.coveralls.maven.plugin.parser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.input.XmlStreamReader;
import org.eluder.coveralls.maven.plugin.CoverageParser;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;

public abstract class AbstractXmlEventParser implements CoverageParser {

    private final File coverageFile;
    private final SourceLoader sourceLoader;

    protected AbstractXmlEventParser(final File coverageFile, final SourceLoader sourceLoader) {
        this.coverageFile = coverageFile;
        this.sourceLoader = sourceLoader;
    }

    @Override
    public final void parse(final SourceCallback callback) throws ProcessingException, IOException {
        XMLStreamReader xml = null;
        try (XmlStreamReader reader = XmlStreamReader.builder().setPath(coverageFile.getPath())
                .setCharset(StandardCharsets.UTF_8).get()) {
            xml = createEventReader(reader);
            while (xml.hasNext()) {
                xml.next();
                onEvent(xml, callback);
            }
        } catch (XMLStreamException ex) {
            throw new ProcessingException(ex);
        } finally {
            close(xml);
        }
    }

    @Override
    public final File getCoverageFile() {
        return coverageFile;
    }

    protected XMLStreamReader createEventReader(final Reader reader) throws ProcessingException {
        try {
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            xmlif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            xmlif.setProperty(XMLInputFactory.IS_VALIDATING, false);
            return xmlif.createXMLStreamReader(reader);
        } catch (FactoryConfigurationError ex) {
            throw new IllegalArgumentException(ex);
        } catch (XMLStreamException ex) {
            throw new ProcessingException(ex);
        }
    }

    private void close(final XMLStreamReader xml) throws ProcessingException {
        if (xml != null) {
            try {
                xml.close();
            } catch (XMLStreamException ex) {
                throw new ProcessingException(ex);
            }
        }
    }

    protected abstract void onEvent(final XMLStreamReader xml, SourceCallback callback)
            throws XMLStreamException, ProcessingException, IOException;

    protected final Source loadSource(final String sourceFile) throws IOException {
        return sourceLoader.load(sourceFile);
    }

    protected final boolean isStartElement(final XMLStreamReader xml, final String name) {
        return XMLStreamConstants.START_ELEMENT == xml.getEventType() && xml.getLocalName().equals(name);
    }

    protected final boolean isEndElement(final XMLStreamReader xml, final String name) {
        return XMLStreamConstants.END_ELEMENT == xml.getEventType() && xml.getLocalName().equals(name);
    }
}
