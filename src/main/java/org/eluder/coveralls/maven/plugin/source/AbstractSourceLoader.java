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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.util.Sha512DigestInputStream;

/**
 * The Class AbstractSourceLoader.
 */
public abstract class AbstractSourceLoader implements SourceLoader {

    /** The source encoding. */
    private final Charset sourceEncoding;

    /** The directory prefix. */
    private final String directoryPrefix;

    /**
     * Instantiates a new abstract source loader.
     *
     * @param base
     *            the base
     * @param sourceBase
     *            the source base
     * @param sourceEncoding
     *            the source encoding
     */
    protected AbstractSourceLoader(final URI base, final URI sourceBase, final String sourceEncoding) {
        this.sourceEncoding = Charset.forName(sourceEncoding);
        this.directoryPrefix = base.relativize(sourceBase).toString();
    }

    @Override
    public Source load(final String sourceFile) throws IOException {
        InputStream stream = locate(sourceFile);
        if (stream == null) {
            return null;
        }
        try (Sha512DigestInputStream ds = new Sha512DigestInputStream(stream);
                InputStreamReader reader = new InputStreamReader(ds, getSourceEncoding())) {
            String source = IOUtils.toString(reader);
            return new Source(getFileName(sourceFile), source, ds.getDigestHex());
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("Sha-512 algorithm not available", ex);
        }
    }

    /**
     * Gets the source encoding.
     *
     * @return the source encoding
     */
    protected Charset getSourceEncoding() {
        return sourceEncoding;
    }

    /**
     * Gets the file name.
     *
     * @param sourceFile
     *            the source file
     *
     * @return the file name
     */
    protected String getFileName(final String sourceFile) {
        return directoryPrefix + sourceFile;
    }

    /**
     * Locate.
     *
     * @param sourceFile
     *            the source file
     *
     * @return the input stream
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected abstract InputStream locate(String sourceFile) throws IOException;
}
