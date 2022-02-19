package springbook.user.domain;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;
import java.util.Objects;

public class UserServiceImpl implements UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MAX_RECOMMEND_FOR_GOLD = 30;
    private UserDao userDao;

    private MailSender mailSender;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void add(User user) {
        if(Objects.isNull(user.getLevel()))
            user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for(User user : users)
            if(canUpgradeLevel(user))
                upgradeLevel(user);
    }

    private boolean canUpgradeLevel(User user) {
        return switch (user.getLevel()) {
            case BASIC -> user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
            case SILVER -> user.getRecommend() >= MAX_RECOMMEND_FOR_GOLD;
            case GOLD -> false;
        };
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("admin@bb.cc");
        mailMessage.setSubject("title");
        mailMessage.setText("contents");
        mailSender.send(mailMessage);
    }
}
