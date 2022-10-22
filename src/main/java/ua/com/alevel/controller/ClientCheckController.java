package ua.com.alevel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.service.clientcheck.ClientCheckService;

@Controller
@RequestMapping("/client-check")
public class ClientCheckController {
    private final ClientCheckService clientCheckService;

    public ClientCheckController(ClientCheckService clientCheckService) {
        this.clientCheckService = clientCheckService;
    }

    @GetMapping
    public String getClientCheckById(Model model, @RequestParam(value = "id") String id) {
        ClientCheck clientCheck = clientCheckService.findById(id);

        model.addAttribute("clientCheck", clientCheck);
        return "checkinfo";
    }
}
