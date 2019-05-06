package waterfall.security;

import waterfall.model.User;

public interface Security {
    public User authorize(String username, String password);

    public void logout(User user);
}
