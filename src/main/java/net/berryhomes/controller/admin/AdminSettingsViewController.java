package net.berryhomes.controller.admin;

import lombok.RequiredArgsConstructor;
import net.berryhomes.model.entity.SystemSetting;
import net.berryhomes.service.SystemSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsViewController {

    private final SystemSettingService settingService;

    @GetMapping
    public ModelAndView showSettings() {
        ModelAndView mav = new ModelAndView("admin/settings");
        mav.addObject("settings", settingService.getAllSettings());
        return mav;
    }

    @PostMapping("/save")
    public ModelAndView saveSettings(@RequestParam Map<String, String> allParams,
                                     RedirectAttributes redirectAttributes) {
        settingService.saveSettings(allParams);
        redirectAttributes.addFlashAttribute("successMessage", "System settings updated successfully!");
        return new ModelAndView("redirect:/admin/settings");
    }
}
