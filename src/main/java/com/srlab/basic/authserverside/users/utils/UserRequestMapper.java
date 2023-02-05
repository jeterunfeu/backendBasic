package com.srlab.basic.authserverside.users.utils;

import com.srlab.basic.authserverside.users.Dto.OAuthDto;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserRequestMapper {
    public OAuthDto toDto(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();
        return OAuthDto.builder()
                .email((String)attributes.get("email"))
                .name((String)attributes.get("name"))
                .build();
    }

}
