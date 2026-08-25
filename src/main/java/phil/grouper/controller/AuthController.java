package phil.grouper.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import phil.grouper.entity.User;
import phil.grouper.repository.UserRepository;
import phil.grouper.services.JobQueueService;

@Controller
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JobQueueService jobQueueService;

    public AuthController(
            JobQueueService jobQueueService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.jobQueueService = jobQueueService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user) {
        // 1. Check if username already exists to avoid SQL errors
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "redirect:/register?error=UserExists";
        }
        // 2. Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 3. Save to MySQL (ID is already set by UUID.randomUUID())
        userRepository.save(user);
        return "redirect:/login?success";
    }

}
