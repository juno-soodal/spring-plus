package org.example.expert.domain.log.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

import static org.example.expert.domain.log.entity.LogStatus.FAIL;
import static org.example.expert.domain.log.entity.LogStatus.SUCCESS;

@Entity
@Getter
@Table(name = "log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    @Enumerated(EnumType.STRING)
    private LogStatus status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Long userId;

    private Log(String action, LogStatus status, String errorMessage, Long userId) {
        this.action = action;
        this.status = status;
        this.errorMessage = errorMessage;
        this.userId = userId;
    }

    public static Log createSuccessLog(String action, Long userId) {
        return new Log(action, SUCCESS, null, userId);
    }

    public static Log createFailLog(String action, Long userId, String errorMessage ) {
        return new Log(action, FAIL, errorMessage, userId);
    }
}
