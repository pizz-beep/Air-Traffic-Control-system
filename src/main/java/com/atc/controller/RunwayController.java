package com.atc.controller;

import com.atc.model.Runway;
import com.atc.model.Runway.RunwayStatus;
import com.atc.service.RunwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/runways")
@RequiredArgsConstructor
public class RunwayController {

    private final RunwayService runwayService;

    // GET /runways  → list all runways with stats
    @GetMapping
    public String listRunways(Model model) {
        model.addAttribute("runways",       runwayService.getAllRunways());
        model.addAttribute("availableCount", runwayService.countByStatus(RunwayStatus.AVAILABLE));
        model.addAttribute("occupiedCount",  runwayService.countByStatus(RunwayStatus.OCCUPIED));
        model.addAttribute("maintenanceCount", runwayService.countByStatus(RunwayStatus.MAINTENANCE));
        model.addAttribute("closedCount",    runwayService.countByStatus(RunwayStatus.CLOSED));
        model.addAttribute("statuses",       RunwayStatus.values());
        return "runway/list";
    }

    // GET /runways/assign  → show assignment form
    @GetMapping("/assign")
    public String assignForm(Model model) {
        model.addAttribute("available", runwayService.getAvailableRunways());
        return "runway/assign";
    }

    // POST /runways/assign  → allocate runway to flight
    @PostMapping("/assign")
    public String assignRunway(@RequestParam String runwayId,
                               @RequestParam String flightId,
                               RedirectAttributes ra) {
        try {
            runwayService.allocateRunway(runwayId, flightId);
            ra.addFlashAttribute("successMsg",
                "Runway " + runwayId + " assigned to flight " + flightId + " successfully.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/runways";
    }

    // POST /runways/{id}/release  → release runway
    @PostMapping("/{runwayId}/release")
    public String releaseRunway(@PathVariable String runwayId, RedirectAttributes ra) {
        try {
            runwayService.releaseRunway(runwayId);
            ra.addFlashAttribute("successMsg", "Runway " + runwayId + " released successfully.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/runways";
    }

    // GET /runways/create → show create runway form (Admin)
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("statuses", RunwayStatus.values());
        return "runway/create";
    }

    // POST /runways/create → create a runway (Admin)
    @PostMapping("/create")
    public String createRunway(@RequestParam String runwayId,
                               @RequestParam String direction,
                               @RequestParam(required = false) Integer lengthMeters,
                               RedirectAttributes ra) {
        try {
            runwayService.createRunway(runwayId, direction, lengthMeters);
            ra.addFlashAttribute("successMsg", "Runway " + runwayId + " created successfully.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/runways";
    }

    // POST /runways/{id}/status → change runway status (Admin)
    @PostMapping("/{runwayId}/status")
    public String updateStatus(@PathVariable String runwayId,
                               @RequestParam RunwayStatus status,
                               RedirectAttributes ra) {
        try {
            runwayService.setRunwayStatus(runwayId, status);
            ra.addFlashAttribute("successMsg",
                "Runway " + runwayId + " status updated to " + status + ".");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/runways";
    }

    // POST /runways/{id}/delete → delete runway (Admin)
    @PostMapping("/{runwayId}/delete")
    public String deleteRunway(@PathVariable String runwayId, RedirectAttributes ra) {
        try {
            runwayService.deleteRunway(runwayId);
            ra.addFlashAttribute("successMsg", "Runway " + runwayId + " deleted.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/runways";
    }
}
