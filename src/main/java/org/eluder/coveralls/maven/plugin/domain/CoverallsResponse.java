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
package org.eluder.coveralls.maven.plugin.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class CoverallsResponse.
 */
public final class CoverallsResponse implements JsonObject {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The message. */
    private final String message;

    /** The error. */
    private final boolean error;

    /** The url. */
    private final String url;

    /**
     * Instantiates a new coveralls response.
     *
     * @param message
     *            the message
     * @param error
     *            the error
     * @param url
     *            the url
     */
    @JsonCreator
    public CoverallsResponse(@JsonProperty("message") final String message, @JsonProperty("error") final boolean error,
            @JsonProperty("url") final String url) {
        this.message = message;
        this.error = error;
        this.url = url;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Checks if is error.
     *
     * @return true, if is error
     */
    public boolean isError() {
        return this.error;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }
}
