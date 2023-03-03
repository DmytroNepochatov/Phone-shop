package ua.com.alevel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.service.phone.PhoneInstanceService;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Controller
@RequestMapping("/client-check")
public class ClientCheckController {
    private final ClientCheckService clientCheckService;
    private final PhoneInstanceService phoneInstanceService;

    public ClientCheckController(ClientCheckService clientCheckService, PhoneInstanceService phoneInstanceService) {
        this.clientCheckService = clientCheckService;
        this.phoneInstanceService = phoneInstanceService;
    }

    @GetMapping
    public String getClientCheckById(Model model, @RequestParam(value = "id") String id) {
        ClientCheck clientCheck = clientCheckService.findById(id);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.M.yyyy HH:mm:ss", Locale.ENGLISH);

        if (clientCheck.getClosedDate() != null) {
            model.addAttribute("closedDate", formatter.format(clientCheck.getClosedDate()));
        }

        model.addAttribute("created", formatter.format(clientCheck.getCreated()));
        model.addAttribute("clientCheck", clientCheck);
        model.addAttribute("totalPrice", phoneInstanceService.findPriceForClientCheckId(clientCheck.getId()));
        return "checkinfo";
    }
}
