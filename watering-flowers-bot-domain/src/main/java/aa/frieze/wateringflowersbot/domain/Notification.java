package aa.frieze.wateringflowersbot.domain;

import aa.frieze.wateringflowersbot.domain.common.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@FieldNameConstants
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Notification extends AbstractEntity<Long> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = Fields.telegramAccountId)
    private TelegramAccount telegramAccount;

    @Column(insertable = false, updatable = false)
    private Long telegramAccountId;

    @NotNull
    private String title;

    @CreationTimestamp
    private ZonedDateTime createdDate;

    @UpdateTimestamp
    private ZonedDateTime updatedDate;

    private boolean archived;

    private ZonedDateTime lastNotificationDate;

    private ZonedDateTime nextNotificationDate;

    public String getArchivedString() {
        return BooleanUtils.toString(archived, "активное", "неактивное");
    }


    public static Notification  mapNotification(TelegramAccount telegramAccount, Pair<ChronoUnit, Long> unitLongPair,
                                                ZonedDateTime usersStartDate, String title, boolean archived) {
        Notification notification = new Notification();
        return mapNotification(notification, telegramAccount, unitLongPair, usersStartDate, title, archived);
    }

    public static Notification  mapNotification(Notification notification, TelegramAccount telegramAccount,
                                                Pair<ChronoUnit, Long> unitLongPair, ZonedDateTime usersStartDate,
                                                String title, boolean archived) {
        notification.setTelegramAccount(telegramAccount);
        notification.setTitle(title);
        notification.setLastNotificationDate(usersStartDate);
        notification.setArchived(archived);
        notification.setNextNotificationDate(usersStartDate.plus(unitLongPair.getRight(), unitLongPair.getLeft()));
        return notification;
    }
}
