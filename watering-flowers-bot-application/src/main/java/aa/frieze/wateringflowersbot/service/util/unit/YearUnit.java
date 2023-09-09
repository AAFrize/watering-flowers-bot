package aa.frieze.wateringflowersbot.service.util.unit;

import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class YearUnit extends AbstractUnit {

    private static final List<String> unitStrings = List.of("г", "г.", "год", "год.", "лет", "лет.", "y", "y.",
            "year", "years");

    public YearUnit() {
        super(unitStrings, "г", ChronoUnit.YEARS);
    }
}
