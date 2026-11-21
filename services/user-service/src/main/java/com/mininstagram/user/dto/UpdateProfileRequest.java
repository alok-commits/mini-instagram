package com.mininstagram.user.dto;
import jakarta.validation.constraints.Size;
public record UpdateProfileRequest(@Size(min=1,max=120) String displayName,
                                   @Size(max=300) String bio) { }
