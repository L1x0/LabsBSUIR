package by.astakhau.arkanoid.model.data.config;

import java.io.IOException;

public class ConfigManager implements ConfigService{
   private AppConfig appConfig;

    public void init() {
        ConfigReader configReader = new ConfigReader();
        try {
            appConfig = configReader.readFile();
        } catch (IOException e) {
            appConfig = new AppConfig();

            appConfig.setAppName("Arkanoid");
            appConfig.setAppVersion("1.0.0");
            appConfig.setHeight(600);
            appConfig.setWidth(600);
            appConfig.setPixelsPerMeter(32);
        }
    }

    @Override
    public AppConfig getConfig() {
        return appConfig;
    }
}
