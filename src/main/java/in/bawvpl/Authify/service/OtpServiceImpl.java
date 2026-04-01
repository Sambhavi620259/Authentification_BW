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

    @Override
    public String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }

    @Override
    @Transactional
    public String generateLoginOtp(UserEntity user) {

        String otp = generateOtp();

        OtpVerification otpEntity = new OtpVerification();
        otpEntity.setUserId(user.getId());
        otpEntity.setEmail(user.getEmail());
        otpEntity.setPhoneNumber(user.getPhoneNumber());
        otpEntity.setOtp(otp);
        otpEntity.setPurpose("LOGIN");
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(LOGIN_OTP_EXPIRY_MINUTES));
        otpEntity.setIsUsed(false);

        // 🔥 FORCE SAVE
        otpRepository.saveAndFlush(otpEntity);

        log.info("🔥 OTP SAVED IN DB for {} => {}", user.getEmail(), otp);

        return otp;
    }

    @Override
    @Transactional
    public void verifyLoginOtp(UserEntity user, String otp) {

        OtpVerification otpEntity = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(user.getEmail(), "LOGIN")
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not found"));

        if (!otpEntity.getOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        if (Boolean.TRUE.equals(otpEntity.getIsUsed())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP already used");
        }

        otpEntity.setIsUsed(true);
        otpRepository.save(otpEntity);

        log.info("✅ OTP verified for {}", user.getEmail());
    }
}