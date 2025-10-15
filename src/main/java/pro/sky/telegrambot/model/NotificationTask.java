package pro.sky.telegrambot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Для упрощения кода можно использовать аннотации плагина Lombok, позволяющие "скрыть" шаблонные методы: <br/>
 * конструкторы (без/с параметрами), геттеры и сеттеры, переопределение equals() и hashCode(), переопределение toString() <br/>
 * соответствующие аннотации: @NoArgsConstructor, @AllArgsConstructor, @Getter, @Setter, @EqualsAndHashCode(onlyExplicitlyIncluded = true), @ToString <br/>
 * аннотация @Data объединяет в себе аннотации @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor <br/>
 * Для этого нужно добавить зависимость в pom.xml и импортировать соответствующий класс <br/>
 * <dependency>
 *     <groupId>org.projectlombok</groupId>
 *     <artifactId>lombok</artifactId>
 *     <optional>true</optional>
 * </dependency>
 */

@Entity
@Table(name = "notification_task")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "message")
    private String message;

    @Column(name = "notification_date_time")
    private LocalDateTime notificationDateTime;

    public NotificationTask() {
    }

    public NotificationTask(Long id, Long chatId, String message, LocalDateTime notificationDateTime) {
        this.id = id;
        this.chatId = chatId;
        this.message = message;
        this.notificationDateTime = notificationDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id) && Objects.equals(chatId, that.chatId) && Objects.equals(message, that.message) && Objects.equals(notificationDateTime, that.notificationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, message, notificationDateTime);
    }

    @Override
    public String toString() {
        return "NotificationTask{" + "id=" + id + ", chatId=" + chatId + ", message='" + message + '\'' + ", dateAndTime=" + notificationDateTime + '}';
    }

}
