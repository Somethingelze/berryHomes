package net.berryhomes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.TenantWebsiteDocument;
import net.berryhomes.model.TenantWebsiteDocumentSlot;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.service.ContactService;
import net.berryhomes.service.TenantWebsiteDocumentService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantsViewController {

    private final ContactService contactService;
    private final TenantWebsiteDocumentService documentService;

    private static final String PORTAL_URL_VAL = "https://www.tenantcloud.com/";


    @GetMapping
    public ModelAndView showTenantCenter() {
        ModelAndView mav = new ModelAndView("tenants");
        mav.addObject("portalUrl", PORTAL_URL_VAL);
        addTenantDocuments(mav);

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
            addTenantDocuments(mav);
            return mav;
        }

        contactService.saveContact(dto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Thank you for your message! Our manager will contact you soon.");
        return new ModelAndView("redirect:/tenants");
    }

    @GetMapping("/documents/{slot}")
    public ResponseEntity<FileSystemResource> downloadTenantDocument(@PathVariable String slot) {
        TenantWebsiteDocumentSlot documentSlot;
        try { documentSlot = TenantWebsiteDocumentSlot.fromSlug(slot); }
        catch (IllegalArgumentException exception) { return ResponseEntity.notFound().build(); }
        TenantWebsiteDocument document = documentService.find(documentSlot);
        if (!document.published()) return ResponseEntity.notFound().build();
        FileSystemResource resource = documentService.resource(documentSlot);
        if (!resource.exists()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.originalFilename().replace("\"", "") + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(resource);
    }

    private void addTenantDocuments(ModelAndView mav) {
        mav.addObject("moveInGuide", published(TenantWebsiteDocumentSlot.MOVE_IN_GUIDE));
        mav.addObject("moveOutChecklist", published(TenantWebsiteDocumentSlot.MOVE_OUT_CHECKLIST));
        mav.addObject("residentHandbook", published(TenantWebsiteDocumentSlot.RESIDENT_HANDBOOK));
    }

    private TenantWebsiteDocument published(TenantWebsiteDocumentSlot slot) {
        TenantWebsiteDocument document = documentService.find(slot);
        return document.published() ? document : null;
    }
}
