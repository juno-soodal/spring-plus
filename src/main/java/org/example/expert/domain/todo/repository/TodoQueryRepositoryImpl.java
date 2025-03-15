package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable, String weather, LocalDate startDate, LocalDate endDate) {

        StringBuilder baseTodosQuery = new StringBuilder("select t from Todo t left join fetch t.user");
        StringBuilder baseCountQuery = new StringBuilder("select count(t) from Todo t");
        Map<String, Object> params = new HashMap<>();
        List<String> conditions = new ArrayList<>();
        if (StringUtils.hasText(weather)) {
            conditions.add("t.weather = :weather");
            params.put("weather", weather);
        }

        if (startDate != null) {
            conditions.add("t.modifiedAt >= :startDate");
            params.put("startDate", LocalDateTime.of(startDate, LocalTime.MIN));
        }

        if (endDate != null) {
            conditions.add("t.modifiedAt <= :endDate");
            params.put("endDate", LocalDateTime.of(endDate, LocalTime.MAX));
        }

        if (!conditions.isEmpty()) {
            baseTodosQuery.append(" WHERE ").append(String.join(" AND ", conditions));
            baseCountQuery.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        baseTodosQuery.append(" order by t.modifiedAt desc");
        TypedQuery<Todo> todosQuery = em.createQuery(baseTodosQuery.toString(), Todo.class);
        params.forEach((k, v) -> todosQuery.setParameter(k, v));

        List<Todo> todos = todosQuery
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        TypedQuery<Long> countQuery = em.createQuery(baseCountQuery.toString(), Long.class);
        params.forEach((k, v) -> countQuery.setParameter(k, v));

        long total = countQuery.getSingleResult();

        return new PageImpl<>(todos, pageable, total);
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo result = queryFactory.selectFrom(todo)
                .leftJoin(todo.user, user)
                .fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchFirst();
        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(Pageable pageable, String keyword, String nickname, LocalDate startDate, LocalDate endDate) {

        JPAQuery<Long> managerCount = queryFactory.select(manager.count()).from(manager).where(manager.todo.id.eq(todo.id));
        JPAQuery<Long> commentCount = queryFactory.select(comment.count()).from(comment).where(comment.todo.id.eq(todo.id));
        List<TodoSearchResponse> fetch = queryFactory.select(Projections.constructor(
                                TodoSearchResponse.class,
                                todo.title,
                                managerCount,
                                commentCount
                        )
                ).from(todo)
                .where(
                        todoTitleContains(keyword),
                        todoCreatedAtGoe(startDate),
                        todoCreatedAtLoe(endDate),
                        nicknameContains(nickname)
                )
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        JPAQuery<Long> countQuery = queryFactory.select(todo.count()).from(todo).where(
                todoTitleContains(keyword),
                todoCreatedAtGoe(startDate),
                todoCreatedAtLoe(endDate),
                nicknameContains(nickname)
        );
        return PageableExecutionUtils.getPage(fetch, pageable, () -> countQuery.fetchOne());
    }

    private BooleanExpression nicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? JPAExpressions.selectOne().from(manager)
                .join(manager.user, user)
                .where(
                        manager.user.id.eq(user.id),
                        manager.todo.id.eq(todo.id),
                        user.nickname.containsIgnoreCase(nickname)
                ).exists() : null;
    }

    private BooleanExpression todoCreatedAtLoe(LocalDate endDate) {
        return endDate != null ? todo.createdAt.loe(LocalDateTime.of(endDate, LocalTime.MAX)) : null;
    }

    private BooleanExpression todoCreatedAtGoe(LocalDate startDate) {
        return startDate != null ? todo.createdAt.goe(LocalDateTime.of(startDate, LocalTime.MIN)) : null;
    }

    private BooleanExpression todoTitleContains(String keyword) {
        return StringUtils.hasText(keyword) ? todo.title.contains(keyword) : null;
    }

}
