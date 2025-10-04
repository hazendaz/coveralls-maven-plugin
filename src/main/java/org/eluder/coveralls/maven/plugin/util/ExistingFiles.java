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
package org.eluder.coveralls.maven.plugin.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.eluder.coveralls.maven.plugin.CoverageParser;

/**
 * The Class ExistingFiles.
 */
public class ExistingFiles implements Iterable<File> {

    /** The delegate. */
    private final List<File> delegate = new ArrayList<>();

    /**
     * Adds the all.
     *
     * @param files
     *            the files
     *
     * @return the existing files
     */
    public ExistingFiles addAll(final Iterable<File> files) {
        if (files == null) {
            throw new NullPointerException("Files must be defined");
        }
        for (final File file : files) {
            this.add(file);
        }
        return this;
    }

    /**
     * Adds the.
     *
     * @param file
     *            the file
     *
     * @return the existing files
     */
    public ExistingFiles add(final File file) {
        if (file == null) {
            throw new NullPointerException("File must be defined");
        }
        if (file.exists() && file.isFile() && !this.delegate.contains(file)) {
            this.delegate.add(file);
        }
        return this;
    }

    @Override
    public Iterator<File> iterator() {
        return this.delegate.iterator();
    }

    /**
     * Creates the.
     *
     * @param files
     *            the files
     *
     * @return the existing files
     */
    public static ExistingFiles create(final Iterable<File> files) {
        final var existingFiles = new ExistingFiles();
        if (files != null) {
            existingFiles.addAll(files);
        }
        return existingFiles;
    }

    /**
     * Converts existing files to a list of CoverageParser using the provided function.
     *
     * @param parserFunction
     *            function to convert File to CoverageParser
     *
     * @return list of CoverageParser for valid files
     */
    public List<CoverageParser> toParsers(Function<File, CoverageParser> parserFunction) {
        final List<CoverageParser> parsers = new ArrayList<>();
        for (final File file : this.delegate) {
            parsers.add(parserFunction.apply(file));
        }
        return parsers;
    }
}
