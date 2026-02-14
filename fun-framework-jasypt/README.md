加密组件
------------------------------------------------------------------------------------------------------------------------
错误码前缀 1006**

# 快速开始

## 公共配置

```properties
# 启用加密
jasypt.encryptor.bootstrap=true
# 配置加密后的匹配格式（例如：spring.datasource.password=ENC(fd639cd245133ae562226a58763584c9a36665a6199a287909dc9423e6)）
jasypt.encryptor.property.prefix=ENC(
jasypt.encryptor.property.suffix=)
```

## 根据不同的加密方式选择不同的bean注入

- RSA非对称算法加密

```properties
# RSA非对称算法加密
jasypt.encryptor.bean=funRSAStringEncryptor
# 需要加密的参数，若ENC中无秘钥，会尝试从jasypt参数中获取：jasypt.encryptor.private-key-string、jasypt.encryptor.public-key-string、jasypt.encryptor.password 
**=ENC(密文`私钥`公钥)
```

- SM2非对称加密

```properties
# SM2非对称加密
jasypt.encryptor.bean=funSM2StringEncryptor
# 需要加密的参数，若ENC中无秘钥，会尝试从jasypt参数中获取：jasypt.encryptor.private-key-string、jasypt.encryptor.public-key-string 
**=ENC(密文`私钥`公钥)
```

- SM4对称加密

```properties
#SM4对称加密
jasypt.encryptor.bean=funSM4StringEncryptor
# 需要加密的参数，若ENC中无秘钥，会尝试从jasypt参数中获取：jasypt.encryptor.password、jasypt.encryptor.private-key-string、jasypt.encryptor.public-key-string 
**=ENC(密文`私钥`公钥)
```

- 自定义bean加解密

```properties
#自定义Bean
jasypt.encryptor.bean=customStringEncryptor
```

## 将配置文件中的明文改密文

* 密文的包裹格式需要匹配上面的配置，例如 spring.datasource.password=ENC(fd639cd245133ae562226a58763584c9a36665a6199a287909dc9423e6)
* 明文的加密方式和key的生成可以参考[StringEncryptorTest](src%2Ftest%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fjasypt%2Fencryptor%2FStringEncryptorTest.java)