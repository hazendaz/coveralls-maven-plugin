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
package org.eluder.coveralls.maven.plugin.logging;

import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Branch;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.source.ChainingSourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;

/**
 * The Class CoverageTracingLogger.
 */
public class CoverageTracingLogger extends ChainingSourceCallback implements Logger {

    /** The files. */
    private long files;

    /** The lines. */
    private long lines;

    /** The relevant. */
    private long relevant;

    /** The covered. */
    private long covered;

    /** The branches. */
    private long branches;

    /** The covered branches. */
    private long coveredBranches;

    /**
     * Instantiates a new coverage tracing logger.
     *
     * @param chained
     *            the chained
     */
    public CoverageTracingLogger(final SourceCallback chained) {
        super(chained);
    }

    /**
     * Gets the files.
     *
     * @return the files
     */
    public long getFiles() {
        return files;
    }

    /**
     * Gets the lines.
     *
     * @return the lines
     */
    public final long getLines() {
        return lines;
    }

    /**
     * Gets the relevant.
     *
     * @return the relevant
     */
    public final long getRelevant() {
        return relevant;
    }

    /**
     * Gets the covered.
     *
     * @return the covered
     */
    public final long getCovered() {
        return covered;
    }

    /**
     * Gets the missed.
     *
     * @return the missed
     */
    public final long getMissed() {
        return relevant - covered;
    }

    /**
     * Gets the branches.
     *
     * @return the branches
     */
    public final long getBranches() {
        return branches;
    }

    /**
     * Gets the covered branches.
     *
     * @return the covered branches
     */
    public final long getCoveredBranches() {
        return coveredBranches;
    }

    /**
     * Gets the missed branches.
     *
     * @return the missed branches
     */
    public final long getMissedBranches() {
        return branches - coveredBranches;
    }

    @Override
    public Position getPosition() {
        return Position.AFTER;
    }

    @Override
    public void log(final Log log) {
        log.info("Gathered code coverage metrics for " + getFiles() + " source files with " + getLines()
                + " lines of code:");
        log.info("- " + getRelevant() + " relevant lines");
        log.info("- " + getCovered() + " covered lines");
        log.info("- " + getMissed() + " missed lines");
        log.info("- " + getBranches() + " branches");
        log.info("- " + getCoveredBranches() + " covered branches");
        log.info("- " + getMissedBranches() + " missed branches");
    }

    @Override
    protected void onSourceInternal(final Source source) throws ProcessingException, IOException {
        files++;
        lines += source.getCoverage().length;
        for (Integer coverage : source.getCoverage()) {
            if (coverage != null) {
                relevant++;
                if (coverage > 0) {
                    covered++;
                }
            }
        }

        this.branches += source.getBranchesList().size();
        for (final Branch b : source.getBranchesList()) {
            if (b.getHits() > 0) {
                coveredBranches++;
            }
        }
    }
}
