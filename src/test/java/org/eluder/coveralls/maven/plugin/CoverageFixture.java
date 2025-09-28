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
package org.eluder.coveralls.maven.plugin;

import java.util.List;

/**
 * The Class CoverageFixture.
 */
public final class CoverageFixture {

    /** The java files. */
    public static final List<List<String>> JAVA_FILES = List
            .of(List.of("org/eluder/coverage/sample/SimpleCoverage.java", "14", "3,6", "10,11", "", ""),
                    List.of("org/eluder/coverage/sample/InnerClassCoverage.java", "31", "3,6,9,10,12,13,16,19,22",
                            "26,27", "", ""),
                    List.of("org/eluder/coverage/sample/PartialCoverage.java", "14", "3,6,7,11", "9", "6", "6"));

    /** The java files it. */
    public static final List<List<String>> JAVA_FILES_IT = List
            .of(List.of("org/eluder/coverage/sample/SimpleCoverage.java", "14", "3,6", "10,11", "", ""),
                    List.of("org/eluder/coverage/sample/InnerClassCoverage.java", "31", "3,6,9,10,12,13,16,19,22",
                            "26,27", "", ""),
                    List.of("org/eluder/coverage/sample/PartialCoverage.java", "14", "3,6,7,9,11", "", "6", "6"));

    /** The java files clover. */
    public static final List<List<String>> JAVA_FILES_CLOVER = List
            .of(List.of("org/eluder/coverage/sample/SimpleCoverage.java", "14", "5,6", "9,10", "", ""),
                    List.of("org/eluder/coverage/sample/InnerClassCoverage.java", "31", "5,6,7,9,12,15,16,21,22",
                            "25,26", "", ""),
                    List.of("org/eluder/coverage/sample/PartialCoverage.java", "14", "5,6,7,9", "", "6", "6"));

    /** The javascript files. */
    public static final List<List<String>> JAVASCRIPT_FILES = List.of(
            List.of("Localization.js", "18", "1,2,4,5,9,13", "6,10", "", ""),
            List.of("Components.js", "5", "1,2", "", "", ""));

    /**
     * Gets the total lines.
     *
     * @param fixture
     *            the fixture
     *
     * @return the total lines
     */
    public static int getTotalLines(List<List<String>> fixture) {
        var lines = 0;
        for (List<String> file : fixture) {
            lines += Integer.parseInt(file.get(1));
        }
        return lines;
    }

    /**
     * Gets the total files.
     *
     * @param fixture
     *            the fixture
     *
     * @return the total files
     */
    public static int getTotalFiles(List<List<String>> fixture) {
        return fixture.size();
    }

    /**
     * Instantiates a new coverage fixture.
     */
    private CoverageFixture() {
        // hide constructor
    }
}
