package org.example.expert.domain.todo.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter

public class TodoSearchRequest {
    private final String weather;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public TodoSearchRequest(String weather, LocalDate startDate, LocalDate endDate) {
        this.weather = weather;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
