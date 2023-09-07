package aa.frieze.wateringflowersbot.domain.common;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Базовая сущность для справочников.
 */
@Data
@MappedSuperclass
public abstract class RefEntity {
    @Id
    @NotNull
    private String key;

    @NotBlank
    private String value;
}
