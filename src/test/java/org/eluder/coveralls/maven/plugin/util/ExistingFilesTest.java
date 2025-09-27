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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

/**
 * The Class ExistingFilesTest.
 */
class ExistingFilesTest {

    /** The folder. */
    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    public Path folder;

    /**
     * Adds the all for null.
     */
    @Test
    void addAllForNull() {
        var existingFiles = new ExistingFiles();
        assertThrows(NullPointerException.class, () -> {
            existingFiles.addAll(null);
        });
    }

    /**
     * Adds the for null.
     */
    @Test
    void addForNull() {
        var existingFiles = new ExistingFiles();
        assertThrows(NullPointerException.class, () -> {
            existingFiles.add(null);
        });
    }

    /**
     * Adds the for existing.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void addForExisting() throws IOException {
        var f = Files.createFile(folder.resolve("f")).toFile();
        var iter = new ExistingFiles().add(f).add(f).iterator();
        assertSize(iter, 1);
    }

    /**
     * Adds the for directory.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void addForDirectory() throws IOException {
        var d = Files.createDirectory(folder.resolve("d")).toFile();
        var iter = new ExistingFiles().add(d).iterator();
        assertSize(iter, 0);
    }

    /**
     * Creates the for null.
     */
    @Test
    void createForNull() {
        var iter = ExistingFiles.create(null).iterator();
        assertSize(iter, 0);
    }

    /**
     * Creates the for multiple files.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createForMultipleFiles() throws IOException {
        var f1 = Files.createFile(folder.resolve("f1")).toFile();
        var f2 = Files.createFile(folder.resolve("f2")).toFile();
        var iter = ExistingFiles.create(Arrays.asList(f1, f2)).iterator();
        assertSize(iter, 2);
    }

    /**
     * Assert size.
     *
     * @param iter
     *            the iter
     * @param size
     *            the size
     */
    private static void assertSize(Iterator<?> iter, int size) {
        for (var i = 0; i < size; i++) {
            iter.next();
        }
        assertFalse(iter.hasNext());
    }
}
