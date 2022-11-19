package ua.com.alevel.service.comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.mapper.CommentMapper;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.comment.Comment;
import ua.com.alevel.model.dto.CommentForPhone;
import ua.com.alevel.model.dto.CommentForPhoneList;
import ua.com.alevel.model.dto.PhoneForMainView;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.repository.comment.CommentRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
    private static final int NEED_COMMENTS = 5;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
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

    public List<Phone> findAllPhonesWithoutComments(List<ClientCheck> clientCheckList) {
        List<Phone> result = new ArrayList<>();

        clientCheckList.forEach(clientCheck -> {
            clientCheck.getPhones().forEach(phone -> {
                if (commentRepository.findCommentForPhoneId(phone.getId()).isEmpty()) {
                    result.add(phone);
                }
            });
        });

        return result;
    }
}
