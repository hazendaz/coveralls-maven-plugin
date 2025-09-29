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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class Sha521DigestInputStreamTest.
 */
class Sha521DigestInputStreamTest {

    /**
     * Test read.
     *
     * @throws NoSuchAlgorithmException
     *             the no such algorithm exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testRead() throws NoSuchAlgorithmException, IOException {
        final byte[] data = { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD };
        try (var is = new Sha512DigestInputStream(new ByteArrayInputStream(data))) {
            Assertions.assertEquals(0xAA, is.read());
            Assertions.assertEquals(0xBB, is.read());
            Assertions.assertEquals(0xCC, is.read());
            Assertions.assertEquals(0xDD, is.read());
            Assertions.assertEquals(-1, is.read());
            Assertions.assertEquals(
                    "48E218B30D4EA16305096FE35E84002A0D262EB3853131309423492228980C60238F9EED238285036F22E37C4662E40C80A461000A7AA9A03FB3CB6E4223E83B",
                    is.getDigestHex());
        }
    }

    /**
     * Read array.
     *
     * @throws NoSuchAlgorithmException
     *             the no such algorithm exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void readArray() throws NoSuchAlgorithmException, IOException {
        final byte[] data = { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD };
        try (var is = new Sha512DigestInputStream(new ByteArrayInputStream(data))) {
            final var buff = new byte[5];
            Assertions.assertEquals(4, is.read(buff));
            Assertions.assertEquals(-1, is.read());
            for (var i = 0; i < data.length; i++) {
                Assertions.assertEquals(data[i], buff[i]);
            }
            Assertions.assertEquals(
                    "48E218B30D4EA16305096FE35E84002A0D262EB3853131309423492228980C60238F9EED238285036F22E37C4662E40C80A461000A7AA9A03FB3CB6E4223E83B",
                    is.getDigestHex());
        }
    }

}
