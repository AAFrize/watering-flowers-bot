package aa.frieze.wateringflowersbot.service.util.unit;

import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class HourUnit extends AbstractUnit {

    private static final List<String> unitStrings = List.of("ч", "ч.", "час", "час.", "часов", "часа",
            "h", "h.", "hh", "hour", "hours");

    public HourUnit() {
        super(unitStrings, "ч", ChronoUnit.HOURS);
    }
}
