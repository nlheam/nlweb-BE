package com.nlweb.user.service;

import com.nlweb.common.enums.UserSessionType;
import com.nlweb.common.enums.UserStatus;
import com.nlweb.user.entity.User;
import com.nlweb.user.repository.UserRepository;
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
public class UserCacheService {

    private final UserRepository userRepository;

    @Cacheable(value = "users:active")
    public List<User> getAllActiveUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "users:pending")
    public List<User> getAllPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING);
    }

    @Cacheable(value = "user", key = "#id")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Cacheable(value = "user", key = "#studentId")
    public Optional<User> getUserByStudentId(String studentId) {
        return userRepository.findByStudentId(studentId);
    }

    @Cacheable(value = "user", key = "#email")
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Cacheable(value = "user", key = "'students:' + #studentIds.hashCode()")
    public List<User> getUsersByStudentIds(List<String> studentIds) {
        return userRepository.findAllByStudentIdIn(studentIds);
    }

    @Cacheable(value = "user", key = "#session")
    public List<User> getUsersBySession(UserSessionType session) {
        return userRepository.findBySessionAndStatus(session, UserStatus.ACTIVE);
    }

    @Cacheable(value = "user", key = "#batch")
    public List<User> getUsersByBatch(Integer batch) {
        return userRepository.findByBatchAndStatusOrderByBatchAsc(batch, UserStatus.ACTIVE);
    }

    @Cacheable(value = "user", key = "#id")
    public boolean isUserExistsById(Long id) {
        return userRepository.existsById(id);
    }

    @Cacheable(value = "user", key = "#studentId")
    public boolean isUserExistsByStudentId(String studentId) {
        return userRepository.existsByStudentId(studentId);
    }

    @Cacheable(value = "user", key = "#email")
    public boolean isUserExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Cacheable(value = "user", key = "#phone")
    public boolean isUserExistsByPhone(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }

    @CacheEvict(value = {"user", "users:active", "users:pending"}, allEntries = true)
    public void evictAllUsersCache() {
        log.debug("모든 사용자 캐시 삭제");
    }

    @CacheEvict(value = {"users:active"}, allEntries = true)
    public void evictAllActiveUsersCache() {
        log.debug("활성 사용자 캐시 삭제");
    }

    @CacheEvict(value = {"users:pending"}, allEntries = true)
    public void evictAllPendingUsersCache() {
        log.debug("대기 사용자 캐시 삭제");
    }

    @CacheEvict(value = {"user"}, key = "#id")
    public void evictUserCacheById(Long id) {
        log.debug("사용자 캐시 삭제: Id = {}", id);
    }

    @CacheEvict(value = {"user"}, key = "#studentId")
    public void evictUserCacheByStudentId(String studentId) {
        log.debug("사용자 캐시 삭제: studentId = {}", studentId);
    }

}
