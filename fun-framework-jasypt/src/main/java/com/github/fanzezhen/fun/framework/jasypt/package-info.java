/**
 * Jasypt 加密集成模块的根包。
 * <p>
 * 提供基于 Jasypt 的配置文件加密功能，支持多种加密算法：
 * <ul>
 *   <li>国密 SM2 非对称加密</li>
 *   <li>国密 SM4 对称加密</li>
 *   <li>RSA 非对称加密</li>
 * </ul>
 * <p>
 * 主要用于 Spring Boot 配置文件中敏感信息（如数据库密码、API 密钥等）的加密存储和运行时解密。
 */
package com.github.fanzezhen.fun.framework.jasypt;
