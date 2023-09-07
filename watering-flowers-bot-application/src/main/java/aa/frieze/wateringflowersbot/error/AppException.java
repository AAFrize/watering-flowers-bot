package aa.frieze.wateringflowersbot.error;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Техническая ошибка.
 */
@Getter
public class AppException extends RuntimeException {
    public static final String KEY = AppException.class.getName();

    private BusinessErrorEnum errorEnum;

    public AppException(String message, Object... args) {
        super(message);
    }

    public AppException(BusinessErrorEnum error, String message, Object... args) {
        super(message);
        this.errorEnum = error;
    }

    public AppException(BusinessErrorEnum error) {
        super(error.getMessage());
    }

    public String getMsg() {
        return StringUtils.defaultString(Objects.isNull(errorEnum) ? null : errorEnum.getMessage(),
                getMessage());
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
