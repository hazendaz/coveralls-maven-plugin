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
package org.eluder.coveralls.maven.plugin.source;

import java.io.IOException;

import org.eluder.coveralls.maven.plugin.domain.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class MultiSourceLoaderTest.
 */
@ExtendWith(MockitoExtension.class)
class MultiSourceLoaderTest {

    /** The sl 1. */
    @Mock
    SourceLoader sl1;

    /** The sl 2. */
    @Mock
    SourceLoader sl2;

    /** The s 1. */
    Source s1 = new Source("source", "{ 1 }", "9A0174D3A5B8D202C6E404601FC260D1");

    /** The s 2. */
    Source s2 = new Source("source", "{ 2 }", "849409F24F4BCAAC904F3B142447D65D");

    /**
     * Missing source.
     */
    @Test
    void missingSource() {
        Assertions.assertThrows(IOException.class, () -> {
            this.creaMultiSourceLoader().load("source");
        });
    }

    /**
     * Primary source.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void primarySource() throws IOException {
        Mockito.when(this.sl1.load("source")).thenReturn(this.s1);
        final var source = this.creaMultiSourceLoader().load("source");
        Assertions.assertSame(this.s1, source);
    }

    /**
     * Secondary source.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void secondarySource() throws IOException {
        Mockito.when(this.sl2.load("source")).thenReturn(this.s2);
        final var source = this.creaMultiSourceLoader().load("source");
        Assertions.assertSame(this.s2, source);
    }

    /**
     * Crea multi source loader.
     *
     * @return the multi source loader
     */
    MultiSourceLoader creaMultiSourceLoader() {
        return new MultiSourceLoader().add(this.sl1).add(this.sl2);
    }
}
