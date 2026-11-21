package com.mininstagram.user.service;
import com.mininstagram.user.dto.*;
public interface UserProfileService {
 ProfileResponse me(String subject,String preferredUsername);
 ProfileResponse update(String subject,String preferredUsername,UpdateProfileRequest request);
}
