package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import net.berryhomes.model.entity.Setting;
import net.berryhomes.repository.SettingRepository;
import net.berryhomes.service.SystemSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SystemSettingService {

    private final SettingRepository settingRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getAllSettings() {
        return settingRepository.findAll().stream()
                .collect(Collectors.toMap(Setting::getKey, Setting::getValue));
    }

    @Override
    @Transactional
    public void saveSettings(Map<String, String> settings) {

        settings.forEach((key, value) -> {
            if (!key.startsWith("_")) {
                Setting setting = settingRepository.findById(key)
                        .orElse(Setting.builder().key(key).build());
                setting.setValue(value);
                setting.setUpdatedAt(ZonedDateTime.now());
                settingRepository.save(setting);
            }
        });
    }
}
