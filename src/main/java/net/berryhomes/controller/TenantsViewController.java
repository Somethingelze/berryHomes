package net.berryhomes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.DocumentCategory;
import net.berryhomes.model.entity.ManagedDocument;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.service.ContactService;
import net.berryhomes.service.FileStorageService;
import net.berryhomes.service.ManagedDocumentService;
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
    private final ManagedDocumentService documentService;
    private final FileStorageService fileStorageService;

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
        DocumentCategory category = switch (slot) {
            case "move-in-guide" -> DocumentCategory.TENANT_MOVE_IN_GUIDE;
            case "move-out-checklist" -> DocumentCategory.TENANT_MOVE_OUT_CHECKLIST;
            case "resident-handbook" -> DocumentCategory.TENANT_RESIDENT_HANDBOOK;
            default -> null;
        };
        if (category == null) return ResponseEntity.notFound().build();
        return documentService.findActive(category).map(this::downloadResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<FileSystemResource> downloadResponse(ManagedDocument document) {
        FileSystemResource resource = new FileSystemResource(fileStorageService.resolveFile(document.getStoredPath()));
        if (!resource.exists()) return ResponseEntity.notFound().build();
        String safeFilename = document.getOriginalFilename().replace("\"", "");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(resource);
    }

    private void addTenantDocuments(ModelAndView mav) {
        mav.addObject("moveInGuide", documentService.findActive(DocumentCategory.TENANT_MOVE_IN_GUIDE).orElse(null));
        mav.addObject("moveOutChecklist", documentService.findActive(DocumentCategory.TENANT_MOVE_OUT_CHECKLIST).orElse(null));
        mav.addObject("residentHandbook", documentService.findActive(DocumentCategory.TENANT_RESIDENT_HANDBOOK).orElse(null));
    }
}
