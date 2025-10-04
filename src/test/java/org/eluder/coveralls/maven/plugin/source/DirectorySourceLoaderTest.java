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
package org.eluder.coveralls.maven.plugin.source;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

/**
 * The Class DirectorySourceLoaderTest.
 */
class DirectorySourceLoaderTest {

    /** The folder. */
    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    Path folder;

    /**
     * Missing source file from directory.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void missingSourceFileFromDirectory() throws IOException {
        final var sourceLoader = new DirectorySourceLoader(this.folder.toFile(), this.folder.toFile(),
                StandardCharsets.UTF_8);
        Assertions.assertNull(sourceLoader.load("Foo.java"));
    }

    /**
     * Invalid source file.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void invalidSourceFile() throws IOException {
        final var subFolder = Files.createDirectory(this.folder.resolve("subFolder")).toFile().getName();
        final var sourceLoader = new DirectorySourceLoader(this.folder.toFile(), this.folder.toFile(),
                StandardCharsets.UTF_8);
        Assertions.assertThrows(IllegalArgumentException.class, () -> sourceLoader.load(subFolder));
    }

    /**
     * Load source.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void loadSource() throws IOException {
        final var file = Files.createFile(this.folder.resolve("newFile")).toFile();
        TestIoUtil.writeFileContent("public class Foo {\r\n    \n}\r", file);
        final var sourceLoader = new DirectorySourceLoader(this.folder.toFile(), this.folder.toFile(),
                StandardCharsets.UTF_8);
        final var source = sourceLoader.load(file.getName());
        Assertions.assertEquals(file.getName(), source.getName());
        Assertions.assertEquals(
                "27F0B29785725F4946DBD05F7963E507B8DB735C2803BBB80C93ECB02291B2E2F9B03CBF27526DB68B6A862F1C6541275CD413A1CCD3E07209B9CAE0C04163C6",
                source.getDigest());
        Assertions.assertEquals(4, source.getCoverage().length);
    }

}
