package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.entity.Settings;
import fashion.coin.wallet.back.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {

    private SettingsRepository settingsRepository;

    public String get(String key) {
        try {
            Settings settings = settingsRepository.findOneByKey(key);
            return settings.getValue();
        } catch (Exception e) {
            System.out.println(e.getMessage());
//            e.printStackTrace();
            return null;
        }
    }

    public Settings set(String key, String value) {
        Settings settings = settingsRepository.findOneByKey(key);
        if(settings==null) settings = new Settings(key, value);
        else settings.setValue(value);
        settingsRepository.save(settings);
        return settings;
    }


    @Autowired
    public void setSettingsRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public boolean isEmpty() {
        List<Settings> settingsList = settingsRepository.findAll();
        return (settingsList == null || settingsList.isEmpty());
    }
}
