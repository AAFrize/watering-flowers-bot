package aa.frieze.wateringflowersbot.service.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import aa.frieze.wateringflowersbot.error.AppException;
import aa.frieze.wateringflowersbot.service.util.converter.LocalAsZonedDateTimeSerializer;
import aa.frieze.wateringflowersbot.service.util.converter.ZonedAsLocalDateTimeDeserializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Реализация сервиса маппинга сообщений.
 */
@Service
public class JsonMappingImpl implements JsonMappingService {

    private final ObjectMapper jsonMapper;

    /**
     * Конструктор.
     */
    public JsonMappingImpl() {
        // создание и настройка jackson маппера для парсинга json сообщений
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.disable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        var zone = ZonedDateTime.now().getZone();
        var timeModule = new SimpleModule();
        timeModule.addSerializer(LocalDateTime.class, new LocalAsZonedDateTimeSerializer(zone));
        timeModule.addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));
        timeModule.addDeserializer(LocalDateTime.class, new ZonedAsLocalDateTimeDeserializer());
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE));
        jsonMapper.registerModule(timeModule);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SneakyThrows
    public <T> JsonNode transformToJsonNode(T object) {
        return jsonMapper.readTree(map(object));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T map(String message, Class<T> messageClass) {
        try {
            return jsonMapper.readValue(message, messageClass);
        } catch (Throwable t) {
            throw new AppException(String.format("Ошибка при чтении содержимого сообщения. " +
                            "Ожидаемый класс: %s. \nСообщение: %s. \nТекст ошибки: %s",
                    messageClass.getSimpleName(), message, t.getMessage()), t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> String map(T message) {
        try {
            return jsonMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new AppException(String.format("Ошибка при формировании содержимого сообщения." +
                    "\nСообщение: %s \nТекст ошибки: %s", message.toString(), e.getMessage()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNode tree(String dto) {
        try {
            return jsonMapper.readTree(dto);
        } catch (Throwable e) {
            throw new AppException(String.format("Ошибка при обработке содержимого сообщения." +
                    "\nТекст ошибки: %s", e.getMessage()), e);
        }
    }
}
