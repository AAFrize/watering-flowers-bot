package aa.frieze.wateringflowersbot.domain;

import aa.frieze.wateringflowersbot.domain.common.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Data
@Entity
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Notification extends AbstractEntity<Long> {

    @NotNull
    @ManyToOne
    private TelegramAccount telegramAccount;

    @NotNull
    private String title;

    @CreationTimestamp
    private ZonedDateTime createdDate;

    @UpdateTimestamp
    private ZonedDateTime updatedDate;

    private boolean archived;

    private ZonedDateTime lastNotificationDate;

    private ZonedDateTime nextNotificationDate;

}
