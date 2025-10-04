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
 * The Class JaCoCoParser.
 */
public class JaCoCoParser extends AbstractXmlEventParser {

    /** The package name. */
    private String packageName;

    /** The source. */
    private Source source;

    /** The branch id. */
    private int branchId;

    /**
     * Instantiates a new jacoco parser.
     *
     * @param coverageFile
     *            the coverage file
     * @param sourceLoader
     *            the source loader
     */
    public JaCoCoParser(final File coverageFile, final SourceLoader sourceLoader) {
        super(coverageFile, sourceLoader);
    }

    @Override
    protected void onEvent(final XMLStreamReader xml, final SourceCallback callback)
            throws XMLStreamException, ProcessingException, IOException {
        if (this.isStartElement(xml, "package")) {
            this.packageName = xml.getAttributeValue(null, "name");
        } else if (this.isStartElement(xml, "sourcefile") && this.packageName != null) {
            final var sourceFile = this.packageName + "/" + xml.getAttributeValue(null, "name");
            this.source = this.loadSource(sourceFile);
            this.branchId = 0;
        } else if (this.isStartElement(xml, "line") && this.source != null) {
            final var ci = Integer.parseInt(xml.getAttributeValue(null, "ci"));
            final var cb = Integer.parseInt(xml.getAttributeValue(null, "cb"));
            final var mb = Integer.parseInt(xml.getAttributeValue(null, "mb"));
            final var nr = Integer.parseInt(xml.getAttributeValue(null, "nr"));

            // jacoco does not count hits. this is why hits is always 0 or 1
            this.source.addCoverage(nr, ci == 0 ? 0 : 1);

            // add branches. unfortunately, there is NO block number and
            // branch number will NOT be unique between coverage changes.
            for (var b = 0; b < cb; b++) {
                this.source.addBranchCoverage(nr, 0, this.branchId++, 1);
            }
            for (var b = 0; b < mb; b++) {
                this.source.addBranchCoverage(nr, 0, this.branchId++, 0);
            }
        } else if (this.isEndElement(xml, "sourcefile") && this.source != null) {
            callback.onSource(this.source);
            this.source = null;
        } else if (this.isEndElement(xml, "package")) {
            this.packageName = null;
        }
    }

}
