/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2025 The Coveralls Maven Plugin Project Contributors:
 *     https://github.com/hazendaz/coveralls-maven-plugin/graphs/contributors
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.apache.maven.plugin.logging.Log;
import org.eluder.coveralls.maven.plugin.domain.Job;

/**
 * The Class JobLogger.
 */
public class JobLogger implements Logger {

    /** The Constant ABBREV. */
    private static final int ABBREV = 7;

    /** The job. */
    private final Job job;

    /** The json mapper. */
    private final ObjectMapper jsonMapper;

    /**
     * Instantiates a new job logger.
     *
     * @param job
     *            the job
     */
    public JobLogger(final Job job) {
        this(job, null);
    }

    /**
     * Instantiates a new job logger.
     *
     * @param job
     *            the job
     * @param jsonMapper
     *            the json mapper
     */
    public JobLogger(final Job job, final ObjectMapper jsonMapper) {
        if (job == null) {
            throw new IllegalArgumentException("job must be defined");
        }
        this.job = job;
        this.jsonMapper = jsonMapper != null ? jsonMapper : this.createDefaultJsonMapper();
    }

    @Override
    public Position getPosition() {
        return Position.BEFORE;
    }

    @Override
    public void log(final Log log) {
        final var starting = new StringBuilder("Starting Coveralls job");
        if (this.job.getServiceName() != null) {
            starting.append(" for ").append(this.job.getServiceName());
            if (this.job.getServiceJobId() != null) {
                starting.append(" (").append(this.job.getServiceJobId()).append(")");
            } else if (this.job.getServiceBuildNumber() != null) {
                starting.append(" (").append(this.job.getServiceBuildNumber());
                if (this.job.getServiceBuildUrl() != null) {
                    starting.append(" / ").append(this.job.getServiceBuildUrl());
                }
                starting.append(")");
            }
        }
        if (this.job.isDryRun()) {
            starting.append(" in dry run mode");
        }
        if (this.job.isParallel()) {
            starting.append(" with parallel option enabled");
        }
        log.info(starting.toString());

        if (this.job.getRepoToken() != null) {
            log.info("Using repository token <secret>");
        }

        if (this.job.getGit() != null) {
            final var commit = this.job.getGit().getHead().getId();
            final var branch = this.job.getBranch() != null ? this.job.getBranch() : this.job.getGit().getBranch();
            log.info("Git commit " + commit.substring(0, JobLogger.ABBREV) + " in " + branch);
        }

        if (log.isDebugEnabled()) {
            try {
                log.debug("Complete Job description:\n" + this.jsonMapper.writeValueAsString(this.job));
            } catch (final JsonProcessingException e) {
                throw new IllegalStateException("FAiled to serialize job to JSON", e);
            }
        }
    }

    /**
     * Creates the default json mapper.
     *
     * @return the object mapper
     */
    private ObjectMapper createDefaultJsonMapper() {
        return JsonMapper.builder().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true).build();
    }

}
