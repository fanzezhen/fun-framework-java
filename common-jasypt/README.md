
## 根据不同的加密方式选择不同的bean注入

```properties
# 通用配置（spring.datasource.password=ENC(fd639cd245133ae562226a58763584c9a36665a6199a287909dc9423e6)）
com.github.fanzezhen.common.jasypt.encryptor.property.prefix=ENC(
com.github.fanzezhen.common.jasypt.encryptor.property.suffix=)
```

- SM2非对称加密

```properties
#SM2非对称加密
com.github.fanzezhen.common.jasypt.encryptor.bean=sm2StringEncryptor
com.github.fanzezhen.common.jasypt.type=SM2
com.github.fanzezhen.common.jasypt.privateKey=MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg+MxXxSMFFgNtXGoFzkH3TJg0jXAo2J5XnM8isT7i0higCgYIKoEcz1UBgi2hRANCAARiDlNDvfGANqiqPHLWUN1mg1nz+4hN/06skj9DelWhIDK8IQ35NvFqf8dWoJkQ0KkxNvbuneWO0xt/e3fOgWkU
com.github.fanzezhen.common.jasypt.publicKey=MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEYg5TQ73xgDaoqjxy1lDdZoNZ8/uITf9OrJI/Q3pVoSAyvCEN+Tbxan/HVqCZENCpMTb27p3ljtMbf3t3zoFpFA==
```
- SM4对称加密
```properties
#SM4对称加密
com.github.fanzezhen.common.jasypt.encryptor.bean=sm4StringEncryptor
com.github.fanzezhen.common.jasypt.type=SM4
com.github.fanzezhen.common.jasypt.secretKey=fan986698
```
- 自定义bean加解密
```properties
#自定义Bean
com.github.fanzezhen.common.jasypt.encryptor.bean=customStringEncryptor
com.github.fanzezhen.common.jasypt.decrypt-param.path=/
com.github.fanzezhen.common.jasypt.encrypt-param.test=test
```

