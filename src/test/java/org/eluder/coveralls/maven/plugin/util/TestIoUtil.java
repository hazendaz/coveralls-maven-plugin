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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public final class TestIoUtil {

    public static void writeFileContent(final String content, final File file) throws IOException {
        try (var writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

    public static String readFileContent(final File file) throws IOException {
        try (var reader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)) {
            return IOUtils.toString(reader);
        }
    }

    public static File getFile(final String resource) {
        try {
            var local = resource;
            if (local.lastIndexOf("/") > 0) {
                local = local.substring(local.lastIndexOf('/'));
            }
            if (!local.startsWith("/")) {
                local = "/" + local;
            }
            return Path.of(getResourceUrl(local).toURI()).toFile();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static String getSha512DigestHex(final String content) {
        return DigestUtils.sha512Hex(content).toUpperCase(Locale.ENGLISH);
    }

    private static URL getResourceUrl(final String resource) {
        return TestIoUtil.class.getResource(resource);
    }

    private TestIoUtil() {
        // Do Nothing
    }

}
