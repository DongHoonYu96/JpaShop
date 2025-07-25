package jpabook.jpashop.service.stock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void 메일_전송_테스트() {
        // given
        String to = "test@example.com";
        String subject = "테스트 메일";
        String text = "테스트 본문입니다.";

        // when
        emailService.sendMail(to, subject, text);

        // then
        ArgumentCaptor<SimpleMailMessage> argument = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(argument.capture());

        SimpleMailMessage sentMessage = argument.getValue();
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(text, sentMessage.getText());
    }
}
