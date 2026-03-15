package jpa.basic.crafthouse.entity;

import jakarta.persistence.*;
import jpa.basic.crafthouse.enums.ReservationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 예약 고유 식별자 ID
    private Long id;

    // 예약 회원 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Member member;

    // 예약 대상 클래스 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "craftClass_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private CraftClass craftClass;

    // 예약 건 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    // 예약 생성 일시
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime reservedAt;

    // 예약 취소 여부(기본값: false)
    @Column(nullable = false)
    private boolean cancelled = false;

    // 예약 취소 일시
    @Column(name = "cancelledAt")
    private LocalDateTime cancelledAt;

    // 예약 취소 메서드
    public void cancel() {
        this.cancelled = true;
        this.cancelledAt = LocalDateTime.now();
        this.status = ReservationStatus.CANCELLED;
    }
}
