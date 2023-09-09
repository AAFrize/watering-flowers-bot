package aa.frieze.wateringflowersbot.service.util.unit;

import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class DayUnit extends AbstractUnit {

    private static final List<String> unitStrings = List.of("д", "д.", "день", "дней", "d", "d.", "day", "days");

    public DayUnit() {
        super(unitStrings, "д", ChronoUnit.DAYS);
    }
}
