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

/**
 * The Class Branch.
 */
public class Branch {

    /** The line number. */
    private final int lineNumber;

    /** The block number. */
    private final int blockNumber;

    /** The branch number. */
    private final int branchNumber;

    /** The hits. */
    private final int hits;

    /**
     * Instantiates a new branch.
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
    public Branch(final int lineNumber, final int blockNumber, final int branchNumber, final int hits) {
        this.lineNumber = lineNumber;
        this.blockNumber = blockNumber;
        this.branchNumber = branchNumber;
        this.hits = hits;
    }

    /**
     * Gets the line number.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets the block number.
     *
     * @return the block number
     */
    public int getBlockNumber() {
        return blockNumber;
    }

    /**
     * Gets the branch number.
     *
     * @return the branch number
     */
    public int getBranchNumber() {
        return branchNumber;
    }

    /**
     * Gets the hits.
     *
     * @return the hits
     */
    public int getHits() {
        return hits;
    }

}
