package net.berryhomes.controller;

import lombok.RequiredArgsConstructor;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.service.ContactService;
import net.berryhomes.service.ManagedDocumentService;
import net.berryhomes.service.SystemSettingService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {
    private final SystemSettingService settingService;
    private final ContactService contactService;
    private final ManagedDocumentService documentService;

    @ModelAttribute("settings")
    public Map<String, String> populateSettings() {
        Map<String, String> localized = new HashMap<>(settingService.getAllSettings());
        if ("ru".equals(LocaleContextHolder.getLocale().getLanguage())) {
            copyLocalizedValue(localized, "site_office");
            copyLocalizedValue(localized, "site_hours");
            copyLocalizedValue(localized, "license_text");
            copyLocalizedValue(localized, "partner_text");
            copyLocalizedValue(localized, "footer_tagline");
        }
        return localized;
    }

    @ModelAttribute("currentLanguage")
    public String currentLanguage() { return LocaleContextHolder.getLocale().getLanguage(); }

    @ModelAttribute("googleMapsUrl")
    public String googleMapsUrl() {
        return settingService.getAllSettings().getOrDefault("google_maps_url",
                "https://www.google.com/maps/search/?api=1&query=Pittsburgh%2C+PA+15222");
    }

    @ModelAttribute("newContactCount")
    public long newContactCount() { return isStaff() ? contactService.countByStatus(ContactStatus.NEW) : 0; }

    @ModelAttribute("unsortedDocumentCount")
    public long unsortedDocumentCount() { return isStaff() ? documentService.countUnsorted() : 0; }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private boolean isStaff() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_MANAGER".equals(a.getAuthority()));
    }

    private void copyLocalizedValue(Map<String, String> settings, String key) {
        String value = settings.get(key + "_ru");
        if (value != null && !value.isBlank()) settings.put(key, value);
    }
}
