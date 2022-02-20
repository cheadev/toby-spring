package springbook.user.domain;

public class TestUserServiceImpl extends UserServiceImpl {
    private String id = "id4";

    @Override
    protected void upgradeLevel(User user) {
        if(id.equals(user.getId()))
            throw new RuntimeException();
        super.upgradeLevel(user);
    }
}
