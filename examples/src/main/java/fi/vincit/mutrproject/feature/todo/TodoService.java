package fi.vincit.mutrproject.feature.todo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import fi.vincit.mutrproject.feature.todo.dto.TodoItemDto;
import fi.vincit.mutrproject.feature.todo.dto.TodoListDto;
import fi.vincit.mutrproject.feature.todo.model.TodoItem;
import fi.vincit.mutrproject.feature.todo.model.TodoList;
import fi.vincit.mutrproject.feature.todo.repository.TodoItemRepository;
import fi.vincit.mutrproject.feature.todo.repository.TodoListRepository;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;

@Service
public class TodoService {

    @Autowired
    private UserService userService;

    @Autowired
    private TodoItemRepository todoItemRepository;
    @Autowired
    private TodoListRepository todoListRepository;


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
        TodoList list = todoListRepository.findOne(listId);
        Optional<User> user = userService.getLoggedInUser();
        authorizeRead(list, user);

        return todoItemRepository.findOne(id);
    }

    private TodoList getTodoListInternal(long id) {
        TodoList list = todoListRepository.findOne(id);
        Optional<User> user = userService.getLoggedInUser();
        return authorizeRead(list, user);
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
