package fi.vincit.multiusertest;

import org.springframework.security.access.AccessDeniedException;

public class TestService {
    public void throwAccessDenied() {
        throw new AccessDeniedException("Denied");
    }

    public void noThrow() {
    }
}
