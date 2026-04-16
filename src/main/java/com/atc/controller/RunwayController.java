package com.atc.controller;

import com.atc.service.RunwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/runways")
@RequiredArgsConstructor
public class RunwayController {

    private final RunwayService runwayService;

    // GET /runways  → list all runways
    @GetMapping
    public String listRunways(Model model) {
        model.addAttribute("runways", runwayService.getAllRunways());
        return "runway/list";
    }

    // GET /runways/assign  → show assignment form
    @GetMapping("/assign")
    public String assignForm(Model model) {
        model.addAttribute("available",
            runwayService.getAvailableRunways());
        return "runway/assign";
    }

    // POST /runways/assign  → allocate runway to flight
    @PostMapping("/assign")
    public String assignRunway(@RequestParam String runwayId,
                              @RequestParam String flightId) {
        runwayService.allocateRunway(runwayId, flightId);
        return "redirect:/runways";
    }

    // POST /runways/{id}/release  → release runway
    @PostMapping("/{runwayId}/release")
    public String releaseRunway(@PathVariable String runwayId) {
        runwayService.releaseRunway(runwayId);
        return "redirect:/runways";
    }
}


