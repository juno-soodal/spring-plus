package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSuccessLog(String action, Long userId) {
        Log log = Log.createSuccessLog(action, userId);
        logRepository.save(log);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailLog(String action, Long userId, String errorMessage) {
        Log log = Log.createFailLog(action, userId, errorMessage);
        logRepository.save(log);

    }
}
