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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * The Class ValidationExceptionTest.
 */
class ValidationExceptionTest {

    /** The Constant MESSAGE. */
    private static final String MESSAGE = "message";

    /**
     * Exception.
     */
    @Test
    void exception() {
        var exception = new ValidationException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Exception with message.
     */
    @Test
    void exceptionWithMessage() {
        var exception = new ValidationException(MESSAGE);
        assertEquals(MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Exception with cause.
     */
    @Test
    void exceptionWithCause() {
        var cause = new RuntimeException("cause message");
        var exception = new ValidationException(cause);
        assertEquals(cause.toString(), exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    /**
     * Exception with message and cause.
     */
    @Test
    void exceptionWithMessageAndCause() {
        var cause = new RuntimeException("cause message");
        var exception = new ValidationException(MESSAGE, cause);
        assertEquals(MESSAGE, exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
