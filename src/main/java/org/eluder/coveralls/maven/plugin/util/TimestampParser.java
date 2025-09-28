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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.eluder.coveralls.maven.plugin.ProcessingException;

/**
 * The Class TimestampParser.
 */
public class TimestampParser {

    /** The Constant EPOCH_MILLIS. */
    public static final String EPOCH_MILLIS = "EpochMillis";

    /** The Constant DEFAULT_FORMAT. */
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";

    /** The parser. */
    private final Parser parser;

    /**
     * Instantiates a new timestamp parser.
     *
     * @param format
     *            the format
     */
    public TimestampParser(final String format) {
        try {
            if (TimestampParser.EPOCH_MILLIS.equalsIgnoreCase(format)) {
                this.parser = new EpochMillisParser();
            } else if (format != null && !format.isBlank()) {
                this.parser = new DateTimeFormatterParser(format);
            } else {
                this.parser = new DateTimeFormatterParser(TimestampParser.DEFAULT_FORMAT);
            }
        } catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid timestamp format \"" + format + "\"", ex);
        }
    }

    /**
     * Parses the.
     *
     * @param timestamp
     *            the timestamp
     *
     * @return the instant
     *
     * @throws ProcessingException
     *             the processing exception
     */
    public Instant parse(final String timestamp) throws ProcessingException {
        if (timestamp == null || timestamp.isBlank()) {
            return null;
        }
        try {
            return parser.parse(timestamp);
        } catch (final Exception ex) {
            throw new ProcessingException("Unable to parse timestamp \"" + timestamp + "\"", ex);
        }
    }

    /**
     * The Interface Parser.
     */
    private interface Parser {

        /**
         * Parses the.
         *
         * @param timestamp
         *            the timestamp
         *
         * @return the instant
         *
         * @throws DateTimeParseException
         *             the date time parse exception
         */
        Instant parse(String timestamp) throws DateTimeParseException;
    }

    /**
     * The Class DateTimeFormatterParser.
     */
    private static class DateTimeFormatterParser implements Parser {

        /** The formatter. */
        final DateTimeFormatter formatter;

        /** The has zone. */
        final boolean hasZone;

        /** The date only. */
        final boolean dateOnly;

        /**
         * Instantiates a new date time formatter parser.
         *
         * @param format
         *            the format
         */
        DateTimeFormatterParser(final String format) {
            this.formatter = DateTimeFormatter.ofPattern(format);
            this.hasZone = format.contains("X") || format.contains("z") || format.contains("Z");
            this.dateOnly = !format.contains("H") && !format.contains("m") && !format.contains("s");
        }

        @Override
        public Instant parse(final String timestamp) throws DateTimeParseException {
            if (hasZone) {
                try {
                    return ZonedDateTime.parse(timestamp, formatter).toInstant();
                } catch (final DateTimeParseException ex) {
                    return OffsetDateTime.parse(timestamp, formatter).toInstant();
                }
            }
            if (dateOnly) {
                // Parse as LocalDate and set time to midnight UTC
                return LocalDate.parse(timestamp, formatter).atStartOfDay(ZoneOffset.UTC).toInstant();
            }
            // Parse as LocalDateTime and assume UTC
            return LocalDateTime.parse(timestamp, formatter).toInstant(ZoneOffset.UTC);
        }
    }

    /**
     * The Class EpochMillisParser.
     */
    private static class EpochMillisParser implements Parser {

        @Override
        public Instant parse(final String timestamp) {
            return Instant.ofEpochMilli(Long.parseLong(timestamp));
        }
    }
}
