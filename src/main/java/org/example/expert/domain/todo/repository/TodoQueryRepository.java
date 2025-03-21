package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface TodoQueryRepository {

    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable, String weather, LocalDate startDate, LocalDate endDate);

    Optional<Todo> findByIdWithUser(Long todoId);

    Page<TodoSearchResponse> searchTodos(Pageable pageable, String keyword, String nickname, LocalDate startDate, LocalDate endDate);
}
