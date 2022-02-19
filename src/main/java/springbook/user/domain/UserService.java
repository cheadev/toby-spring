package springbook.user.domain;

import java.util.List;
import java.util.Objects;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MAX_RECOMMEND_FOR_GOLD = 30;
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
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
        switch (user.getLevel()) {
            case BASIC : return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
            case SILVER: return user.getRecommend() >= MAX_RECOMMEND_FOR_GOLD;
            case GOLD: return false;
            default: throw new IllegalArgumentException();
        }
    }

    private void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }
}
