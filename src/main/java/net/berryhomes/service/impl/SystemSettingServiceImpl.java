package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import net.berryhomes.model.entity.SystemSetting;
import net.berryhomes.repository.SystemSettingRepository;
import net.berryhomes.service.SystemSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getAllSettings() {
        return repository.findAll().stream()
                .collect(Collectors.toMap(SystemSetting::getKey, SystemSetting::getValue));
    }

    @Override
    @Transactional
    public void saveSettings(Map<String, String> settings) {
        settings.forEach((key, value) -> {
            if (!key.startsWith("_")) {
                SystemSetting setting = repository.findById(key)
                        .orElse(SystemSetting.builder().key(key).build());
                setting.setValue(value);
                setting.setUpdatedAt(ZonedDateTime.now());
                repository.save(setting);
            }
        });
    }
}
