package fi.vincit.multiusertest.util;

public class SecurityUtil {

    private static User loggedInUser;

    public static void logInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

}
