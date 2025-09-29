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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
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
        final var existingFiles = new ExistingFiles();
        Assertions.assertThrows(NullPointerException.class, () -> {
            existingFiles.addAll(null);
        });
    }

    /**
     * Adds the for null.
     */
    @Test
    void addForNull() {
        final var existingFiles = new ExistingFiles();
        Assertions.assertThrows(NullPointerException.class, () -> {
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
        final var f = Files.createFile(this.folder.resolve("f")).toFile();
        final var iter = new ExistingFiles().add(f).add(f).iterator();
        ExistingFilesTest.assertSize(iter, 1);
    }

    /**
     * Adds the for directory.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void addForDirectory() throws IOException {
        final var d = Files.createDirectory(this.folder.resolve("d")).toFile();
        final var iter = new ExistingFiles().add(d).iterator();
        ExistingFilesTest.assertSize(iter, 0);
    }

    /**
     * Creates the for null.
     */
    @Test
    void createForNull() {
        final var iter = ExistingFiles.create(null).iterator();
        ExistingFilesTest.assertSize(iter, 0);
    }

    /**
     * Creates the for multiple files.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createForMultipleFiles() throws IOException {
        final var f1 = Files.createFile(this.folder.resolve("f1")).toFile();
        final var f2 = Files.createFile(this.folder.resolve("f2")).toFile();
        final var iter = ExistingFiles.create(Arrays.asList(f1, f2)).iterator();
        ExistingFilesTest.assertSize(iter, 2);
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
        Assertions.assertFalse(iter.hasNext());
    }

}
