package aa.frieze.wateringflowersbot.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Список сообщений об ошибке.
 */
@Getter
@AllArgsConstructor
public enum BusinessErrorEnum {

    L999("999", "Непредвиденная ошибка"),
    E001("001", "Указан некорректный период");

    private final String code;
    private final String message;

    public AppException thr(Object... args) {
        return new AppException(this, String.format(this.message, args));
    }

    public void thr(Boolean isTrue, Object... args) {
        if (Boolean.TRUE.equals(isTrue)) return;
        throw new AppException(this, String.format(this.message, args));
    }
}
