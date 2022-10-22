package ua.com.alevel.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.comment.Comment;
import ua.com.alevel.model.dto.CommentForSave;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.service.comment.CommentService;
import ua.com.alevel.service.phone.PhoneService;
import ua.com.alevel.service.rating.RatingService;
import ua.com.alevel.service.user.UserDetailsServiceImpl;
import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentsController {
    private final CommentService commentService;
    private final ClientCheckService clientCheckService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PhoneService phoneService;
    private final RatingService ratingService;

    public CommentsController(CommentService commentService, ClientCheckService clientCheckService,
                              UserDetailsServiceImpl userDetailsServiceImpl, PhoneService phoneService, RatingService ratingService) {
        this.clientCheckService = clientCheckService;
        this.commentService = commentService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.phoneService = phoneService;
        this.ratingService = ratingService;
    }

    @GetMapping
    public String getPhonesForComment(Model model, @RequestParam(value = "successMessage") String successMessage) {
        RegisteredUser registeredUser = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName());
        List<ClientCheck> clientCheckList = clientCheckService.findAllClosedChecksForUserId(registeredUser.getId());
        List<Phone> phones = commentService.findAllPhonesWithoutComments(clientCheckList);

        model.addAttribute("successMessage", successMessage);
        model.addAttribute("phones", phones);
        return "phonesforcomments";
    }

    @GetMapping("/add-comment")
    public String addComment(Model model, @RequestParam(value = "id") String id) {
        Phone phone = phoneService.findById(id);
        CommentForSave commentForSave = new CommentForSave("", id, 0);

        model.addAttribute("phone", phone);
        model.addAttribute("commentForSave", commentForSave);
        model.addAttribute("infoText", " ");
        return "createcomment";
    }

    @PostMapping("/add-comment")
    public String saveComment(Model model, CommentForSave commentForSave) {
        if (commentForSave.getDescription().isBlank()) {
            return getErrorMsg(model, commentForSave, "Comment field must be not empty");
        }
        if(commentForSave.getRating() <=0 | commentForSave.getRating() >5){
            return getErrorMsg(model, commentForSave, "Incorrect rating");
        }

        RegisteredUser registeredUser = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName());
        Phone phone = phoneService.findById(commentForSave.getPhoneId());

        Comment comment = new Comment();
        comment.setPhone(phone);
        comment.setRegisteredUser(registeredUser);
        comment.setDescription(commentForSave.getDescription());
        commentService.save(comment);

        int oldNumberOfPoints = phone.getRating().getNumberOfPoints();
        int oldTotalPoints = phone.getRating().getTotalPoints();
        ratingService.updateRating(oldNumberOfPoints+1, oldTotalPoints+commentForSave.getRating(),
                phone.getRating().getId());

        return "redirect:/comments?successMessage=Comment saved successfully";
    }

    private String getErrorMsg(Model model, CommentForSave commentForSave, String msg){
        Phone phone = phoneService.findById(commentForSave.getPhoneId());
        commentForSave.setDescription("");
        commentForSave.setRating(0);

        model.addAttribute("phone", phone);
        model.addAttribute("commentForSave", commentForSave);
        model.addAttribute("infoText", msg);
        return "createcomment";
    }
}
