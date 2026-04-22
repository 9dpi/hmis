package com.hmis.module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Module Bus: quản lý các module đang active trong hệ thống.
 * Cho phép enable/disable module theo tenant (runtime).
 *
 * Modules hiện tại:
 *   - EHR          (Bệnh án điện tử)
 *   - APPOINTMENT  (Lịch hẹn)
 *   - PHARMACY     (Dược)
 *   - LAB          (Xét nghiệm)
 *   - BILLING      (Viện phí)
 *   - IOT          (Thiết bị y tế)
 */
@Component
@Slf4j
public class ModuleManager {

    // tenantId → set of enabled modules
    private final Map<String, Set<String>> tenantModules = new ConcurrentHashMap<>();

    // Default modules bật cho tất cả tenant
    private static final Set<String> DEFAULT_MODULES = Set.of("EHR");

    public void enableModule(String tenantId, String module) {
        tenantModules.computeIfAbsent(tenantId, k -> ConcurrentHashMap.newKeySet())
                     .add(module.toUpperCase());
        log.info("Module {} enabled for tenant {}", module, tenantId);
    }

    public void disableModule(String tenantId, String module) {
        Set<String> modules = tenantModules.get(tenantId);
        if (modules != null) {
            modules.remove(module.toUpperCase());
            log.info("Module {} disabled for tenant {}", module, tenantId);
        }
    }

    public boolean isEnabled(String tenantId, String module) {
        if (DEFAULT_MODULES.contains(module.toUpperCase())) {
            return true;
        }
        Set<String> modules = tenantModules.get(tenantId);
        return modules != null && modules.contains(module.toUpperCase());
    }

    public Set<String> getEnabledModules(String tenantId) {
        Set<String> base = ConcurrentHashMap.newKeySet();
        base.addAll(DEFAULT_MODULES);
        Set<String> extra = tenantModules.get(tenantId);
        if (extra != null) base.addAll(extra);
        return Collections.unmodifiableSet(base);
    }
}
