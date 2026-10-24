package phil.grouper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/welcome")
    public String welcomePage(Model model) {
        // Adding data to the "Model" makes it available in the HTML
        model.addAttribute("username", "Java Developer");
        model.addAttribute("status", "Active");
        // This returns the name of the HTML file (without the .html extension)
        return "index"; 
    }
}