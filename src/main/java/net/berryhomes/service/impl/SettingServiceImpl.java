package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import net.berryhomes.aop.Loggable;
import net.berryhomes.model.entity.Setting;
import net.berryhomes.repository.SettingRepository;
import net.berryhomes.service.SystemSettingService;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Loggable
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
        String updatedBy = SecurityContextHolder.getContext().getAuthentication().getName();

        settings.forEach((key, value) -> {
            if (!key.startsWith("_")) {
                Setting setting = settingRepository.findById(key)
                        .orElse(Setting.builder().key(key).build());
                setting.setValue(value);
                setting.setUpdatedAt(ZonedDateTime.now());
                setting.setUpdatedBy(updatedBy);
                settingRepository.save(setting);
            }
        });
    }
}
