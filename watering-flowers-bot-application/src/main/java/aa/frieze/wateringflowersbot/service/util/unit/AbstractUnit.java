package aa.frieze.wateringflowersbot.service.util.unit;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@RequiredArgsConstructor
public abstract class AbstractUnit {

    private final List<String> unitStrings;
    private final String identity;
    private final ChronoUnit chronoUnit;

    public boolean check(String unit) {
        return unitStrings.contains(unit);
    }

    public long toChronoUnit(long seconds) {
       return seconds / this.chronoUnit.getDuration().getSeconds();
    }
}
