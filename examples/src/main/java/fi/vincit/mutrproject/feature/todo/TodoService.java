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
        this.secureSystemAdminTodos = secureSystemAdminTodos;
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
        User user = userService.getLoggedInUser().orElseThrow(() -> new AccessDeniedException("Not logged in"));

        TodoList list = todoListRepository.save(new TodoList(
                listName,
                publicList,
                user
        ));
        return list.getId();
    }

    public List<TodoListDto> getTodoLists() {
        final Optional<User> currentUser = userService.getLoggedInUser();
        List<TodoList> todoLists;
        if (currentUser.isPresent()) {
            if (isAnyAdmin(currentUser.get())) {
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
        authorizeEdit(getTodoListInternal(listId), userService.getLoggedInUser().orElse(null));
        existingItem.setDone(done);
        todoItemRepository.save(existingItem);
    }

    @PreAuthorize("isAuthenticated()")
    public long addItemToList(long listId, String task) {
        TodoList list = getTodoListInternal(listId);

        Optional<User> user = userService.getLoggedInUser();
        authorizeEdit(list, user.orElse(null));

        TodoItem item = todoItemRepository.save(new TodoItem(listId, task, false));
        return item.getId();
    }

    private TodoItem getTodoItemInternal(long listId, long id) {
        Optional<TodoList> list = todoListRepository.findById(listId);
        Optional<User> user = userService.getLoggedInUser();
        authorizeRead(list.orElse(null), user.orElse(null));

        return todoItemRepository.findById(id).orElse(null);
    }

    private TodoList getTodoListInternal(long id) {
        Optional<TodoList> list = todoListRepository.findById(id);
        Optional<User> user = userService.getLoggedInUser();
        return authorizeRead(list.orElse(null), user.orElse(null));
    }

    private TodoList authorizeRead(TodoList list, User user) {
        if (list == null || list.isPublicList()) {
            return list;
        }
        authorizeEdit(list, user);
        return list;
    }

    private void authorizeEdit(TodoList list, User user) {
        if (user == null) {
            throw new AccessDeniedException("No user");
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
