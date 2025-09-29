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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * The Class SourceTest.
 */
class SourceTest {

    /**
     * Test add coverage.
     */
    @Test
    void testAddCoverage() {
        final var source = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n",
                "E8BD88CF0BDB77A6408234FD91FD22C3");
        source.addCoverage(1, 3);
        source.addCoverage(3, 3);
        Assertions.assertArrayEquals(new Integer[] { 3, null, 3, null }, source.getCoverage());
    }

    /**
     * Test add branch coverage.
     */
    @Test
    void testAddBranchCoverage() {
        final var source = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n",
                "609BD24390ADB11D11536CA2ADD18BD0");
        source.addBranchCoverage(2, 0, 0, 2);
        source.addBranchCoverage(2, 0, 1, 3);
        Assertions.assertArrayEquals(new Integer[] { 2, 0, 0, 2, 2, 0, 1, 3 }, source.getBranches());
    }

    /**
     * Adds the same branch replace existing one.
     */
    @Test
    void addSameBranchReplaceExistingOne() {
        final var source = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n",
                "609BD24390ADB11D11536CA2ADD18BD0");
        source.addBranchCoverage(2, 0, 0, 2);
        source.addBranchCoverage(2, 0, 0, 3);
        Assertions.assertArrayEquals(new Integer[] { 2, 0, 0, 3 }, source.getBranches());
    }

    /**
     * Adds the same branch do not keep ordering.
     */
    @Test
    void addSameBranchDoNotKeepOrdering() {
        final var source = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n",
                "609BD24390ADB11D11536CA2ADD18BD0");
        source.addBranchCoverage(2, 0, 0, 0);
        source.addBranchCoverage(2, 0, 1, 0);
        source.addBranchCoverage(2, 0, 0, 1);
        Assertions.assertArrayEquals(new Integer[] { 2, 0, 1, 0, 2, 0, 0, 1 }, source.getBranches());
    }

    /**
     * Adds the coverage for source out of bounds.
     */
    @Test
    void addCoverageForSourceOutOfBounds() {
        final var source = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n",
                "E8BD88CF0BDB77A6408234FD91FD22C3");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            source.addCoverage(5, 1);
        });
    }

    /**
     * Adds the branch coverage for source out of bounds.
     */
    @Test
    void addBranchCoverageForSourceOutOfBounds() {
        final var source = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n",
                "609BD24390ADB11D11536CA2ADD18BD0");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            source.addBranchCoverage(6, 0, 0, 2);
        });
    }

    /**
     * Gets the name with classifier.
     */
    @Test
    @Disabled("#45: https://github.com/trautonen/coveralls-maven-plugin/issues/45")
    void getNameWithClassifier() {
        final var source = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n",
                "E8BD88CF0BDB77A6408234FD91FD22C3");
        source.setClassifier("Inner");
        Assertions.assertEquals("src/main/java/Hello.java", source.getName());
        Assertions.assertEquals("src/main/java/Hello.java#Inner", source.getFullName());
    }

    /**
     * Test merge.
     */
    @Test
    void testMerge() {
        final var source1 = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n",
                "609BD24390ADB11D11536CA2ADD18BD0");
        source1.addCoverage(1, 2);
        source1.addCoverage(3, 4);
        source1.addBranchCoverage(2, 0, 0, 1);
        final var source2 = new Source("src/main/java/Hello.java", "public class Hello {\n  if(true) {\n  }\n}\n",
                "609BD24390ADB11D11536CA2ADD18BD0");
        source2.addCoverage(2, 1);
        source2.addCoverage(3, 3);
        source2.addBranchCoverage(2, 0, 0, 1);
        source2.addBranchCoverage(2, 0, 1, 3);

        final var merged = source1.merge(source2);
        org.assertj.core.api.Assertions.assertThat(source1).isNotSameAs(merged);
        org.assertj.core.api.Assertions.assertThat(source2).isNotSameAs(merged);
        Assertions.assertEquals(source1.getName(), merged.getName());
        Assertions.assertEquals(source1.getDigest(), merged.getDigest());
        Assertions.assertEquals(source1.getClassifier(), merged.getClassifier());
        Assertions.assertEquals(Integer.valueOf(2), merged.getCoverage()[0]);
        Assertions.assertEquals(Integer.valueOf(1), merged.getCoverage()[1]);
        Assertions.assertEquals(Integer.valueOf(7), merged.getCoverage()[2]);
        Assertions.assertNull(merged.getCoverage()[3]);
        Assertions.assertEquals(Integer.valueOf(2), merged.getBranches()[0]);
        Assertions.assertEquals(Integer.valueOf(0), merged.getBranches()[1]);
        Assertions.assertEquals(Integer.valueOf(0), merged.getBranches()[2]);
        Assertions.assertEquals(Integer.valueOf(2), merged.getBranches()[3]);
        Assertions.assertEquals(Integer.valueOf(2), merged.getBranches()[4]);
        Assertions.assertEquals(Integer.valueOf(0), merged.getBranches()[5]);
        Assertions.assertEquals(Integer.valueOf(1), merged.getBranches()[6]);
        Assertions.assertEquals(Integer.valueOf(3), merged.getBranches()[7]);
    }

    /**
     * Merge different.
     */
    @Test
    void mergeDifferent() {
        final var source1 = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n",
                "E8BD88CF0BDB77A6408234FD91FD22C3");
        source1.addCoverage(1, 3);
        final var source2 = new Source("src/main/java/Hello.java", "public class Hello {\n  void();\n}\n",
                "CBA7831606B51D1499349451B70758E3");
        source2.addCoverage(2, 4);
        final var merged = source1.merge(source2);
        org.assertj.core.api.Assertions.assertThat(source1).isNotSameAs(merged);
        org.assertj.core.api.Assertions.assertThat(source2).isNotSameAs(merged);
        Assertions.assertArrayEquals(source1.getCoverage(), merged.getCoverage());
    }

    /**
     * Equals for null.
     */
    @Test
    void equalsForNull() {
        final var source = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n",
                "E8BD88CF0BDB77A6408234FD91FD22C3");
        Assertions.assertNotNull(source);
    }

    /**
     * Equals for different sources.
     */
    @Test
    void equalsForDifferentSources() {
        final var source1 = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n",
                "E8BD88CF0BDB77A6408234FD91FD22C3");
        final var source2 = new Source("src/main/java/Hello.java", "public class Hello {\n  void();\n}\n",
                "CBA7831606B51D1499349451B70758E3");
        Assertions.assertNotEquals(source1, source2);
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode() {
        final var source1 = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n",
                "E8BD88CF0BDB77A6408234FD91FD22C3");
        final var source2 = new Source("src/main/java/Hello.java", "public class Hello {\n  \n}\n",
                "E8BD88CF0BDB77A6408234FD91FD22C3");
        final var source3 = new Source("src/main/java/Hello.java", "public class Hello {\n  void();\n}\n",
                "CBA7831606B51D1499349451B70758E3");
        Assertions.assertEquals(source1.hashCode(), source2.hashCode());
        Assertions.assertNotEquals(source1.hashCode(), source3.hashCode());
        Assertions.assertNotEquals(source2.hashCode(), source3.hashCode());
    }

}
