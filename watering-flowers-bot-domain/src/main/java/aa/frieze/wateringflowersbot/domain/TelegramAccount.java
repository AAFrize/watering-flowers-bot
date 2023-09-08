package aa.frieze.wateringflowersbot.domain;

import aa.frieze.wateringflowersbot.domain.common.AbstractEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Data
@Entity
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class TelegramAccount extends AbstractEntity<Long> {

    @NotNull
    private Long chatId;

    private String username;

    @CreationTimestamp
    private ZonedDateTime createdDate;

    @UpdateTimestamp
    private ZonedDateTime updatedDate;

    private boolean subscribed;

    @Type(type = "jsonb")
    private JsonNode settings;

}
