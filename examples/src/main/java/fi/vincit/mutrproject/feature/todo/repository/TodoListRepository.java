package fi.vincit.mutrproject.feature.todo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fi.vincit.mutrproject.feature.todo.model.TodoList;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    @Query("select l from todo_list l where publicList = true")
    List<TodoList> findPublicLists();

    @Query("select l from todo_list l where publicList = true or owner.username = :username")
    List<TodoList> findPublicAndOwnedBy(@Param("username") String username);
}
