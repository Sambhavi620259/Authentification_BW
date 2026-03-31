package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.OtpVerification;
import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;

    private static final int LOGIN_OTP_EXPIRY_MINUTES = 5;

    // -----------------------
    // GENERAL OTP GENERATOR
    // -----------------------
    @Override
    public String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }

    // -----------------------
    // LOGIN OTP GENERATION
    // -----------------------
    @Override
    @Transactional
    public String generateLoginOtp(UserEntity user) {

        // 1. Generate OTP
        String otp = generateOtp();

        // 2. Create OTP entity
        OtpVerification otpEntity = new OtpVerification();
        otpEntity.setUserId(user.getId());
        otpEntity.setEmail(user.getEmail());
        otpEntity.setPhoneNumber(user.getPhoneNumber());
        otpEntity.setOtp(otp);
        otpEntity.setPurpose("LOGIN");
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(LOGIN_OTP_EXPIRY_MINUTES));
        otpEntity.setIsUsed(false);

        // 3. Save OTP in DB
        otpRepository.save(otpEntity);

        // 4. Log for debugging
        log.info("Login OTP generated for {} => {}", user.getEmail(), otp);

        return otp;
    }

    // -----------------------
    // VERIFY LOGIN OTP
    // -----------------------
    @Override
    @Transactional
    public void verifyLoginOtp(UserEntity user, String otp) {

        // 1. Fetch latest OTP
        OtpVerification otpEntity = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(user.getEmail(), "LOGIN")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not found"));

        // 2. Validate OTP
        if (!otpEntity.getOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        // 3. Check expiry
        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        // 4. Check already used
        if (Boolean.TRUE.equals(otpEntity.getIsUsed())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP already used");
        }

        // 5. Mark as used
        otpEntity.setIsUsed(true);
        otpRepository.save(otpEntity);

        log.info("Login OTP verified successfully for {}", user.getEmail());
    }
}