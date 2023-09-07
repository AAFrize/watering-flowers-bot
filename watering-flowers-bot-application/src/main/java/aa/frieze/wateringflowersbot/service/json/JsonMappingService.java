package aa.frieze.wateringflowersbot.service.json;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;

/**
 * Сервис маппинга сообщений в формат json.
 */
public interface JsonMappingService {

    @SneakyThrows
    <T> JsonNode transformToJsonNode(T object);

    /**
     * Маппинг сообщения из json в ДТО.
     *
     * @param message      Сообщение в json-формате
     * @param messageClass класс, в экземпляр которого нужно преобразовать сообщение
     * @param <T>          тип ДТО сообщения
     * @return сформированный ДТО объект
     */
    <T> T map(String message, Class<T> messageClass);

    /**
     * Маппинг ДТО объекта сообщения в строку в json-формате.
     *
     * @param dto ДТО объект для преобразования
     * @param <T> Тип сообщения
     * @return сообщение, преобразованное в строку
     */
    <T> String map(T dto);

    /**
     *
     * @param dto
     * @return
     */
    JsonNode tree(String dto);
}
