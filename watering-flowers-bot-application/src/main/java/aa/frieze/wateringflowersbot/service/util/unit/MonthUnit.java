package aa.frieze.wateringflowersbot.service.util.unit;

import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class MonthUnit extends AbstractUnit {

    private static final List<String> unitStrings = List.of("мес", "мес.", "месяц", "месяц.", "месяцев",
            "mon.", "month", "months");

    public MonthUnit() {
        super(unitStrings, "мес", ChronoUnit.MONTHS);
    }
}
