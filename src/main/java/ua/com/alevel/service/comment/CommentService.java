package ua.com.alevel.service.comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.com.alevel.mapper.CommentMapper;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.comment.Comment;
import ua.com.alevel.model.dto.CommentForPhone;
import ua.com.alevel.model.dto.CommentForPhoneList;
import ua.com.alevel.model.dto.PhoneForMainView;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.phone.PhoneInstance;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.repository.comment.CommentRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
    private static final int NEED_COMMENTS = 20;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
        LOGGER.info("Comment for phone {} saved", comment.getPhone().getId());
    }

    public CommentForPhoneList findCommentsForPhone(PhoneForMainView phoneForMainView, int page) {
        List<CommentForPhone> commentForPhoneList = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_COMMENTS);

        CommentMapper.mapCommentToCommentForPhone(commentRepository, commentForPhoneList, phoneForMainView, pageable);
        int commentsCount = getPagesCountForList(phoneForMainView);

        return new CommentForPhoneList(commentForPhoneList, commentsCount);
    }

    private int getPagesCountForList(PhoneForMainView phoneForMainView) {
        int commentsCount = commentRepository.countCommentsForPhone(phoneForMainView.getBrand(), phoneForMainView.getName(),
                phoneForMainView.getSeries(), phoneForMainView.getAmountOfBuiltInMemory(), phoneForMainView.getAmountOfRam()).get();

        return (commentsCount % NEED_COMMENTS == 0) ? commentsCount / NEED_COMMENTS : (commentsCount / NEED_COMMENTS) + 1;
    }

    public List<Phone> findAllAvailablePhonesForComment(RegisteredUser user, List<ClientCheck> clientCheckList, List<Phone> allPhonesInDb) {
        List<Phone> resultPhones = new ArrayList<>();
        List<PhoneInstance> allPhonesFromChecks = new ArrayList<>();
        clientCheckList.forEach(clientCheck -> allPhonesFromChecks.addAll(clientCheck.getPhoneInstances()));

        for (int i = 0; i < allPhonesInDb.size(); i++) {
            for (PhoneInstance allPhonesFromCheck : allPhonesFromChecks) {
                if (allPhonesInDb.get(i).equals(allPhonesFromCheck.getPhone())) {
                    if (commentRepository.findCommentForPhoneCharacteristicsAndRegisteredUser(
                            allPhonesFromCheck.getPhone().getPhoneDescription().getBrand(),
                            allPhonesFromCheck.getPhone().getPhoneDescription().getName(),
                            allPhonesFromCheck.getPhone().getPhoneDescription().getSeries(),
                            allPhonesFromCheck.getPhone().getAmountOfBuiltInMemory(),
                            allPhonesFromCheck.getPhone().getAmountOfRam(),
                            user
                    ).isEmpty()) {
                        boolean check = true;
                        for (Phone resultPhone : resultPhones) {
                            if (resultPhone.equals(allPhonesInDb.get(i))) {
                                check = false;
                            }
                        }

                        if (check) {
                            resultPhones.add(allPhonesInDb.get(i));
                        }
                    }
                }
            }
        }

        return resultPhones;
    }
}
