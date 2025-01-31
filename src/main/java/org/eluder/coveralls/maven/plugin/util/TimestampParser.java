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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.eluder.coveralls.maven.plugin.ProcessingException;

public class TimestampParser {

    public static final String EPOCH_MILLIS = "EpochMillis";

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private final Parser parser;

    public TimestampParser(final String format) {
        try {
            if (EPOCH_MILLIS.equalsIgnoreCase(format)) {
                this.parser = new EpochMillisParser();
            } else if (StringUtils.isNotBlank(format)) {
                this.parser = new DateFormatParser(format);
            } else {
                this.parser = new DateFormatParser(DEFAULT_FORMAT);
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid timestamp format \"" + format + "\"", ex);
        }
    }

    public Instant parse(final String timestamp) throws ProcessingException {
        if (StringUtils.isBlank(timestamp)) {
            return null;
        }
        try {
            return parser.parse(timestamp);
        } catch (Exception ex) {
            throw new ProcessingException("Unable to parse timestamp \"" + timestamp + "\"", ex);
        }
    }

    private interface Parser {
        Instant parse(String timestamp) throws Exception;
    }

    private static class DateFormatParser implements Parser {

        final DateFormat format;

        DateFormatParser(final String format) {
            this.format = new SimpleDateFormat(format);
        }

        @Override
        public synchronized Instant parse(final String timestamp) throws ParseException {
            return format.parse(timestamp).toInstant();
        }
    }

    private static class EpochMillisParser implements Parser {

        @Override
        public Instant parse(final String timestamp) {
            return Instant.ofEpochMilli(Long.parseLong(timestamp));
        }
    }
}
