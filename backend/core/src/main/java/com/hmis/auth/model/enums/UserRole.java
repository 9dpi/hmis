package com.hmis.auth.model.enums;

/**
 * Vai trò người dùng trong hệ thống HMIS.
 * Mỗi vai trò được cấp quyền khác nhau theo RBAC.
 */
public enum UserRole {
    ADMIN,       // Quản trị viên hệ thống
    DOCTOR,      // Bác sĩ
    NURSE,       // Y tá / Điều dưỡng
    PHARMACIST,  // Dược sĩ
    STAFF,       // Nhân viên hành chính
    READONLY     // Xem không chỉnh sửa
}
