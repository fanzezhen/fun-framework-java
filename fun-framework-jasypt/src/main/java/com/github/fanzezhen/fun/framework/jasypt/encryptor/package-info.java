/**
 * Jasypt 字符串加密器实现包。
 * <p>
 * 提供各种加密算法的字符串加密器实现：
 * <ul>
 *   <li>{@link com.github.fanzezhen.fun.framework.jasypt.encryptor.FunSM2StringEncryptor} - 国密 SM2 加密器</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.jasypt.encryptor.FunSM4StringEncryptor} - 国密 SM4 加密器</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.jasypt.encryptor.FunRSAStringEncryptor} - RSA 加密器</li>
 * </ul>
 * <p>
 * 所有加密器继承自 {@link com.github.fanzezhen.fun.framework.jasypt.encryptor.AbstractStringEncryptor}，
 * 实现 Jasypt 的 {@link org.jasypt.encryption.StringEncryptor} 接口。
 */
package com.github.fanzezhen.fun.framework.jasypt.encryptor;
