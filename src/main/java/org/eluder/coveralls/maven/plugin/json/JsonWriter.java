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
package org.eluder.coveralls.maven.plugin.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map.Entry;
import java.util.Properties;

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Job;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;

/**
 * The Class JsonWriter.
 */
public class JsonWriter implements SourceCallback, Closeable {

    /** The Constant TIMESTAMP_FORMAT. */
    protected static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    /** The job. */
    private final Job job;

    /** The coveralls file. */
    private final File coverallsFile;

    /** The generator. */
    private final JsonGenerator generator;

    /**
     * Instantiates a new json writer.
     *
     * @param job
     *            the job
     * @param coverallsFile
     *            the coveralls file
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public JsonWriter(final Job job, final File coverallsFile) throws IOException {
        final var directory = coverallsFile.getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        this.job = job;
        this.coverallsFile = coverallsFile;
        this.generator = new MappingJsonFactory().createGenerator(coverallsFile, JsonEncoding.UTF8);
    }

    /**
     * Gets the job.
     *
     * @return the job
     */
    public final Job getJob() {
        return this.job;
    }

    /**
     * Gets the coveralls file.
     *
     * @return the coveralls file
     */
    public final File getCoverallsFile() {
        return this.coverallsFile;
    }

    @Override
    public void onBegin() throws ProcessingException, IOException {
        try {
            this.generator.writeStartObject();
            this.writeOptionalString("repo_token", this.job.getRepoToken());
            this.writeOptionalString("service_name", this.job.getServiceName());
            this.writeOptionalString("service_job_id", this.job.getServiceJobId());
            this.writeOptionalString("service_number", this.job.getServiceBuildNumber());
            this.writeOptionalString("service_build_url", this.job.getServiceBuildUrl());
            this.writeOptionalString("service_branch", this.job.getBranch());
            this.writeOptionalString("service_pull_request", this.job.getPullRequest());
            this.writeOptionalBoolean("parallel", this.job.isParallel());
            this.writeOptionalTimestamp("run_at", this.job.getTimestamp());
            this.writeOptionalEnvironment("environment", this.job.getServiceEnvironment());
            this.writeOptionalObject("git", this.job.getGit());
            this.generator.writeArrayFieldStart("source_files");
        } catch (final JsonProcessingException ex) {
            throw new ProcessingException(ex);
        }
    }

    @Override
    public void onSource(final Source source) throws ProcessingException, IOException {
        try {
            this.generator.writeObject(source);
        } catch (final JsonProcessingException ex) {
            throw new ProcessingException(ex);
        }
    }

    @Override
    public void onComplete() throws ProcessingException, IOException {
        try {
            this.generator.writeEndArray();
            this.generator.writeEndObject();
        } catch (final JsonProcessingException ex) {
            throw new ProcessingException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        this.generator.close();
    }

    /**
     * Write optional string.
     *
     * @param field
     *            the field
     * @param value
     *            the value
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void writeOptionalString(final String field, final String value) throws IOException {
        if (value != null && !value.isBlank()) {
            this.generator.writeStringField(field, value);
        }
    }

    /**
     * Write optional boolean.
     *
     * @param field
     *            the field
     * @param value
     *            the value
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void writeOptionalBoolean(final String field, final boolean value) throws IOException {
        if (value) {
            this.generator.writeBooleanField(field, value);
        }
    }

    /**
     * Write optional object.
     *
     * @param field
     *            the field
     * @param value
     *            the value
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void writeOptionalObject(final String field, final Object value) throws IOException {
        if (value != null) {
            this.generator.writeObjectField(field, value);
        }
    }

    /**
     * Write optional timestamp.
     *
     * @param field
     *            the field
     * @param value
     *            the value
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void writeOptionalTimestamp(final String field, final Long value) throws IOException {
        if (value != null) {
            final var formatter = DateTimeFormatter.ofPattern(JsonWriter.TIMESTAMP_FORMAT)
                    .withZone(ZoneId.systemDefault());
            this.writeOptionalString(field, formatter.format(Instant.ofEpochMilli(value)));
        }
    }

    /**
     * Write optional environment.
     *
     * @param field
     *            the field
     * @param properties
     *            the properties
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void writeOptionalEnvironment(final String field, final Properties properties) throws IOException {
        if (properties != null) {
            this.generator.writeObjectFieldStart(field);
            for (final Entry<Object, Object> property : properties.entrySet()) {
                this.writeOptionalString(property.getKey().toString(), property.getValue().toString());
            }
            this.generator.writeEndObject();
        }
    }
}
