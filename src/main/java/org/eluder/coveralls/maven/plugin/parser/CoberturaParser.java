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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;

public class CoberturaParser extends AbstractXmlEventParser {

    protected Source source;
    protected boolean inMethods;
    private int branchId;

    public CoberturaParser(final File coverageFile, final SourceLoader sourceLoader) {
        super(coverageFile, sourceLoader);
    }

    @Override
    protected void onEvent(final XMLStreamReader xml, final SourceCallback callback)
            throws XMLStreamException, ProcessingException, IOException {
        if (isStartElement(xml, "class")) {
            source = loadSource(xml.getAttributeValue(null, "filename"));
            String className = xml.getAttributeValue(null, "name");
            int classifierPosition = className.indexOf('$');
            if (classifierPosition > 0) {
                source.setClassifier(className.substring(classifierPosition + 1));
            }
            this.branchId = 0;
        } else

        if (isStartElement(xml, "methods") && source != null) {
            inMethods = true;
        } else

        if (isEndElement(xml, "methods") && source != null) {
            inMethods = false;
        } else

        if (isStartElement(xml, "line") && !inMethods && source != null) {
            final int nr = Integer.parseInt(xml.getAttributeValue(null, "number"));
            source.addCoverage(nr, Integer.valueOf(xml.getAttributeValue(null, "hits")));
            if (Boolean.parseBoolean(xml.getAttributeValue(null, "branch"))) {
                final String value = xml.getAttributeValue(null, "condition-coverage");

                // Is "condition-coverage" attribute always here?
                if (value == null) {
                    return;
                }

                // C'mon Cobertura, human readable format for XML ?
                final String[] values = value // 50% (2/4)
                        .replace(" ", "") // 50%(2/4)
                        .replace("%", "/") // 50/(2/4)
                        .replace("(", "") // 50/2/4)
                        .replace(")", "") // 50/2/4
                        .split("/", -1);

                final int cb = Integer.parseInt(values[1]);
                final int tb = Integer.parseInt(values[2]);
                final int mb = tb - cb;

                // add branches. unfortunately, there is NO block number and
                // branch number will NOT be unique between coverage changes.
                for (int b = 0; b < cb; b++) {
                    this.source.addBranchCoverage(nr, 0, this.branchId++, 1);
                }
                for (int b = 0; b < mb; b++) {
                    this.source.addBranchCoverage(nr, 0, this.branchId++, 0);
                }
            }
        } else

        if (isEndElement(xml, "class") && source != null) {
            callback.onSource(source);
            source = null;
        }
    }

}
