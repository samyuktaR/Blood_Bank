package com.bloodbank.web;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Donor;
import com.bloodbank.matching.MatchingService;
import com.bloodbank.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/donors")
public class DonorController {

    static final List<Integer> RADIUS_PRESETS_KM = List.of(10, 25, 50, 100);

    private final DonorService donorService;
    private final MatchingService matchingService;
    private final Clock clock;

    public DonorController(DonorService donorService, MatchingService matchingService, Clock clock) {
        this.donorService = donorService;
        this.matchingService = matchingService;
        this.clock = clock;
    }

    @InitBinder("donor")
    void restrictBinding(WebDataBinder binder) {
        binder.setDisallowedFields("id", "createdAt");
    }

    @ModelAttribute
    void commonAttributes(Model model) {
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("today", LocalDate.now(clock));
        model.addAttribute("minDays", matchingService.properties().minDaysBetweenDonations());
        model.addAttribute("radiusPresets", RADIUS_PRESETS_KM);
    }

    @GetMapping
    public String list(@RequestParam(required = false) BloodGroup bloodGroup, Model model) {
        model.addAttribute("donors", donorService.search(bloodGroup));
        model.addAttribute("selectedGroup", bloodGroup);
        return "donors/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("donor", new Donor());
        return "donors/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("donor") Donor donor, BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "donors/form";
        }
        Donor saved = donorService.create(donor);
        redirect.addFlashAttribute("message", "Thank you, " + saved.getName() + ". You're registered as a donor.");
        return "redirect:/donors/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @RequestParam(required = false) Double radiusKm, Model model) {
        Donor donor = donorService.get(id);
        double radius = matchingService.resolveRadius(radiusKm);
        model.addAttribute("donor", donor);
        model.addAttribute("radiusKm", radius);
        model.addAttribute("matches", matchingService.findRecipientsFor(donor, radius));
        model.addAttribute("eligible", matchingService.isEligibleToday(donor));
        model.addAttribute("nextEligibleDate", matchingService.nextEligibleDate(donor));
        return "donors/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("donor", donorService.get(id));
        return "donors/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("donor") Donor donor,
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            donor.setId(id);
            return "donors/form";
        }
        donorService.update(id, donor);
        redirect.addFlashAttribute("message", "Donor details updated.");
        return "redirect:/donors/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        donorService.delete(id);
        redirect.addFlashAttribute("message", "Donor removed.");
        return "redirect:/donors";
    }
}