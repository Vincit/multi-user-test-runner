package fi.vincit.mutrproject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vincit.mutrproject.feature.todo.repository.TodoItemRepository;
import fi.vincit.mutrproject.feature.todo.repository.TodoListRepository;
import fi.vincit.mutrproject.feature.user.repository.UserRepository;

@Component
public class DatabaseUtil {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TodoListRepository todoListRepository;
    @Autowired
    private TodoItemRepository todoItemRepository;

    @Transactional
    public void clearDb() {
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
        userRepository.deleteAll();
    }

}
