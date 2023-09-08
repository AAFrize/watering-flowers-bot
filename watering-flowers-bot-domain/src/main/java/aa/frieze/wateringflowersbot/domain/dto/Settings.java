package aa.frieze.wateringflowersbot.domain.dto;

import lombok.Data;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
public class Settings {

    private List<SettingDto> settings;

    @Data
    public static class SettingDto {
        private String title;
        private ChronoUnit periodUnit;
        private Long periodValue;
    }
}
