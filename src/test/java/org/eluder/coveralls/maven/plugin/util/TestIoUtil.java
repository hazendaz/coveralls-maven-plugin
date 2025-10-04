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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * The Class TestIoUtil.
 */
public final class TestIoUtil {

    /**
     * Write file content.
     *
     * @param content
     *            the content
     * @param file
     *            the file
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeFileContent(final String content, final File file) throws IOException {
        try (var writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

    /**
     * Read file content.
     *
     * @param file
     *            the file
     *
     * @return the string
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static String readFileContent(final File file) throws IOException {
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    /**
     * Gets the file.
     *
     * @param resource
     *            the resource
     *
     * @return the file
     */
    public static File getFile(final String resource) {
        try {
            var local = resource;
            if (local.lastIndexOf("/") > 0) {
                local = local.substring(local.lastIndexOf('/'));
            }
            if (!local.startsWith("/")) {
                local = "/" + local;
            }
            return Path.of(TestIoUtil.getResourceUrl(local).toURI()).toFile();
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Gets the sha 512 digest hex.
     *
     * @param content
     *            the content
     *
     * @return the sha 512 digest hex
     */
    public static String getSha512DigestHex(final String content) {
        return DigestUtils.sha512Hex(content).toUpperCase(Locale.ENGLISH);
    }

    /**
     * Gets the resource url.
     *
     * @param resource
     *            the resource
     *
     * @return the resource url
     */
    private static URL getResourceUrl(final String resource) {
        return TestIoUtil.class.getResource(resource);
    }

    /**
     * Instantiates a new test io util.
     */
    private TestIoUtil() {
        // Do Nothing
    }

}
