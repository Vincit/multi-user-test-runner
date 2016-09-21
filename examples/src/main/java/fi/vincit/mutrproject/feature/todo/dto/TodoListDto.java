package fi.vincit.mutrproject.feature.todo.dto;

import java.util.List;
import java.util.stream.Collectors;

import fi.vincit.mutrproject.feature.todo.model.TodoList;

public class TodoListDto {
    private long id;
    private String name;
    private boolean publicList;
    private String owner;
    private List<TodoItemDto> items;

    public TodoListDto(long id, String name, boolean publicList, String owner, List<TodoItemDto> items) {
        this.id = id;
        this.name = name;
        this.publicList = publicList;
        this.owner = owner;
        this.items = items;
    }

    public TodoListDto(TodoList todoList) {
        this(todoList.getId(),
                todoList.getName(),
                todoList.isPublicList(),
                todoList.getOwner().getUsername(),
                todoList.getItems().stream()
                        .map(TodoItemDto::new)
                        .collect(Collectors.toList())
        );
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPublicList() {
        return publicList;
    }

    public String getOwner() {
        return owner;
    }

    public List<TodoItemDto> getItems() {
        return items;
    }
}
