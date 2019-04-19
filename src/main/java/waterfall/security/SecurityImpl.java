package waterfall.security;

import com.google.inject.Inject;
import waterfall.model.User;
import waterfall.service.UserService;

public class SecurityImpl implements Security {

    @Inject
    private UserService userService;

    @Override
    public User authorize(String username, String password) {
        User user = userService.findByUsername("username");

        if(!user.getPassword().equals(password)) {
            user = null;
        }

        return user;
    }
}
