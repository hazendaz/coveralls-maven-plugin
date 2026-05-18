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

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;

/**
 * The Class CloverParser.
 */
public class CloverParser extends AbstractXmlEventParser {

    /** The source. */
    private Source source;

    /** The package name. */
    private String packageName;

    /**
     * Instantiates a new clover parser.
     *
     * @param coverageFile
     *            the coverage file
     * @param sourceLoader
     *            the source loader
     */
    public CloverParser(final File coverageFile, final SourceLoader sourceLoader) {
        super(coverageFile, sourceLoader);
    }

    @Override
    protected void onEvent(final XMLStreamReader xml, final SourceCallback callback)
            throws XMLStreamException, ProcessingException, IOException {
        if (this.isStartElement(xml, "package")) {
            this.packageName = xml.getAttributeValue(null, "name");
        } else if (this.isStartElement(xml, "file") && this.packageName != null) {
            final var sourceFile = this.getSourceFile(xml.getAttributeValue(null, "name"));
            this.source = this.loadSource(sourceFile);
        } else if (this.isStartElement(xml, "line") && this.source != null) {
            // lines can be "method", "stmt", or "cond"
            final var type = xml.getAttributeValue(null, "type");
            var coverage = 0;
            if ("method".equals(type) || "stmt".equals(type)) {
                coverage = Integer.parseInt(xml.getAttributeValue(null, "count")) == 0 ? 0 : 1;
            } else if ("cond".equals(type)) {
                final var falseCount = Integer.parseInt(xml.getAttributeValue(null, "falsecount"));
                final var trueCount = Integer.parseInt(xml.getAttributeValue(null, "truecount"));
                coverage = trueCount == 0 || falseCount == 0 ? 0 : 1;
            }
            final var lineNumber = Integer.parseInt(xml.getAttributeValue(null, "num"));
            this.source.addCoverage(lineNumber, coverage);
        } else if (this.isEndElement(xml, "file") && this.source != null) {
            callback.onSource(this.source);
            this.source = null;
        } else if (this.isEndElement(xml, "package")) {
            this.packageName = null;
        }
    }

    /**
     * Gets the source file.
     *
     * @param fileName
     *            the file name
     *
     * @return the source file
     */
    private String getSourceFile(final String fileName) {
        return this.packageName.replace('.', '/') + "/" + fileName;
    }
}
