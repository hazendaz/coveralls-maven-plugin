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
package org.eluder.coveralls.maven.plugin.validation;

/**
 * The Class ValidationError.
 */
public final class ValidationError {

    /**
     * The Enum Level.
     */
    public enum Level {
        /** The warn. */
        WARN,
        /** The error. */
        ERROR
    }

    /** The level. */
    private final Level level;

    /** The message. */
    private final String message;

    /**
     * Instantiates a new validation error.
     *
     * @param level
     *            the level
     * @param message
     *            the message
     */
    public ValidationError(final Level level, final String message) {
        if (level == null) {
            throw new IllegalArgumentException("level must be defined");
        }
        if (message == null) {
            throw new IllegalArgumentException("message must be defined");
        }
        this.level = level;
        this.message = message;
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    public Level getLevel() {
        return this.level;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.level + ": " + this.message;
    }
}
