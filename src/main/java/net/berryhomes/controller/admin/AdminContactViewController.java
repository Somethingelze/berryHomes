package net.berryhomes.controller.admin;

import lombok.RequiredArgsConstructor;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.model.entity.Contact;
import net.berryhomes.service.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
public class AdminContactViewController {

    private final ContactService contactService;

    @GetMapping
    public ModelAndView listContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ContactType typeFilter,
            @RequestParam(required = false) ContactStatus statusFilter) {

        ModelAndView mav = new ModelAndView("admin/contacts");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ContactDto> contactPage;

        if (search != null && !search.trim().isEmpty()) {
            contactPage = contactService.searchContacts(search, pageable);

            mav.addObject("currentSearch", search.trim());
            mav.addObject("currentTypeFilter", "");
            mav.addObject("currentStatusFilter", "");
        }
        else {
            contactPage = contactService.filterContacts(typeFilter, statusFilter, pageable);

            mav.addObject("currentSearch", "");
            mav.addObject("currentTypeFilter", typeFilter != null ? typeFilter.name() : "");
            mav.addObject("currentStatusFilter", statusFilter != null ? statusFilter.name() : "");
        }

        mav.addObject("contactPage", contactPage);
        mav.addObject("statuses", ContactStatus.values());

        return mav;
    }

    @PostMapping("/{id}/status")
    public ModelAndView updateStatus(@PathVariable UUID id,
                                     @RequestParam ContactStatus status,
                                     RedirectAttributes redirectAttributes) {
        contactService.updateContactStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Status updated successfully!");
        return new ModelAndView("redirect:/admin/contacts");
    }


    @PostMapping("/{id}/delete")
    public ModelAndView deleteContact(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        contactService.deleteContact(id);
        redirectAttributes.addFlashAttribute("successMessage", "Contact deleted successfully!");
        return new ModelAndView("redirect:/admin/contacts");
    }
}

