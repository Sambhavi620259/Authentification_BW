package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.KycEntity;
import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.io.RegisterRequest;
import in.bawvpl.Authify.repository.KycRepository;
import in.bawvpl.Authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KycRepository kycRepository; // ✅ ADD THIS

    public UserEntity registerUser(RegisterRequest req) {

        // Check duplicate email
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Check duplicate phone
        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }

        // ✅ CREATE USER
        UserEntity user = UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name(req.getName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .password(passwordEncoder.encode(req.getPassword()))
                .role("ROLE_USER")
                .isAccountVerified(false)
                .isKycVerified(false)
                .createdAt(System.currentTimeMillis())
                .build();

        user = userRepository.save(user);

        // ✅ CREATE KYC
        KycEntity kyc = KycEntity.builder()
                .aadhaarNumber(req.getAadhaarNumber())
                .panNumber(req.getPanNumber())
                .status("VERIFIED") // or "PENDING"
                .completed(true)
                .uploadedAt(Instant.now())
                .user(user)
                .build();

        kycRepository.save(kyc);

        // ✅ UPDATE USER KYC STATUS
        user.setIsKycVerified(true);
        userRepository.save(user);

        return user;
    }
}