package fi.vincit.mutrproject.feature.todo;

import fi.vincit.mutrproject.feature.todo.dto.TodoItemDto;
import fi.vincit.mutrproject.feature.todo.dto.TodoListDto;
import fi.vincit.mutrproject.feature.todo.model.TodoItem;
import fi.vincit.mutrproject.feature.todo.model.TodoList;
import fi.vincit.mutrproject.feature.todo.repository.TodoItemRepository;
import fi.vincit.mutrproject.feature.todo.repository.TodoListRepository;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final UserService userService;
    private final TodoItemRepository todoItemRepository;
    private final TodoListRepository todoListRepository;

    @Autowired
    public TodoService(UserService userService, TodoItemRepository todoItemRepository, TodoListRepository todoListRepository) {
        this.userService = userService;
        this.todoItemRepository = todoItemRepository;
        this.todoListRepository = todoListRepository;
    }


    public void clearList() {
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
    }

    @PreAuthorize("isAuthenticated()")
    public long createTodoList(String listName, boolean publicList) {
        TodoList list = todoListRepository.save(new TodoList(
                listName,
                publicList,
                userService.getLoggedInUser().get()
        ));
        return list.getId();
    }

    public List<TodoListDto> getTodoLists() {
        final Optional<User> currentUser = userService.getLoggedInUser();
        List<TodoList> todoLists;
        if (currentUser.isPresent()) {
            if (isAdmin(currentUser.get())) {
                todoLists = todoListRepository.findAll();
            } else {
                todoLists = todoListRepository.findPublicAndOwnedBy(currentUser.get().getUsername());
            }
        } else {
            todoLists = todoListRepository.findPublicLists();
        }
        return todoLists.stream().map(TodoListDto::new).collect(Collectors.toList());
    }

    public TodoListDto getTodoList(long id) {
        return new TodoListDto(getTodoListInternal(id));
    }

    @PreAuthorize("isAuthenticated()")
    public TodoItemDto getTodoItem(long listId, long id) {
        return new TodoItemDto(getTodoItemInternal(listId, id));
    }

    @PreAuthorize("isAuthenticated()")
    public void setItemStatus(long listId, long itemId, boolean done) {
        TodoItem existingItem = getTodoItemInternal(listId, itemId);
        authorizeEdit(getTodoListInternal(listId), userService.getLoggedInUser());
        existingItem.setDone(done);
        todoItemRepository.save(existingItem);
    }

    @PreAuthorize("isAuthenticated()")
    public long addItemToList(long listId, String task) {
        TodoList list = getTodoListInternal(listId);

        Optional<User> user = userService.getLoggedInUser();
        authorizeEdit(list, user);

        TodoItem item = todoItemRepository.save(new TodoItem(listId, task, false));
        return item.getId();
    }

    private TodoItem getTodoItemInternal(long listId, long id) {
        Optional<TodoList> list = todoListRepository.findById(listId);
        Optional<User> user = userService.getLoggedInUser();
        authorizeRead(list.orElse(null), user);

        return todoItemRepository.findById(id).orElse(null);
    }

    private TodoList getTodoListInternal(long id) {
        Optional<TodoList> list = todoListRepository.findById(id);
        Optional<User> user = userService.getLoggedInUser();
        return authorizeRead(list.orElse(null), user);
    }

    private TodoList authorizeRead(TodoList list, Optional<User> user) {
        if (list.isPublicList()) {
            return list;
        }
        authorizeEdit(list, user);
        return list;
    }

    private void authorizeEdit(TodoList list, Optional<User> user) {
        if (user.isPresent()) {
            User loggedInUser = user.get();
            if (isAdmin(loggedInUser)) {
                return;
            } else if (isOwner(list, loggedInUser)) {
                return;
            }
        }
        throw new AccessDeniedException("");
    }

    private boolean isAdmin(User loggedInUser) {
        return loggedInUser.getAuthorities().contains(Role.ROLE_ADMIN)
                || loggedInUser.getAuthorities().contains(Role.ROLE_SYSTEM_ADMIN);
    }
    private boolean isOwner(TodoList list, User currentUser) {
        return list.getOwner().getUsername().equals(currentUser.getUsername());
    }

}
