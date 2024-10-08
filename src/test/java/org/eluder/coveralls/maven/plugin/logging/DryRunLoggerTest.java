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
package org.eluder.coveralls.maven.plugin.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.eluder.coveralls.maven.plugin.logging.Logger.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DryRunLoggerTest {

    @Mock
    Log logMock;

    @Mock
    File coverallsFileMock;

    @Test
    void missingCoverallsFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DryRunLogger(true, null);
        });
    }

    @Test
    void testGetPosition() {
        assertEquals(Position.AFTER, new DryRunLogger(true, coverallsFileMock).getPosition());
    }

    @Test
    void logDryRunDisabled() {
        new DryRunLogger(false, coverallsFileMock).log(logMock);

        verifyNoInteractions(logMock);
    }

    @Test
    void logDryRunEnabled() {
        when(coverallsFileMock.length()).thenReturn(1024L);
        when(coverallsFileMock.getAbsolutePath()).thenReturn("/target/coveralls.json");

        new DryRunLogger(true, coverallsFileMock).log(logMock);

        verify(logMock).info("Dry run enabled, Coveralls report will NOT be submitted to API");
        verify(logMock).info("1024 bytes of data was recorded in /target/coveralls.json");
    }
}
