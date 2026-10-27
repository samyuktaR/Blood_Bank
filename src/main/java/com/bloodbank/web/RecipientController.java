package com.bloodbank.web;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.RequestStatus;
import com.bloodbank.domain.Urgency;
import com.bloodbank.matching.MatchingService;
import com.bloodbank.service.RecipientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Clock;
import java.time.LocalDate;

@Controller
@RequestMapping("/recipients")
public class RecipientController {

    private final RecipientService recipientService;
    private final MatchingService matchingService;
    private final Clock clock;

    public RecipientController(RecipientService recipientService, MatchingService matchingService, Clock clock) {
        this.recipientService = recipientService;
        this.matchingService = matchingService;
        this.clock = clock;
    }

    @InitBinder("recipient")
    void restrictBinding(WebDataBinder binder) {
        binder.setDisallowedFields("id", "createdAt", "status");
    }

    @ModelAttribute
    void commonAttributes(Model model) {
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("urgencies", Urgency.values());
        model.addAttribute("statuses", RequestStatus.values());
        model.addAttribute("today", LocalDate.now(clock));
        model.addAttribute("minDays", matchingService.properties().minDaysBetweenDonations());
        model.addAttribute("radiusPresets", DonorController.RADIUS_PRESETS_KM);
    }

    @GetMapping
    public String list(@RequestParam(required = false) RequestStatus status,
                       @RequestParam(required = false) BloodGroup bloodGroup, Model model) {
        model.addAttribute("recipients", recipientService.search(status, bloodGroup));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedGroup", bloodGroup);
        return "recipients/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("recipient", new Recipient());
        return "recipients/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("recipient") Recipient recipient, BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "recipients/form";
        }
        Recipient saved = recipientService.create(recipient);
        redirect.addFlashAttribute("message", "Request saved. Here are the compatible donors nearby.");
        return "redirect:/recipients/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @RequestParam(required = false) Double radiusKm, Model model) {
        Recipient recipient = recipientService.get(id);
        model.addAttribute("recipient", recipient);
        model.addAttribute("result", matchingService.findDonorsFor(recipient, radiusKm));
        return "recipients/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("recipient", recipientService.get(id));
        return "recipients/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("recipient") Recipient recipient,
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            recipient.setId(id);
            return "recipients/form";
        }
        recipientService.update(id, recipient);
        redirect.addFlashAttribute("message", "Request updated.");
        return "redirect:/recipients/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam RequestStatus status,
                               RedirectAttributes redirect) {
        recipientService.updateStatus(id, status);
        redirect.addFlashAttribute("message", "Request marked as " + status.getLabel().toLowerCase() + ".");
        return "redirect:/recipients/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        recipientService.delete(id);
        redirect.addFlashAttribute("message", "Request deleted.");
        return "redirect:/recipients";
    }
}