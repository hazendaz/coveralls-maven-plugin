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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.apache.commons.lang3.StringUtils;
import org.eluder.coveralls.maven.plugin.ProcessingException;

public class TimestampParser {

    public static final String EPOCH_MILLIS = "EpochMillis";

    public static final String DEFAULT_FORMAT = "uuuu-MM-dd HH:mm:ss Z";

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

    public LocalDateTime parse(final String timestamp) throws ProcessingException {
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
        LocalDateTime parse(String timestamp);
    }

    private static class DateFormatParser implements Parser {

        final DateTimeFormatter format;

        DateFormatParser(final String format) {
            if (DEFAULT_FORMAT.equals(format)) {
                this.format = DateTimeFormatter.ISO_INSTANT;
            } else {
                this.format = new DateTimeFormatterBuilder().appendPattern(format).parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).toFormatter();
            }
        }

        @Override
        public synchronized LocalDateTime parse(final String timestamp) {
            return LocalDateTime.parse(timestamp, format);
        }
    }

    private static class EpochMillisParser implements Parser {

        @Override
        public LocalDateTime parse(final String timestamp) {
            return Instant.ofEpochMilli(Long.valueOf(timestamp)).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }
}
