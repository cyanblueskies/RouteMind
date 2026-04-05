package uob.codecollective.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uob.codecollective.backend.entity.UserPreference;
import uob.codecollective.backend.service.UserPreferenceService;


@RestController
@RequestMapping("/api/user/preference")
public class UserPreferenceController {

    private final UserPreferenceService svc;

    public UserPreferenceController(UserPreferenceService svc) 
    {
         this.svc = svc;
    }
    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@RequestBody UserPreference preference,
                     @CookieValue(value = "user_id", required = true) Long userId){
        return svc.save(preference, userId);
    
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserPreference getByUserId(@PathVariable Long userId){
        return svc.getByUserId(userId);
    }

}
