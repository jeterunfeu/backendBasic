package com.srlab.basic.serverside.utils;

import com.srlab.basic.serverside.configs.YamlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class BcryptUtil {

//    @Autowired
//    private YamlConfig config;

//    private Integer count= Integer.parseInt(config.getCount());

//    private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(count);

    public String encodeBcrypt(String text, Integer num) {
        return new BCryptPasswordEncoder(num).encode(text);
    }

    public boolean matchBcrypt(String text, String hash, Integer num) {
        return new BCryptPasswordEncoder(num).matches(text, hash);
    }

}
