package com.nlweb.admin.service;

import com.nlweb.admin.entity.Admin;
import com.nlweb.admin.repository.AdminRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCacheService {

    private final AdminRepository adminRepository;

    @Cacheable(value = "admins:active")
    public List<Admin> getAllAdmins() {
        return adminRepository.findAllAdmins();
    }

    @Cacheable(value = "admin", key = "#studentId")
    public boolean isAdmin(Long userId) {
        return adminRepository.existsByUserId(userId);
    }

    @Cacheable(value = "admin", key = "#studentId")
    public boolean isAdmin(String studentId) {
        return adminRepository.existsByStudentId(studentId);
    }

    @Cacheable(value = "admin", key = "#userId")
    public Optional<Admin> getAdmin(Long userId) {
        return adminRepository.findByUserId(userId);
    }

    @Cacheable(value = "admin", key = "#studentId")
    public Optional<Admin> getAdmin(String studentId) {
        return adminRepository.findByStudentId(studentId);
    }

    @CacheEvict(value = {"admin", "admins:active"}, allEntries = true)
    public void evictAllAdminsCache() {
        log.debug("모든 관리자 캐시 삭제");
    }

    @CacheEvict(value = {"admin"}, key = "#userId")
    public void evictAdminCache(Long userId) {
        log.debug("관리자 캐시 삭제: Id = {}", userId);
    }

    @CacheEvict(value = {"admin"}, key = "#studentId")
    public void evictAdminCache(String studentId) {
        log.debug("관리자 캐시 삭제: studentId = {}", studentId);
    }
}
