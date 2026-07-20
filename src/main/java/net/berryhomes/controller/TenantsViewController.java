package net.berryhomes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.service.ContactService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantsViewController {

    private final ContactService contactService;

    private static final String PORTAL_URL_VAL = "https://www.tenantcloud.com/";


    @GetMapping
    public ModelAndView showTenantCenter() {
        ModelAndView mav = new ModelAndView("tenants");
        mav.addObject("portalUrl", PORTAL_URL_VAL);

        ContactDto emptyForm = ContactDto.builder()
                .type(ContactType.TENANT)
                .status(ContactStatus.NEW)
                .build();
        mav.addObject("contactDto", emptyForm);

        return mav;
    }

    @PostMapping("/message")
    public ModelAndView handleTenantMessage(@ModelAttribute("contactDto") @Valid ContactDto dto,
                                           BindingResult bindingResult,
                                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("tenants");
            mav.addObject("portalUrl", PORTAL_URL_VAL);
            return mav;
        }

        contactService.saveContact(dto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Thank you for your message! Our manager will contact you soon.");
        return new ModelAndView("redirect:/tenants");
    }
}
