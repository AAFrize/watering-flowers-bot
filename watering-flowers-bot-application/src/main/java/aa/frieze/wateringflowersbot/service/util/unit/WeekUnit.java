package aa.frieze.wateringflowersbot.service.util.unit;

import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class WeekUnit extends AbstractUnit {

    private static final List<String> unitStrings = List.of("н", "н.", "нед", "нед.", "недель", "недели",
            "w", "w.", "week", "weeks");

    public WeekUnit() {
        super(unitStrings, "нед", ChronoUnit.WEEKS);
    }
}
