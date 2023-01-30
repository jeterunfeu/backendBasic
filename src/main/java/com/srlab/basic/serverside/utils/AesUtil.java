package com.srlab.basic.serverside.utils;

import com.srlab.basic.serverside.configs.YamlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class AesUtil {

    private final Logger LOG = LoggerFactory.getLogger(AesUtil.class);

//    @Autowired
//    private YamlConfig config;

//    private String key= config.getKey();
//    private String salt= config.getSalt();

//    private AesBytesEncryptor aes = new AesBytesEncryptor(key, salt);

    public String encodeAes(String text, String key, String salt) {
        return new String(new AesBytesEncryptor(key, salt).encrypt(text.getBytes(StandardCharsets.UTF_8)));
    }

    public String decodeAes(String hash, String key, String salt) {
        return new String(new AesBytesEncryptor(key, salt).decrypt(hash.getBytes()));
    }

}
