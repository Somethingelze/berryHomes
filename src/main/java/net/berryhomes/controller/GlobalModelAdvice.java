package net.berryhomes.controller;

import lombok.RequiredArgsConstructor;
import net.berryhomes.service.SystemSettingService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final SystemSettingService settingService;

    @ModelAttribute("settings")
    public Map<String, String> populateSettings() {
        return settingService.getAllSettings();
    }
}