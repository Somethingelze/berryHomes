//package net.berryhomes.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import net.berryhomes.model.dto.ContactDto;
//import net.berryhomes.service.ContactService;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/contacts")
//@RequiredArgsConstructor
//public class ContactController {
//
//    private final ContactService contactService;
//
//    @GetMapping("/all")
//    public Page<ContactDto> findAll(@PageableDefault Pageable pageable) {
//        return contactService.getAll(pageable);
//    }
//
//    @GetMapping("/name")
//    public Page<ContactDto> getByName(@RequestParam String name, @PageableDefault(size = 6,
//            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        return contactService.getByName(name, pageable);
//    }
//
//    @GetMapping("/email")
//    public Page<ContactDto> getByEmail(@RequestParam String email, @PageableDefault(size = 6,
//            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        return contactService.getByEmail(email, pageable);
//    }
//
//    @GetMapping("/phone")
//    public Page<ContactDto> getByPhone(@RequestParam String phone, @PageableDefault(size = 6,
//            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        return contactService.getByPhone(phone, pageable);
//    }
//
//    @GetMapping()
//    public Page<ContactDto> getAll(@PageableDefault(size = 6,
//            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        return contactService.getAll(pageable);
//    }
//
//    @PostMapping("/save")
//    public ContactDto saveContact(@Valid @RequestBody ContactDto contactDto) {
//        return contactService.saveContact(contactDto);
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public void deleteContact(@PathVariable("id") UUID id) {
//        contactService.deleteContact(id);
//    }
//}
