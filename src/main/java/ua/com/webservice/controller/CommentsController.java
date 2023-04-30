package ua.com.webservice.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.webservice.model.check.ClientCheck;
import ua.com.webservice.model.comment.Comment;
import ua.com.webservice.model.dto.CommentForSave;
import ua.com.webservice.model.phone.Phone;
import ua.com.webservice.model.user.RegisteredUser;
import ua.com.webservice.service.clientcheck.ClientCheckService;
import ua.com.webservice.service.comment.CommentService;
import ua.com.webservice.service.phone.PhoneInstanceService;
import ua.com.webservice.service.rating.RatingService;
import ua.com.webservice.service.user.UserDetailsServiceImpl;
import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.lang.NumberUtils.isNumber;

@Controller
@RequestMapping("/comments")
public class CommentsController {
    private final CommentService commentService;
    private final ClientCheckService clientCheckService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PhoneInstanceService phoneInstanceService;
    private final RatingService ratingService;

    public CommentsController(CommentService commentService, ClientCheckService clientCheckService,
                              UserDetailsServiceImpl userDetailsServiceImpl, PhoneInstanceService phoneInstanceService, RatingService ratingService) {
        this.clientCheckService = clientCheckService;
        this.commentService = commentService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.phoneInstanceService = phoneInstanceService;
        this.ratingService = ratingService;
    }

    @GetMapping
    public String getPhonesForComment(Model model, @RequestParam(value = "successMessage") String successMessage) {
        RegisteredUser registeredUser = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        List<ClientCheck> clientCheckList = clientCheckService.findAllClosedChecksForUserId(registeredUser.getId());
        List<Phone> allPhonesInDb = phoneInstanceService.findAllPhonesInDb();
        List<Phone> phones = commentService.findAllAvailablePhonesForComment(registeredUser, clientCheckList, allPhonesInDb);

        List<Integer> prices = new ArrayList<>();
        for (Phone phone : phones) {
            prices.add(phoneInstanceService.findPriceForPhoneId(phone.getId()));
        }

        model.addAttribute("successMessage", successMessage);
        model.addAttribute("phones", phones);
        model.addAttribute("prices", prices);
        return "phonesforcomments";
    }

    @GetMapping("/add-comment")
    public String addComment(Model model, @RequestParam(value = "id") String id) {
        Phone phone = phoneInstanceService.findByIdPhone(id);
        CommentForSave commentForSave = new CommentForSave("", id, "");

        model.addAttribute("price", phoneInstanceService.findPriceForPhoneId(id));
        model.addAttribute("phone", phone);
        model.addAttribute("commentForSave", commentForSave);
        model.addAttribute("infoText", " ");
        return "createcomment";
    }

    @PostMapping("/add-comment")
    public String saveComment(Model model, CommentForSave commentForSave) {
        if (commentForSave.getDescription().isBlank()) {
            return getErrorMsg(model, commentForSave, "Поле коментаря не повинно бути порожнім");
        }
        if (commentForSave.getRating().isBlank()) {
            return getErrorMsg(model, commentForSave, "Поле оцінки не повинно бути порожнім");
        }
        if (!isNumber(commentForSave.getRating())) {
            return getErrorMsg(model, commentForSave, "Неправильна оцінка");
        }
        if (Integer.parseInt(commentForSave.getRating()) <= 0 | Integer.parseInt(commentForSave.getRating()) > 5) {
            return getErrorMsg(model, commentForSave, "Неправильна оцінка");
        }

        RegisteredUser registeredUser = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Phone phone = phoneInstanceService.findByIdPhone(commentForSave.getPhoneId());

        Comment comment = new Comment();
        comment.setPhone(phone);
        comment.setRegisteredUser(registeredUser);
        comment.setDescription(commentForSave.getDescription());
        commentService.save(comment);

        int oldNumberOfPoints = phone.getRating().getNumberOfPoints();
        int oldTotalPoints = phone.getRating().getTotalPoints();
        ratingService.updateRating(oldNumberOfPoints + 1, oldTotalPoints + Integer.parseInt(commentForSave.getRating()),
                phone.getRating().getId());

        return getPhonesForComment(model, "Коментар успішно збережено");
    }

    private String getErrorMsg(Model model, CommentForSave commentForSave, String msg) {
        Phone phone = phoneInstanceService.findByIdPhone(commentForSave.getPhoneId());
        commentForSave.setDescription("");
        commentForSave.setRating("");

        model.addAttribute("phone", phone);
        model.addAttribute("commentForSave", commentForSave);
        model.addAttribute("infoText", msg);
        return "createcomment";
    }
}