package uob.codecollective.backend.service;

import org.springframework.stereotype.Service;
import uob.codecollective.backend.controller.errors.NotFoundError;
import uob.codecollective.backend.entity.UserPreference;
import uob.codecollective.backend.entity.User;
import uob.codecollective.backend.repository.UserPreferenceRepository;

import java.util.Optional;

@Service
public class UserPreferenceService {
    private final UserPreferenceRepository repo;
    private final UserService userSvc;

    public UserPreferenceService(UserPreferenceRepository repo, UserService userSvc) {
        this.repo = repo;
        this.userSvc = userSvc;
    }

    public Long save(UserPreference preference, Long userId) {
        User user = userSvc.findById(userId);

        preference.setUser(user);
        repo.save(preference);
        return preference.getUserId();
    }

    public UserPreference getByUserId(Long userId) {
        return repo.findByUserId(userId).orElseThrow(
                () -> new NotFoundError("No preference found for user " + userId)
        );
    }

}
