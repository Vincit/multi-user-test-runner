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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TodoService {

    private final UserService userService;
    private final TodoItemRepository todoItemRepository;
    private final TodoListRepository todoListRepository;
    private boolean secureSystemAdminTodos;

    @Autowired
    public TodoService(UserService userService, TodoItemRepository todoItemRepository, TodoListRepository todoListRepository) {
        this.userService = userService;
        this.todoItemRepository = todoItemRepository;
        this.todoListRepository = todoListRepository;
    }

    public void setSecureSystemAdminTodos(boolean secureSystemAdminTodos) {
        this.secureSystemAdminTodos = secureSystemAdminTodos;
    }

    public void clearList() {
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
    }

    @PreAuthorize("isAuthenticated()")
    public long createTodoList(String listName, boolean publicList) {
        final User user = userService.getLoggedInUser();
        if (!user.isLoggedIn()) {
            throw new AccessDeniedException("Not logged in");
        }

        return todoListRepository.save(new TodoList(
                listName,
                publicList,
                user
        )).getId();
    }

    public List<TodoListDto> getTodoLists() {
        final User currentUser = userService.getLoggedInUser();
        List<TodoList> todoLists;
        if (currentUser.isLoggedIn()) {
            if (isAnyAdmin(currentUser)) {
                todoLists = todoListRepository.findAll();
            } else {
                todoLists = todoListRepository.findPublicAndOwnedBy(currentUser.getUsername());
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
        return getTodoItemInternal(listId, id)
                .map(TodoItemDto::new)
                .orElse(null);
    }

    @PreAuthorize("isAuthenticated()")
    public void setItemStatus(long listId, long itemId, boolean done) {
        getTodoItemInternal(listId, itemId).ifPresent(existingItem -> {
            authorizeEdit(getTodoListInternal(listId), userService.getLoggedInUser());
            existingItem.setDone(done);
            todoItemRepository.save(existingItem);
        });
    }

    @PreAuthorize("isAuthenticated()")
    public long addItemToList(long listId, String task) {
        final User user = userService.getLoggedInUser();
        final TodoList list = getTodoListInternal(listId);
        authorizeEdit(list, user);

        TodoItem item = todoItemRepository.save(new TodoItem(listId, task, false));
        return item.getId();
    }

    private Optional<TodoItem> getTodoItemInternal(long listId, long id) {
        TodoList list = todoListRepository.findById(listId)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));
        User user = userService.getLoggedInUser();

        authorizeRead(list, user);

        return todoItemRepository.findById(id);
    }

    private TodoList getTodoListInternal(long id) {
        TodoList list = todoListRepository.findById(id)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));
        User user = userService.getLoggedInUser();

        authorizeRead(list, user);

        return list;
    }

    private void authorizeRead(TodoList list, User user) {
        if (!list.isPublicList()) {
            // In this test application private list are visible
            // only for those, how can also edit the list.
            authorizeEdit(list, user);
        }
    }

    private void authorizeEdit(TodoList list, User user) {
        if (!user.isLoggedIn()) {
            throw new AccessDeniedException("Not allowed");
        } else if (secureSystemAdminTodos) {
            final boolean ownedByStemAdmin = isSystemAdmin(list.getOwner());
            if (ownedByStemAdmin && isSystemAdmin(user)) {
                return;
            } else if (!ownedByStemAdmin && (isAnyAdmin(user) || isOwner(list, user))) {
                return;
            }
        } else if (isAnyAdmin(user)) {
            return;
        } else if (isOwner(list, user)) {
            return;
        }
        throw new AccessDeniedException("User role <" + user.getAuthorities() + "> doesn't have privileges to edit.");
    }

    private boolean isAnyAdmin(User loggedInUser) {
        return loggedInUser.getAuthorities().contains(Role.ROLE_ADMIN)
                || loggedInUser.getAuthorities().contains(Role.ROLE_SYSTEM_ADMIN);
    }

    private boolean isNormalAdmin(User loggedInUser) {
        return loggedInUser.getAuthorities().contains(Role.ROLE_ADMIN);
    }

    private boolean isSystemAdmin(User loggedInUser) {
        return loggedInUser.getAuthorities().contains(Role.ROLE_SYSTEM_ADMIN);
    }

    private boolean isOwner(TodoList list, User currentUser) {
        return list.getOwner().getUsername().equals(currentUser.getUsername());
    }
}
