package aa.frieze.wateringflowersbot.service.util.unit;

import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class MinuteUnit extends AbstractUnit {

    private static final List<String> unitStrings = List.of("м", "м.", "мин", "мин.", "минут", "минуты",
            "m", "m.", "mm", "min", "min.", "minute", "minutes");

    public MinuteUnit() {
        super(unitStrings, "мин", ChronoUnit.MINUTES);
    }
}
