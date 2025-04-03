package com.github.fanzezhen.fun.framework.mp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * @author fanzezhen
 */
@Data
@ConfigurationProperties(prefix = "fun.mp")
public class FunMpProperties {
    private Tenant tenant = new Tenant();

    @Data
    public static class Tenant {
        boolean enabled = false;
        private Set<String> ignoreTenantTables = Collections.emptySet();
    }
}
