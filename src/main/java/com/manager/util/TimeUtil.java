package com.manager.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class TimeUtil {
    protected static DateTimeFormatter emailDateTimeFormat = DateTimeFormatter
            .ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    //plain format is for filename without special characters
    protected static DateTimeFormatter emailDateTimeFormatPlain = DateTimeFormatter
            .ofPattern("uuuu-MM-dd-HHmmss", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    public static DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss"
            + "'Z'")
            .withResolverStyle(ResolverStyle.STRICT);

    //------------------Email Date Time---------------------

    /**
     * Parses the email date time string to a LocalDateTime.
     *
     * @param dateTimeString string of the date time
     * @return LocalDateTime object to be stored
     */
    public static LocalDateTime parseEmailDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, emailDateTimeFormat);
    }

    /**
     * Formats the email date time to a string.
     *
     * @param dateTime LocalDateTime object stored in the Email object
     * @return String of the formatted date time.
     */
    public static String formatEmailDateTime(LocalDateTime dateTime) {
        return dateTime.format(emailDateTimeFormat);
    }

    /**
     * Formats the email date time to a plain string without any special character for filename.
     *
     * @param dateTime LocalDateTiem object stored in the Email object
     * @return String of plain formatted date time.
     */
    public static String formatEmailDateTimePlain(LocalDateTime dateTime) {
        return dateTime.format(emailDateTimeFormatPlain);
    }

    //------------------Timestamp Date Time----------------------

    /**
     * Gets a timestamp to be used in file.
     *
     * @return timestamp in string
     */
    public static String getTimestamp() {
        return LocalDateTime.now().format(timestampFormatter);
    }

    public static String formatTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return getTimestamp();
        }
        return dateTime.format(timestampFormatter);
    }

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * Parses a timestamp to LocalDateTime.
     *
     * @param timestamp timestamp stored in file in string
     * @return LocalDateTime parsed from timestamp
     */
    public static LocalDateTime parseTimestamp(String timestamp) {
        return LocalDateTime.parse(timestamp, timestampFormatter);
    }
}
