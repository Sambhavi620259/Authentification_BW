package in.bawvpl.Authify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    private String password;

    private String role;

    private Boolean isAccountVerified = false;
    private Boolean isKycVerified = false;

    // ✅ OTP FIELDS (FIXED)
    private String verifyOtp;
    private Long verifyOtpExpireAt;

    private String resetOtp;
    private Long resetOtpExpireAt;

    private String registerOtp;
    private Long registerOtpExpireAt;

    // ✅ KYC RELATION
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private KycEntity kyc;

    @Column(updatable = false)
    private Long createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now().toEpochMilli();
    }
}