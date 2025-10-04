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
package org.eluder.coveralls.maven.plugin.logging;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.eluder.coveralls.maven.plugin.logging.Logger.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class DryRunLoggerTest.
 */
@ExtendWith(MockitoExtension.class)
class DryRunLoggerTest {

    /** The log mock. */
    @Mock
    Log logMock;

    /** The coveralls file mock. */
    @Mock
    File coverallsFileMock;

    /**
     * Missing coveralls file.
     */
    @Test
    void missingCoverallsFile() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DryRunLogger(true, null);
        });
    }

    /**
     * Test get position.
     */
    @Test
    void position() {
        Assertions.assertEquals(Position.AFTER, new DryRunLogger(true, this.coverallsFileMock).getPosition());
    }

    /**
     * Log dry run disabled.
     */
    @Test
    void logDryRunDisabled() {
        new DryRunLogger(false, this.coverallsFileMock).log(this.logMock);

        Mockito.verifyNoInteractions(this.logMock);
    }

    /**
     * Log dry run enabled.
     */
    @Test
    void logDryRunEnabled() {
        Mockito.when(this.coverallsFileMock.length()).thenReturn(1024L);
        Mockito.when(this.coverallsFileMock.getAbsolutePath()).thenReturn("/target/coveralls.json");

        new DryRunLogger(true, this.coverallsFileMock).log(this.logMock);

        Mockito.verify(this.logMock).info("Dry run enabled, Coveralls report will NOT be submitted to API");
        Mockito.verify(this.logMock).info("1024 bytes of data was recorded in /target/coveralls.json");
    }

}
