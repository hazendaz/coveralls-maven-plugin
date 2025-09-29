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
package org.eluder.coveralls.maven.plugin.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Git;
import org.eluder.coveralls.maven.plugin.domain.Job;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

/**
 * The Class JsonWriterTest.
 */
class JsonWriterTest {

    /** The Constant TEST_TIME. */
    static final long TEST_TIME = 1357009200000L;

    /** The folder. */
    @TempDir(cleanup = CleanupMode.NEVER)
    Path folder;

    /** The file. */
    File file;

    /**
     * Inits the.
     */
    @BeforeEach
    void init() {
        this.file = this.folder.resolve("file").toFile();
    }

    /**
     * Sub directory creation.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void subDirectoryCreation() throws IOException {
        final var f = this.folder.resolve("sub1").resolve("sub2");
        final var job = this.job();
        try (var writer = new JsonWriter(job, f.toFile())) {
            Assertions.assertTrue(writer.getCoverallsFile().getParentFile().isDirectory());
        }
    }

    /**
     * Test get job.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testGetJob() throws IOException {
        final var job = this.job();
        try (var writer = new JsonWriter(job, this.file)) {
            Assertions.assertSame(job, writer.getJob());
        }
    }

    /**
     * Test get coveralls file.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testGetCoverallsFile() throws IOException {
        final var job = this.job();
        try (var writer = new JsonWriter(job, this.file)) {
            Assertions.assertSame(this.file, writer.getCoverallsFile());
        }
    }

    /**
     * Write start and end.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ProcessingException
     *             the processing exception
     */
    @SuppressWarnings("rawtypes")
    @Test
    void writeStartAndEnd() throws IOException, ProcessingException {
        try (var writer = new JsonWriter(this.job(), this.file)) {
            writer.onBegin();
            writer.onComplete();
        }
        final var content = TestIoUtil.readFileContent(this.file);
        final var jsonMap = this.stringToJsonMap(content);
        Assertions.assertEquals("service", jsonMap.get("service_name"));
        Assertions.assertEquals("job123", jsonMap.get("service_job_id"));
        Assertions.assertEquals("build5", jsonMap.get("service_number"));
        Assertions.assertEquals("https://ci.com/build5", jsonMap.get("service_build_url"));
        Assertions.assertEquals("foobar", ((Map) jsonMap.get("environment")).get("custom_property"));
        Assertions.assertEquals("master", jsonMap.get("service_branch"));
        Assertions.assertEquals("pull10", jsonMap.get("service_pull_request"));

        final var formatter = DateTimeFormatter.ofPattern(JsonWriter.TIMESTAMP_FORMAT).withZone(ZoneId.systemDefault());
        final var expectedRunAt = formatter.format(Instant.ofEpochMilli(JsonWriterTest.TEST_TIME));
        Assertions.assertEquals(expectedRunAt, jsonMap.get("run_at"));

        Assertions.assertEquals("af456fge34acd", ((Map) jsonMap.get("git")).get("branch"));
        Assertions.assertEquals("aefg837fge", ((Map) ((Map) jsonMap.get("git")).get("head")).get("id"));
        Assertions.assertEquals(0, ((Collection<?>) jsonMap.get("source_files")).size());
    }

    /**
     * Test on source.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ProcessingException
     *             the processing exception
     */
    @Test
    void testOnSource() throws IOException, ProcessingException {
        try (var writer = new JsonWriter(this.job(), this.file)) {
            writer.onBegin();
            writer.onSource(this.source());
            writer.onComplete();
        }
        final var content = TestIoUtil.readFileContent(this.file);
        var jsonMap = this.stringToJsonMap(content);
        if (jsonMap.get("source_files") instanceof List) {
            jsonMap = ((List<Map<String, Object>>) jsonMap.get("source_files")).get(0);
        }
        Assertions.assertEquals("Foo.java", jsonMap.get("name"));
        Assertions.assertEquals("6E0F89B516198DC6AB743EA5FBFB3108", jsonMap.get("source_digest"));
        Assertions.assertEquals(1, ((Collection<?>) jsonMap.get("coverage")).size());
    }

    /**
     * Job.
     *
     * @return the job
     */
    Job job() {
        final var head = new Git.Head("aefg837fge", "john", "john@mail.com", "john", "john@mail.com", "test commit");
        final var remote = new Git.Remote("origin", "git@git.com:foo.git");
        final var environment = new Properties();
        environment.setProperty("custom_property", "foobar");
        return new Job().withServiceName("service").withServiceJobId("job123").withServiceBuildNumber("build5")
                .withServiceBuildUrl("https://ci.com/build5").withServiceEnvironment(environment).withBranch("master")
                .withPullRequest("pull10").withTimestamp(JsonWriterTest.TEST_TIME)
                .withGit(new Git(null, head, "af456fge34acd", Arrays.asList(remote)));
    }

    /**
     * Source.
     *
     * @return the source
     */
    Source source() {
        return new Source("Foo.java", "public class Foo { }", "6E0F89B516198DC6AB743EA5FBFB3108");
    }

    /**
     * String to json map.
     *
     * @param content
     *            the content
     *
     * @return the map
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    Map<String, Object> stringToJsonMap(final String content) throws JsonProcessingException {
        final var mapper = new ObjectMapper();
        final var type = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        return mapper.readValue(content, type);
    }

}
