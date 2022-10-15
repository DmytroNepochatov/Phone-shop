package ua.com.alevel.service.comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.com.alevel.mapper.CommentMapper;
import ua.com.alevel.model.dto.CommentForPhone;
import ua.com.alevel.model.dto.PhoneForMainView;
import ua.com.alevel.repository.comment.CommentRepository;
import java.util.ArrayList;
import java.util.Collection;
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

    public Object[] findCommentsForPhone(PhoneForMainView phoneForMainView, int page) {
        List<CommentForPhone> commentForPhoneList = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_COMMENTS);

        CommentMapper.mapCommentToCommentForPhone(commentRepository, commentForPhoneList, phoneForMainView, pageable);
        int commentsCount = getPagesCountForList(commentForPhoneList);

        return new Object[]{commentForPhoneList, commentsCount};
    }

    private int getPagesCountForList(Collection<CommentForPhone> commentForPhones) {
        int commentsCount = commentForPhones.size();

        return (commentsCount % NEED_COMMENTS == 0) ? commentsCount / NEED_COMMENTS : (commentsCount / NEED_COMMENTS) + 1;
    }
}
