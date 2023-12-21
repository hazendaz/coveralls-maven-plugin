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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

class DirectorySourceLoaderTest {

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    public Path folder;

    @Test
    void testMissingSourceFileFromDirectory() throws IOException {
        DirectorySourceLoader sourceLoader = new DirectorySourceLoader(folder.toFile(), folder.toFile(), "UTF-8");
        assertNull(sourceLoader.load("Foo.java"));
    }

    @Test
    void testInvalidSourceFile() throws IOException {
        String subFolder = Files.createDirectory(folder.resolve("subFolder")).toFile().getName();
        DirectorySourceLoader sourceLoader = new DirectorySourceLoader(folder.toFile(), folder.toFile(), "UTF-8");
        assertThrows(IllegalArgumentException.class, () -> {
            sourceLoader.load(subFolder);
        });
    }

    @Test
    void testLoadSource() throws IOException {
        File file = Files.createFile(folder.resolve("newFile")).toFile(); 
        TestIoUtil.writeFileContent("public class Foo {\r\n    \n}\r", file);
        DirectorySourceLoader sourceLoader = new DirectorySourceLoader(folder.toFile(), folder.toFile(), "UTF-8");
        Source source = sourceLoader.load(file.getName());
        assertEquals(file.getName(), source.getName());
        assertEquals("27F0B29785725F4946DBD05F7963E507B8DB735C2803BBB80C93ECB02291B2E2F9B03CBF27526DB68B6A862F1C6541275CD413A1CCD3E07209B9CAE0C04163C6", source.getDigest());
        assertEquals(4, source.getCoverage().length);
    }
}
