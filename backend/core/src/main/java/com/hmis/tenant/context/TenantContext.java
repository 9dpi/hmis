package com.hmis.tenant.context;

import java.util.UUID;

/**
 * ThreadLocal lưu tenantId của request hiện tại.
 * Được set bởi TenantFilter sau khi xác thực JWT.
 */
public class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    public static void setCurrentTenantId(UUID tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static UUID getCurrentTenantId() {
        UUID tenantId = CURRENT_TENANT.get();
        if (tenantId == null) {
            throw new IllegalStateException("Không xác định được tenant hiện tại trong request");
        }
        return tenantId;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
