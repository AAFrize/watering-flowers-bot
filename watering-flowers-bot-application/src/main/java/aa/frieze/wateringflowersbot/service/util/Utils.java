package aa.frieze.wateringflowersbot.service.util;

import com.google.common.base.Preconditions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static aa.frieze.wateringflowersbot.service.util.Constants.START_DATE_ILLEGAL_WARNING_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.START_DATE_WARNING_MESSAGE;
import static aa.frieze.wateringflowersbot.service.util.Constants.dateFormatterInput;
import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

/**
 * Вспомогательный класс.
 */
public class Utils {

    /**
     * Получить значения отложенного supplier'a.
     *
     * @param supplier supplier.
     * @param <T>      тип.
     * @return результат выполнения.
     */
    public static <T> T safeGet(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T, V> V safeGet(T t, Function<T, V> converter) {
        return Optional.ofNullable(t).map(converter).orElse(null);
    }

    /**
     * @param throwable исключение
     * @return корень
     */
    public static Throwable findCauseUsingPlainJava(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (Objects.nonNull(rootCause.getCause()) && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    public static String camel2underscore(String text) {
        return Objects.isNull(text) ? null : LOWER_CAMEL.to(LOWER_UNDERSCORE, text);
    }

    public static ZonedDateTime getZonedDateTimeAndCheck(SendMessage replyToUser, String startDateString,
                                                         ZoneId userZone) {
        try {
            LocalDateTime startDateLocal = LocalDateTime.from(dateFormatterInput.parse(startDateString));
            ZonedDateTime startDate = ZonedDateTime.of(startDateLocal, userZone);
            Preconditions.checkState(ZonedDateTime.now().isBefore(startDate));
            return startDate;
        } catch (DateTimeParseException exception) {
            replyToUser.setText(START_DATE_WARNING_MESSAGE);
            return null;
        } catch (IllegalStateException exception) {
            replyToUser.setText(START_DATE_ILLEGAL_WARNING_MESSAGE);
            return null;
        }
    }
}
