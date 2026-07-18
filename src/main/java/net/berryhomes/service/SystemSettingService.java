package net.berryhomes.service;

import java.util.Map;

public interface SystemSettingService {
    Map<String, String> getAllSettings();
    void saveSettings(Map<String, String> settings);
}