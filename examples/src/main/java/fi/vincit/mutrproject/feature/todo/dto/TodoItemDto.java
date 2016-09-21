package fi.vincit.mutrproject.feature.todo.dto;

import fi.vincit.mutrproject.feature.todo.model.TodoItem;

public class TodoItemDto {

    private long id;
    private long todoListId;
    private String task;
    private boolean done;

    public TodoItemDto(long id, long todoListId, String task, boolean done) {
        this.id = id;
        this.todoListId = todoListId;
        this.task = task;
        this.done = done;
    }

    public TodoItemDto(TodoItem todoItem) {
        this(todoItem.getId(), todoItem.getTodoListId(), todoItem.getTask(), todoItem.isDone());
    }

    public long getId() {
        return id;
    }

    public long getTodoListId() {
        return todoListId;
    }

    public String getTask() {
        return task;
    }

    public boolean isDone() {
        return done;
    }
}
