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
package org.eluder.coveralls.maven.plugin.util;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
 * The Class Sha512DigestInputStream.
 */
public class Sha512DigestInputStream extends DigestInputStream {

    /**
     * Instantiates a new sha 512 digest input stream.
     *
     * @param stream
     *            the stream
     */
    public Sha512DigestInputStream(final InputStream stream) {
        super(stream, Sha512DigestInputStream.getSha512Digest());
    }

    private static MessageDigest getSha512Digest() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (final NoSuchAlgorithmException e) {
            // SHA-512 is guaranteed to be available in all Java SE implementations
            throw new AssertionError("SHA-512 algorithm not available", e);
        }
    }

    /**
     * Gets the digest hex.
     * <p>
     * <b>Note:</b> Calling this method will finalize and reset the digest. Subsequent calls will return the digest of
     * an empty stream unless the stream is re-read. This is standard behavior for {@link MessageDigest#digest()}.
     *
     * @return the digest hex
     */
    public String getDigestHex() {
        return Hex.encodeHexString(getMessageDigest().digest(), false);
    }
}
