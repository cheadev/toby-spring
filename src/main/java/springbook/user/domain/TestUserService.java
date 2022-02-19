package springbook.user.domain;

public class TestUserService extends UserService {
    private String id;

    public TestUserService(String id) {
        this.id = id;
    }

    @Override
    protected void upgradeLevel(User user) {
        if(id.equals(user.getId()))
            throw new RuntimeException();
        super.upgradeLevel(user);
    }
}
