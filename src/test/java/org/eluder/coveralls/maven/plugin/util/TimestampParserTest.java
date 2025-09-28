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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.junit.jupiter.api.Test;

/**
 * The Class TimestampParserTest.
 */
class TimestampParserTest {

    /**
     * Invalid format.
     */
    @Test
    void invalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TimestampParser("scsscdfsd");
        });
    }

    /**
     * Parses the epoch millis.
     *
     * @throws ProcessingException
     *             the processing exception
     */
    @Test
    void parseEpochMillis() throws ProcessingException {
        var format = TimestampParser.EPOCH_MILLIS;
        var time = System.currentTimeMillis();
        var parsed = new TimestampParser(format).parse(String.valueOf(time));

        assertEquals(time, parsed.toEpochMilli());
    }

    /**
     * Parses the simple format.
     *
     * @throws ProcessingException
     *             the processing exception
     */
    @Test
    void parseSimpleFormat() throws ProcessingException {
        var format = "yyyy-MM-dd";
        var parsed = new TimestampParser(format).parse("2015-08-20");
        var formatted = DateTimeFormatter.ofPattern(format).withZone(ZoneOffset.UTC).format(parsed);

        assertEquals("2015-08-20", formatted);
    }

    /**
     * Parses the default format.
     *
     * @throws ProcessingException
     *             the processing exception
     */
    @Test
    void parseDefaultFormat() throws ProcessingException {
        var format = TimestampParser.DEFAULT_FORMAT;
        var parsed = new TimestampParser(null).parse("2015-08-20T20:10:00Z");
        var formatted = DateTimeFormatter.ofPattern(format).withZone(ZoneOffset.UTC).format(parsed);

        assertEquals("2015-08-20T20:10:00Z", formatted);
    }

    /**
     * Parses the null.
     *
     * @throws ProcessingException
     *             the processing exception
     */
    @Test
    void parseNull() throws ProcessingException {
        var parsed = new TimestampParser(null).parse(null);

        assertNull(parsed);
    }

    /**
     * Parses the invalid timestamp.
     */
    @Test
    void parseInvalidTimestamp() {
        assertThrows(ProcessingException.class, () -> {
            new TimestampParser(null).parse("2015-08-20");
        });
    }
}
