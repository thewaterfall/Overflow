package waterfall.injection;

import com.google.inject.AbstractModule;
import waterfall.dao.*;
import waterfall.protocol.CommandUtil;
import waterfall.protocol.JSONCommandUtil;
import waterfall.security.Security;
import waterfall.security.SecurityImpl;
import waterfall.service.*;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserDAO.class).to(UserDAOImpl.class);
        bind(LobbyDAO.class).to(LobbyDAOImpl.class);
        bind(GameStatDAO.class).to(GameStatDAOImpl.class);
        bind(GameTypeDAO.class).to(GameTypeDAOImpl.class);

        bind(UserService.class).to(UserServiceImpl.class);
        bind(LobbyService.class).to(LobbyServiceImpl.class);
        bind(GameStatService.class).to(GameStatServiceImpl.class);
        bind(GameTypeService.class).to(GameTypeServiceImpl.class);

        bind(Security.class).to(SecurityImpl.class);

        bind(CommandUtil.class).to(JSONCommandUtil.class);
    }
}