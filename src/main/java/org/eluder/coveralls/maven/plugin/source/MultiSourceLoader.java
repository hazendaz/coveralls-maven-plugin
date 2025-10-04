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
package org.eluder.coveralls.maven.plugin.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eluder.coveralls.maven.plugin.domain.Source;

/**
 * The Class MultiSourceLoader.
 */
public class MultiSourceLoader implements SourceLoader {

    /** The source loaders. */
    private final List<SourceLoader> sourceLoaders = new ArrayList<>();

    /**
     * Instantiates a new multi source loader.
     */
    public MultiSourceLoader() {
        // do nothing
    }

    /**
     * Adds the.
     *
     * @param sourceLoader
     *            the source loader
     *
     * @return the multi source loader
     */
    public MultiSourceLoader add(final SourceLoader sourceLoader) {
        this.sourceLoaders.add(sourceLoader);
        return this;
    }

    @Override
    public Source load(final String sourceFile) throws IOException {
        for (final SourceLoader sourceLoader : this.sourceLoaders) {
            final var source = sourceLoader.load(sourceFile);
            if (source != null) {
                return source;
            }
        }
        throw new IOException("No source found for " + sourceFile);
    }
}
