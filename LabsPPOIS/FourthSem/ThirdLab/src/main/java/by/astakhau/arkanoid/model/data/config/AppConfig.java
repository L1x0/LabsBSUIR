package by.astakhau.arkanoid.model.data.config;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AppConfig {
    private String appName;
    private String appVersion;
    private int width;
    private int height;
    private int pixelsPerMeter;
}
