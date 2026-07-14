package net.berryhomes.service.impl.SecurityServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import net.berryhomes.model.entity.Admin;
import net.berryhomes.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@Slf4j
@Loggable
@RequiredArgsConstructor
public class LoginAttemptService {

    private final AdminRepository adminRepository;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_DURATION_MINUTES = 15;

    @Transactional
    public void processFailedLogin(String login) {
        adminRepository.findByLogin(login).ifPresent(admin -> {
            if (!admin.isAccountNonLocked()) {
                if (isLockExpired(admin)) {
                    unlockAccount(admin);
                } else {
                    return;
                }
            }

            int newAttempts = admin.getFailedAttempts() + 1;
            admin.setFailedAttempts(newAttempts);
            log.warn("Неудачная попытка входа для пользователя {}. Всего попыток: {}", login, newAttempts);

            if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                admin.setAccountNonLocked(false);
                admin.setLockTime(ZonedDateTime.now());
                log.error("АККАУНТ ЗАБЛОКИРОВАН: Пользователь {} превысил лимит неудачных попыток входа!", login);
            }
            adminRepository.save(admin);
        });
    }

    @Transactional
    public void resetFailedAttempts(String login) {
        adminRepository.findByLogin(login).ifPresent(admin -> {
            if (admin.getFailedAttempts() > 0) {
                admin.setFailedAttempts(0);
                admin.setAccountNonLocked(true);
                admin.setLockTime(null);
                adminRepository.save(admin);
                log.info("Счетчик неудачных попыток для пользователя {} успешно сброшен.", login);
            }
        });
    }

    public boolean checkLockDurationAndUnlock(Admin admin) {
        if (!admin.isAccountNonLocked() && isLockExpired(admin)) {
            unlockAccount(admin);
            return true;
        }
        return admin.isAccountNonLocked();
    }

    private boolean isLockExpired(Admin admin) {
        if (admin.getLockTime() == null) return true;
        return admin.getLockTime().plusMinutes(LOCK_TIME_DURATION_MINUTES).isBefore(ZonedDateTime.now());
    }

    private void unlockAccount(Admin admin) {
        admin.setAccountNonLocked(true);
        admin.setFailedAttempts(0);
        admin.setLockTime(null);
        adminRepository.save(admin);
        log.info("Аккаунт пользователя {} был автоматически разблокирован по истечении времени.", admin.getLogin());
    }
}