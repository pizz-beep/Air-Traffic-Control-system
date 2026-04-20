package com.atc.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int status = statusCode != null ? Integer.parseInt(statusCode.toString()) : 500;

        model.addAttribute("status", status);
        model.addAttribute("requestUri", requestUri != null ? requestUri.toString() : "unknown");

        if (status == HttpStatus.FORBIDDEN.value()) {
            model.addAttribute("title", "Access Denied");
            model.addAttribute("message",
                "You don't have permission to access this resource. " +
                "Please contact an administrator if you believe this is a mistake.");
            model.addAttribute("icon", "🔒");
            model.addAttribute("color", "var(--accent-red)");
        } else if (status == HttpStatus.NOT_FOUND.value()) {
            model.addAttribute("title", "Page Not Found");
            model.addAttribute("message",
                "The page you're looking for doesn't exist or has been moved.");
            model.addAttribute("icon", "🔍");
            model.addAttribute("color", "var(--accent-amber)");
        } else {
            model.addAttribute("title", "Something Went Wrong");
            model.addAttribute("message",
                errorMessage != null ? errorMessage.toString() :
                "An unexpected error occurred. Please try again later.");
            model.addAttribute("icon", "⚠");
            model.addAttribute("color", "var(--accent-amber)");
        }

        return "error/error";
    }
}
