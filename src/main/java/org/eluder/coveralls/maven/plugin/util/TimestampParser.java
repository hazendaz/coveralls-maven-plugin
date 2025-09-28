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

import org.eluder.coveralls.maven.plugin.ProcessingException;

/**
 * The Class TimestampParser.
 */
public class TimestampParser {

    /** The Constant EPOCH_MILLIS. */
    public static final String EPOCH_MILLIS = "EpochMillis";

    /** The Constant DEFAULT_FORMAT. */
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

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
            if (EPOCH_MILLIS.equalsIgnoreCase(format)) {
                this.parser = new EpochMillisParser();
            } else if (format != null && !format.isBlank()) {
                this.parser = new DateFormatParser(format);
            } else {
                this.parser = new DateFormatParser(DEFAULT_FORMAT);
            }
        } catch (IllegalArgumentException ex) {
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
        } catch (Exception ex) {
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
         * @throws ParseException
         *             the exception
         */
        Instant parse(String timestamp) throws ParseException;
    }

    /**
     * The Class DateFormatParser.
     */
    private static class DateFormatParser implements Parser {

        /** The format. */
        final DateFormat format;

        /**
         * Instantiates a new date format parser.
         *
         * @param format
         *            the format
         */
        DateFormatParser(final String format) {
            this.format = new SimpleDateFormat(format);
        }

        @Override
        public synchronized Instant parse(final String timestamp) throws ParseException {
            return format.parse(timestamp).toInstant();
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
