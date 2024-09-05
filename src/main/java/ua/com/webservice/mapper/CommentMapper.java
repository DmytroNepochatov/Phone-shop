package ua.com.webservice.mapper;

import org.springframework.data.domain.Pageable;
import ua.com.webservice.model.dto.CommentForPhone;
import ua.com.webservice.model.dto.PhoneForMainView;
import ua.com.webservice.repository.comment.CommentRepository;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public final class CommentMapper {
    private static final String DATE_PATTERN = "dd.M.yyyy";

    private CommentMapper() {
    }

    public static List<CommentForPhone> mapCommentToCommentForPhone(CommentRepository commentRepository, List<CommentForPhone> commentForPhoneList, PhoneForMainView phoneForMainView, Pageable pageable) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        commentRepository.findAllCommentsForPhone(phoneForMainView.getBrand(), phoneForMainView.getName(), phoneForMainView.getSeries(),
                phoneForMainView.getAmountOfBuiltInMemory(), phoneForMainView.getAmountOfRam(), pageable).forEach(comment ->
                commentForPhoneList.add(new CommentForPhone(comment.getRegisteredUser().getLastName(), comment.getRegisteredUser().getFirstName(),
                        comment.getDescription(), String.valueOf(comment.getGrade()), formatter.format(comment.getCreated()))));

        return commentForPhoneList;
    }
}
