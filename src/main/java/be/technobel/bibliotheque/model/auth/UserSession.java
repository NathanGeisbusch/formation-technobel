package be.technobel.bibliotheque.model.auth;

import java.time.LocalDateTime;

public class UserSession {
    private final UserAccount userAccount;
    private final LocalDateTime startDateTime = LocalDateTime.now();

    public UserAccount getUser() {
        return userAccount;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public UserSession(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
