package pro.sky.telegrambot.util;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Класс с константами для использования в методе processNotification класса TelegramBotUpdatesListener
 */
public class NotificationUtils {

    private static final String DATE_TIME_REGEX = "(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}) (.+)";
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    public static final Pattern NOTIFICATION_PATTERN = Pattern.compile(DATE_TIME_REGEX);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

}
