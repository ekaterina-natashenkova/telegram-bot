package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.util.NotificationUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    private final NotificationTaskRepository repository;

    public TelegramBotUpdatesListener(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            final Chat chat = update.message().chat();
            String message = null;
            String userMessage = update.message().text();
            Long chatId = chat.id();

            /**
             * Блок для обработки уведомлений, с учетом команды /start и приветствия
             */
            String response = processNotification(chatId, userMessage, chat.username());

            SendMessage sendNotificationMessage = new SendMessage(chatId, response);
            try {
                telegramBot.execute(sendNotificationMessage);
            } catch (Exception e) {
                logger.error("Error sending message: {}", e.getMessage());
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public String processNotification(Long chatId, String text, String username) {
        if (text.equals("/start")) { // по заданию текст напоминания "01.01.2022 20:00 Сделать домашнюю работу"
            return "Привет! " + username + "\n\n" +
                    "Я бот для напоминаний.\n" +
                    "Отправь мне напоминание в формате:\n" +
                    "дд.мм.гггг чч:мм Текст_напоминания\n";
        }

        Matcher matcher = NotificationUtils.NOTIFICATION_PATTERN.matcher(text);

        if (matcher.matches()) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(matcher.group(1), NotificationUtils.DATE_TIME_FORMATTER);
                String task = matcher.group(2);

                if (dateTime.isBefore(LocalDateTime.now())) {
                    return "Нельзя установить напоминание на прошедшее время!";
                }

                NotificationTask notification = new NotificationTask();
                notification.setChatId(chatId);
                notification.setMessage(task);
                notification.setNotificationDateTime(dateTime);

                repository.save(notification);

                return "Напоминание установлено на:\n" + matcher.group(1) + " " + task;
            } catch (DateTimeParseException e) {
                return "Неверный формат даты и времени уведомления. Используй: дд.мм.гггг чч:мм";
            }
        }
        return "Неверный формат.\n\n" +
                "Для начала работы нужно написать команду - /start\n" +
                "Для установки напоминания нужно написать сообщение в формате: дд.мм.гггг чч:мм Текст_напоминания\n\n";
    }

    @Scheduled(cron = "0 0/1 * * * *")
    // параметр можно заменить на (fixedRate = 60000) - каждую минуту от запуска метода
    // или (fixedDelay = 60000) - через минуту после завершения метода
    public void sendNotifications() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasks = repository.findByNotificationDateTime(now);

        logger.info("Checking notifications for time: {}, found: {}", now, tasks.size());

        for (NotificationTask task : tasks) {
            try {
                SendMessage sendMessage = new SendMessage(
                        task.getChatId().toString(),
                        "Напоминание: " + task.getMessage()
                );
                telegramBot.execute(sendMessage);
                System.out.println("Напоминание для chatId " + task.getChatId() + ": " + task.getMessage());
                // Удаляем отправленное напоминание
                repository.delete(task);
            } catch (Exception e) {
                System.err.println("Ошибка отправки сообщения для chatId " + task.getChatId() + ": " + e.getMessage());
            }
        }
    }
}