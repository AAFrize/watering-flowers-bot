package aa.frieze.wateringflowersbot.service.util;

import aa.frieze.wateringflowersbot.error.BusinessErrorEnum;
import aa.frieze.wateringflowersbot.service.util.unit.AbstractUnit;
import aa.frieze.wateringflowersbot.service.util.unit.MinuteUnit;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UnitParser {

    private final List<AbstractUnit> units;

    public UnitParser(List<AbstractUnit> units) {
        units.sort(Comparator.comparing(AbstractUnit::getChronoUnit));
        this.units = units;
    }

    public Pair<ChronoUnit, Long> parsePeriodFromString(String periodString) {
        List<String> unitList = Arrays.stream(periodString.split("\\d*.?\\d+?"))
                .map(unit -> RegExUtils.replaceAll(unit.toLowerCase(), "[\\s,\\.]", ""))
                .filter(StringUtils::isNoneBlank).toList();

        Pattern pattern = Pattern.compile("\\d*\\.?,?\\d+?");
        Matcher matcher = pattern.matcher(periodString);
        List<String> stringValuesList = new ArrayList<>();
        while (matcher.find()) {
            stringValuesList.add(matcher.group());
        }

        BusinessErrorEnum.E001.thr(unitList.size() == stringValuesList.size());

        List<AbstractUnit> chronoUnitList = unitList.stream()
                .map(this::findUnitByString)
                .toList();
        long sumValue = 0;
        AbstractUnit minUnit = chronoUnitList.stream()
                .min(Comparator.comparing(AbstractUnit::getChronoUnit))
                .orElseThrow(BusinessErrorEnum.E001::thr);
        for (int i = 0; i < chronoUnitList.size(); i++) {
            ChronoUnit currentChronoUnit = chronoUnitList.get(i).getChronoUnit();
            long secondsInUnit = currentChronoUnit.getDuration().get(ChronoUnit.SECONDS);
            double doubleValue = Double.parseDouble(stringValuesList.get(i).replace(',', '.'));

            minUnit = findMinUnitIfExist(doubleValue, currentChronoUnit, minUnit);
            sumValue += (long) (minUnit.toChronoUnit(secondsInUnit) * doubleValue);
        }
        return Pair.of(minUnit.getChronoUnit(), sumValue);
    }

    private AbstractUnit findMinUnitIfExist(double doubleValue, ChronoUnit currentChronoUnit, AbstractUnit minUnit) {
        if (doubleValue % 1 == 0) {
            return minUnit;
        }
        if ((currentChronoUnit.compareTo(minUnit.getChronoUnit()) > 0)) {
            return minUnit;
        }
        if (minUnit instanceof MinuteUnit) {
            return minUnit;
        }

        return units.stream()
                .filter(unit -> unit.getChronoUnit().compareTo(minUnit.getChronoUnit()) < 0)
                .findFirst()
                .orElse(new MinuteUnit());
    }

    private AbstractUnit findUnitByString(String unitString) {
        return units.stream()
                .filter(unit -> unit.check(unitString))
                .findFirst()
                .orElseThrow(BusinessErrorEnum.E001::thr);
    }
}
