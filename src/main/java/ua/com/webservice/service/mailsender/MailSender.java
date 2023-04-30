package ua.com.webservice.service.mailsender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import ua.com.webservice.model.dto.OrderInfoForMail;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.thymeleaf.context.Context;

@Service
public class MailSender {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    public MailSender(JavaMailSender javaMailSender, @Qualifier("mailNotifierTemplateEngine") TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMailPurchaseNotice(String emailTo, String subject, OrderInfoForMail orderInfoForMail) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(username);
            mimeMessageHelper.setTo(emailTo);
            mimeMessageHelper.setText(getContentFromTemplate(orderInfoForMail), true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String getContentFromTemplate(OrderInfoForMail orderInfoForMail) {
        StringBuffer content = new StringBuffer();
        Map<String, Object> model = new HashMap<>();

        model.put("clientCheck", orderInfoForMail.getClientCheck());
        model.put("created", orderInfoForMail.getCreated());
        model.put("totalPrice", orderInfoForMail.getTotalPrice());
        model.put("flag", orderInfoForMail.isFlag());
        model.put("user", orderInfoForMail.getRegisteredUser());

        try {
            Context context = new Context();
            context.setVariables(model);
            content.append(templateEngine.process("classpath:/templates/checkinfomail.html", context));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return content.toString();
    }
}