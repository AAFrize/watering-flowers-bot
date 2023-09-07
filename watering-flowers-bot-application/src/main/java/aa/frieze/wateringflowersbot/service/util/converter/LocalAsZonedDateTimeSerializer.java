package aa.frieze.wateringflowersbot.service.util.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Сериализатор для LocalDateTime с установкой таймзоны.
 *
 */
public class LocalAsZonedDateTimeSerializer extends JsonSerializer<LocalDateTime> {

	private final ZoneId zone;

	public LocalAsZonedDateTimeSerializer(ZoneId zone) {
		this.zone = zone;
	}

	@Override
	public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
		gen.writeObject(value.atZone(zone)
				.format(DateTimeFormatter.ofPattern(format)));
	}
}
