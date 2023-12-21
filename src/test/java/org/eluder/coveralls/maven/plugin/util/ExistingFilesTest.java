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

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

class ExistingFilesTest {

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    public Path folder;


    @Test
    void testAddAllForNull() {
        ExistingFiles existingFiles = new ExistingFiles();
        assertThrows(NullPointerException.class, () -> {
            existingFiles.addAll(null);
        });
    }

    @Test
    void testAddForNull() {
        ExistingFiles existingFiles = new ExistingFiles();
        assertThrows(NullPointerException.class, () -> {
            existingFiles.add(null);
        });
    }

    @Test
    void testAddForExisting() throws IOException {
        File f = Files.createFile(folder.resolve("f")).toFile(); 
        Iterator<File> iter = new ExistingFiles().add(f).add(f).iterator();
        assertSize(iter, 1);
    }

    @Test
    void testAddForDirectory() throws IOException {
        File d = Files.createDirectory(folder.resolve("d")).toFile(); 
        Iterator<File> iter = new ExistingFiles().add(d).iterator();
        assertSize(iter, 0);
    }

    @Test
    void testCreateForNull() {
        Iterator<File> iter = ExistingFiles.create(null).iterator();
        assertSize(iter, 0);
    }

    @Test
    void testCreateForMultipleFiles() throws IOException {
        File f1 = Files.createFile(folder.resolve("f1")).toFile(); 
        File f2 = Files.createFile(folder.resolve("f2")).toFile(); 
        Iterator<File> iter = ExistingFiles.create(Arrays.asList(f1, f2)).iterator();
        assertSize(iter, 2);
    }

    private static void assertSize(Iterator<?> iter, int size) {
        for (int i = 0; i < size; i++) {
            iter.next();
        }
        assertFalse(iter.hasNext());
    }
}
