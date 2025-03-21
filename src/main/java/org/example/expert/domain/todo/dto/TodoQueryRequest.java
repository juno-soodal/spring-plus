package org.example.expert.domain.todo.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TodoQueryRequest {

    private final String keyword;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String nickname;


    public TodoQueryRequest(String keyword, LocalDate startDate, LocalDate endDate, String nickname) {
        this.keyword = keyword;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nickname = nickname;
    }
}
