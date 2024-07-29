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
package org.eluder.coveralls.maven.plugin.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SourceTest {

    @Test
    void testAddCoverage() {
        Source source = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n", "E8BD88CF0BDB77A6408234FD91FD22C3");
        source.addCoverage(1, 3);
        source.addCoverage(3, 3);
        assertArrayEquals(new Integer[] { 3, null, 3, null }, source.getCoverage());
    }

    @Test
    void testAddBranchCoverage() {
        Source source = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n", "609BD24390ADB11D11536CA2ADD18BD0");
        source.addBranchCoverage(2, 0, 0, 2);
        source.addBranchCoverage(2, 0, 1, 3);
        assertArrayEquals(new Integer[] { 2, 0, 0, 2, 2, 0, 1, 3 }, source.getBranches());
    }

    @Test
    void testAddSameBranchReplaceExistingOne() {
        Source source = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n", "609BD24390ADB11D11536CA2ADD18BD0");
        source.addBranchCoverage(2, 0, 0, 2);
        source.addBranchCoverage(2, 0, 0, 3);
        assertArrayEquals(new Integer[] { 2, 0, 0, 3 }, source.getBranches());
    }

    @Test
    void testAddSameBranchDoNotKeepOrdering() {
        Source source = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n", "609BD24390ADB11D11536CA2ADD18BD0");
        source.addBranchCoverage(2, 0, 0, 0);
        source.addBranchCoverage(2, 0, 1, 0);
        source.addBranchCoverage(2, 0, 0, 1);
        assertArrayEquals(new Integer[] { 2, 0, 1, 0, 2, 0, 0, 1 }, source.getBranches());
    }

    @Test
    void testAddCoverageForSourceOutOfBounds() {
        Source source = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n", "E8BD88CF0BDB77A6408234FD91FD22C3");
        assertThrows(IllegalArgumentException.class, () -> {
            source.addCoverage(5, 1);
        });
    }

    @Test
    void testAddBranchCoverageForSourceOutOfBounds() {
        Source source = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n", "609BD24390ADB11D11536CA2ADD18BD0");
        assertThrows(IllegalArgumentException.class, () -> {
            source.addBranchCoverage(6, 0, 0, 2);
        });
    }

    @Test
    @Disabled("#45: https://github.com/trautonen/coveralls-maven-plugin/issues/45")
    void testGetNameWithClassifier() {
        Source source = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n", "E8BD88CF0BDB77A6408234FD91FD22C3");
        source.setClassifier("Inner");
        assertEquals("src/main/java/Hello.java", source.getName());
        assertEquals("src/main/java/Hello.java#Inner", source.getFullName());
    }

    @Test
    void testMerge() {
        Source source1 = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n", "609BD24390ADB11D11536CA2ADD18BD0");
        source1.addCoverage(1, 2);
        source1.addCoverage(3, 4);
        source1.addBranchCoverage(2, 0, 0, 1);
        Source source2 = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n", "609BD24390ADB11D11536CA2ADD18BD0");
        source2.addCoverage(2, 1);
        source2.addCoverage(3, 3);
        source2.addBranchCoverage(2, 0, 0, 1);
        source2.addBranchCoverage(2, 0, 1, 3);

        Source merged = source1.merge(source2);
        assertFalse(source1 == merged);
        assertFalse(source2 == merged);
        assertEquals(source1.getName(), merged.getName());
        assertEquals(source1.getDigest(), merged.getDigest());
        assertEquals(source1.getClassifier(), merged.getClassifier());
        assertEquals(Integer.valueOf(2), merged.getCoverage()[0]);
        assertEquals(Integer.valueOf(1), merged.getCoverage()[1]);
        assertEquals(Integer.valueOf(7), merged.getCoverage()[2]);
        assertNull(merged.getCoverage()[3]);
        assertEquals(Integer.valueOf(2), merged.getBranches()[0]);
        assertEquals(Integer.valueOf(0), merged.getBranches()[1]);
        assertEquals(Integer.valueOf(0), merged.getBranches()[2]);
        assertEquals(Integer.valueOf(2), merged.getBranches()[3]);
        assertEquals(Integer.valueOf(2), merged.getBranches()[4]);
        assertEquals(Integer.valueOf(0), merged.getBranches()[5]);
        assertEquals(Integer.valueOf(1), merged.getBranches()[6]);
        assertEquals(Integer.valueOf(3), merged.getBranches()[7]);
    }

    @Test
    void testMergeDifferent() {
        Source source1 = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n", "E8BD88CF0BDB77A6408234FD91FD22C3");
        source1.addCoverage(1, 3);
        Source source2 = new Source("src/main/java/Hello.java", "public class Hello {\n  void();\n}\n", "CBA7831606B51D1499349451B70758E3");
        source2.addCoverage(2, 4);
        Source merged = source1.merge(source2);
        assertFalse(source1 == merged);
        assertFalse(source2 == merged);
        assertArrayEquals(source1.getCoverage(), merged.getCoverage());
    }

    @Test
    void testEqualsForNull() {
        Source source = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n", "E8BD88CF0BDB77A6408234FD91FD22C3");
        assertNotNull(source);
    }

    @Test
    void testEqualsForDifferentSources() {
        Source source1 = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n", "E8BD88CF0BDB77A6408234FD91FD22C3");
        Source source2 = new Source("src/main/java/Hello.java", "public class Hello {\n  void();\n}\n", "CBA7831606B51D1499349451B70758E3");
        assertNotEquals(source1, source2);
    }

    @Test
    void testHashCode() {
        Source source1 = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n", "E8BD88CF0BDB77A6408234FD91FD22C3");
        Source source2 = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n", "E8BD88CF0BDB77A6408234FD91FD22C3");
        Source source3 = new Source("src/main/java/Hello.java", "public class Hello {\n  void();\n}\n", "CBA7831606B51D1499349451B70758E3");
        assertEquals(source1.hashCode(), source2.hashCode());
        assertNotEquals(source1.hashCode(), source3.hashCode());
        assertNotEquals(source2.hashCode(), source3.hashCode());
    }
}
