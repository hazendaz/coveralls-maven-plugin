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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The Class Source.
 */
public final class Source implements JsonObject {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant NEWLINE. */
    private static final Pattern NEWLINE = Pattern.compile("\r\n|\r|\n");
    // private static final String CLASSIFIER_SEPARATOR = "#";

    /** The name. */
    String name;

    /** The digest. */
    String digest;

    /** The coverage. */
    Integer[] coverage;

    /** The branches. */
    List<Branch> branches;

    /** The classifier. */
    String classifier;

    /**
     * Instantiates a new source.
     *
     * @param name
     *            the name
     * @param source
     *            the source
     * @param digest
     *            the digest
     */
    public Source(final String name, final String source, final String digest) {
        this(name, Source.getLines(source), digest, null);
    }

    /**
     * Instantiates a new source.
     *
     * @param name
     *            the name
     * @param lines
     *            the lines
     * @param digest
     *            the digest
     * @param classifier
     *            the classifier
     */
    public Source(final String name, final int lines, final String digest, final String classifier) {
        this.name = name;
        this.digest = digest;
        this.coverage = new Integer[lines];
        this.classifier = classifier;
        this.branches = new ArrayList<>();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @JsonIgnore
    public String getName() {
        return this.name;
    }

    /**
     * Gets the full name.
     *
     * @return the full name
     */
    @JsonProperty("name")
    public String getFullName() {
        return this.name;

        // #45: cannot use identifier due to unfetchable source files
        // return (classifier == null ? name : name + CLASSIFIER_SEPARATOR + classifier);
    }

    /**
     * Gets the digest.
     *
     * @return the digest
     */
    @JsonProperty("source_digest")
    public String getDigest() {
        return this.digest;
    }

    /**
     * Gets the coverage.
     *
     * @return the coverage
     */
    @JsonProperty("coverage")
    public Integer[] getCoverage() {
        return this.coverage;
    }

    /**
     * Gets the branches.
     *
     * @return the branches
     */
    @JsonProperty("branches")
    public Integer[] getBranches() {
        final List<Integer> branchesRaw = new ArrayList<>(this.branches.size() * 4);
        for (final Branch b : this.branches) {
            branchesRaw.add(b.getLineNumber());
            branchesRaw.add(b.getBlockNumber());
            branchesRaw.add(b.getBranchNumber());
            branchesRaw.add(b.getHits());
        }
        return branchesRaw.toArray(new Integer[branchesRaw.size()]);
    }

    /**
     * Gets the branches list.
     *
     * @return the branches list
     */
    public List<Branch> getBranchesList() {
        return Collections.unmodifiableList(this.branches);
    }

    /**
     * Gets the classifier.
     *
     * @return the classifier
     */
    @JsonIgnore
    public String getClassifier() {
        return this.classifier;
    }

    /**
     * Sets the classifier.
     *
     * @param classifier
     *            the new classifier
     */
    public void setClassifier(final String classifier) {
        this.classifier = classifier;
    }

    /**
     * Check line range.
     *
     * @param lineNumber
     *            the line number
     */
    private void checkLineRange(final int lineNumber) {
        final var index = lineNumber - 1;
        if (index >= this.coverage.length) {
            throw new IllegalArgumentException(
                    "Line number " + lineNumber + " is greater than the source file " + this.name + " size");
        }
    }

    /**
     * Adds the coverage.
     *
     * @param lineNumber
     *            the line number
     * @param coverage
     *            the coverage
     */
    public void addCoverage(final int lineNumber, final Integer coverage) {
        this.checkLineRange(lineNumber);
        this.coverage[lineNumber - 1] = coverage;
    }

    /**
     * Adds the branch coverage.
     *
     * @param lineNumber
     *            the line number
     * @param blockNumber
     *            the block number
     * @param branchNumber
     *            the branch number
     * @param hits
     *            the hits
     */
    public void addBranchCoverage(final int lineNumber, final int blockNumber, final int branchNumber, final int hits) {
        this.addBranchCoverage(false, lineNumber, blockNumber, branchNumber, hits);
    }

    /**
     * Adds the branch coverage.
     *
     * @param merge
     *            the merge
     * @param lineNumber
     *            the line number
     * @param blockNumber
     *            the block number
     * @param branchNumber
     *            the branch number
     * @param hits
     *            the hits
     */
    private void addBranchCoverage(final boolean merge, final int lineNumber, final int blockNumber,
            final int branchNumber, final int hits) {
        this.checkLineRange(lineNumber);
        var hitSum = hits;
        final var it = this.branches.listIterator();
        while (it.hasNext()) {
            final var b = it.next();
            if (b.getLineNumber() == lineNumber && b.getBlockNumber() == blockNumber
                    && b.getBranchNumber() == branchNumber) {
                it.remove();
                if (merge) {
                    hitSum += b.getHits();
                }
            }
        }
        this.branches.add(new Branch(lineNumber, blockNumber, branchNumber, hitSum));
    }

    /**
     * Merge.
     *
     * @param source
     *            the source
     *
     * @return the source
     */
    public Source merge(final Source source) {
        final var copy = new Source(this.name, this.coverage.length, this.digest, this.classifier);
        System.arraycopy(this.coverage, 0, copy.coverage, 0, this.coverage.length);
        copy.branches.addAll(this.branches);
        if (copy.equals(source)) {
            for (var i = 0; i < copy.coverage.length; i++) {
                if (source.coverage[i] != null) {
                    final var base = copy.coverage[i] != null ? copy.coverage[i] : 0;
                    copy.coverage[i] = base + source.coverage[i];
                }
            }
            for (final Branch b : source.branches) {
                copy.addBranchCoverage(true, b.getLineNumber(), b.getBlockNumber(), b.getBranchNumber(), b.getHits());
            }
        }
        return copy;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Source)) {
            return false;
        }
        Source other = (Source) obj;
        return Objects.equals(this.name, other.name) && Objects.equals(this.digest, other.digest)
                && this.coverage.length == other.coverage.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.digest, this.coverage.length);
    }

    /**
     * Gets the lines.
     *
     * @param source
     *            the source
     *
     * @return the lines
     */
    private static int getLines(final String source) {
        var lines = 1;
        final var matcher = Source.NEWLINE.matcher(source);
        while (matcher.find()) {
            lines++;
        }
        return lines;
    }
}
