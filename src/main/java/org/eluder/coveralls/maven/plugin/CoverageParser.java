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
package org.eluder.coveralls.maven.plugin;

import java.io.File;
import java.io.IOException;

import org.eluder.coveralls.maven.plugin.source.SourceCallback;

/**
 * Handles parsing of a coverage report. The implementation can be stateful, and the same instance should be used only
 * one time to parse a coverage report. Completed source files are passed to the
 * {@link org.eluder.coveralls.maven.plugin.source.SourceCallback} handler. To maximize performance, the parser should
 * use streaming.
 */
public interface CoverageParser {

    /**
     * Parses a coverage report. Parsed source files are passed to the callback handler. This method should be called
     * only once per instance.
     *
     * @param callback
     *            the source callback handler
     *
     * @throws ProcessingException
     *             if processing of the coverage report fails
     * @throws IOException
     *             if an I/O error occurs
     */
    void parse(SourceCallback callback) throws ProcessingException, IOException;

    /**
     * Gets the coverage file.
     *
     * @return the coverage report file under processing
     */
    File getCoverageFile();
}
