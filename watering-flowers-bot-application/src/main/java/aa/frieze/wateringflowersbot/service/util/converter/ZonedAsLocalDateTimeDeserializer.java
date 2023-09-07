package aa.frieze.wateringflowersbot.service.util.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Десериализатор для LocalDateTime с корректировкой таймзоны.
 */
public class ZonedAsLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter dateTimeFormatter;

    /**
     * Конструктор по-умолчанию.
     */
    public ZonedAsLocalDateTimeDeserializer() {
        this.dateTimeFormatter = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_ZONED_DATE_TIME)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
                .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                .toFormatter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            return ZonedDateTime.parse(jsonParser.getText(), dateTimeFormatter)
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }
}

