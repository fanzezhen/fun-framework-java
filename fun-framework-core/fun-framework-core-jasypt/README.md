加密组件
------------------------------------------------------------------------------------------------------------------------

# 快速开始

## 根据不同的加密方式选择不同的bean注入

- SM2非对称加密

```properties
#SM2非对称加密
com.github.fanzezhen.fun.framework.jasypt.encryptor.bean=sm2StringEncryptor
com.github.fanzezhen.fun.framework.jasypt.type=SM2
com.github.fanzezhen.fun.framework.jasypt.a-symmetric.privateKey=MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg+MxXxSMFFgNtXGoFzkH3TJg0jXAo2J5XnM8isT7i0higCgYIKoEcz1UBgi2hRANCAARiDlNDvfGANqiqPHLWUN1mg1nz+4hN/06skj9DelWhIDK8IQ35NvFqf8dWoJkQ0KkxNvbuneWO0xt/e3fOgWkU
com.github.fanzezhen.fun.framework.jasypt.a-symmetric.publicKey=MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEYg5TQ73xgDaoqjxy1lDdZoNZ8/uITf9OrJI/Q3pVoSAyvCEN+Tbxan/HVqCZENCpMTb27p3ljtMbf3t3zoFpFA==
```

- SM4对称加密

```properties
#SM4对称加密
com.github.fanzezhen.fun.framework.jasypt.encryptor.bean=sm4StringEncryptor
com.github.fanzezhen.fun.framework.jasypt.type=SM4
com.github.fanzezhen.fun.framework.jasypt.symmetric.secretKey=fan986698
```

- 自定义bean加解密

```properties
#自定义Bean
com.github.fanzezhen.fun.framework.jasypt.encryptor.bean=customStringEncryptor
com.github.fanzezhen.fun.framework.jasypt.decrypt-param.path=/
com.github.fanzezhen.fun.framework.jasypt.encrypt-param.test=test
```

## 公共配置

```properties
# 启用加密
jasypt.encryptor.bootstrap=true
# 配置加密后的匹配格式（例如：spring.datasource.password=ENC(fd639cd245133ae562226a58763584c9a36665a6199a287909dc9423e6)）
com.github.fanzezhen.fun.framework.jasypt.encryptor.property.prefix=ENC(
com.github.fanzezhen.fun.framework.jasypt.encryptor.property.suffix=)
```

## 将配置文件中的明文改密文

* 密文的包裹格式需要匹配上面的配置，例如 spring.datasource.password=ENC(fd639cd245133ae562226a58763584c9a36665a6199a287909dc9423e6)
* 明文的加密方式和key的生成可以参考[StringEncryptorTest](src%2Ftest%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fjasypt%2Fencryptor%2FStringEncryptorTest.java)