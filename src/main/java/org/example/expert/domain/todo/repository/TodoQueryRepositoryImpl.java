package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository{

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
        params.forEach( (k, v) -> todosQuery.setParameter(k, v));

        List<Todo> todos = todosQuery
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        TypedQuery<Long> countQuery = em.createQuery(baseCountQuery.toString(), Long.class);
        params.forEach( (k, v) -> countQuery.setParameter(k, v));

        long total = countQuery.getSingleResult();

        return new PageImpl<>(todos,pageable,total);
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
}
