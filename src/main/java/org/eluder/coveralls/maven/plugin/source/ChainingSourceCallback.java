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

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Source;

/**
 * Source callback handler that allows chaining multiple callback handlers. Chained callback handler is executed after
 * this callback.
 */
public abstract class ChainingSourceCallback implements SourceCallback {

    /** The chained. */
    private final SourceCallback chained;

    /**
     * Instantiates a new chaining source callback.
     *
     * @param chained
     *            the chained
     */
    protected ChainingSourceCallback(final SourceCallback chained) {
        if (chained == null) {
            throw new IllegalArgumentException("chained must be defined");
        }
        this.chained = chained;
    }

    @Override
    public final void onBegin() throws ProcessingException, IOException {
        this.onBeginInternal();
        this.chained.onBegin();
    }

    @Override
    public final void onSource(final Source source) throws ProcessingException, IOException {
        this.onSourceInternal(source);
        this.chained.onSource(source);
    }

    @Override
    public final void onComplete() throws ProcessingException, IOException {
        this.onCompleteInternal();
        this.chained.onComplete();
    }

    /**
     * Defaults to no-op implementation.
     *
     * @throws ProcessingException
     *             if processing fails
     * @throws IOException
     *             if an I/O error occurs
     *
     * @see #onBegin()
     */
    protected void onBeginInternal() throws ProcessingException, IOException {

    }

    /**
     * On source internal.
     *
     * @param source
     *            the source file
     *
     * @throws ProcessingException
     *             if further processing of the source fails
     * @throws IOException
     *             if an I/O error occurs
     *
     * @see #onSource(Source)
     */
    protected abstract void onSourceInternal(final Source source) throws ProcessingException, IOException;

    /**
     * Defaults to no-op implementation.
     *
     * @throws ProcessingException
     *             if processing fails
     * @throws IOException
     *             if an I/O error occurs
     *
     * @see #onComplete()
     */
    protected void onCompleteInternal() throws ProcessingException, IOException {

    }
}
