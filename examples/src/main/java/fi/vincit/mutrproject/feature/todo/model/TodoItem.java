package fi.vincit.mutrproject.feature.todo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "todo_list_item")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "todo_list_item")
    private long id;
    @Column(name = "todo_list_id")
    private long todoListId;
    @Column(name = "name")
    private String task;
    @Column(name = "is_done")
    private boolean done;

    public TodoItem() {
    }

    public TodoItem(long todoListId, String task, boolean done) {
        this.todoListId = todoListId;
        this.task = task;
        this.done = done;
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

    public void setDone(boolean done) {
        this.done = done;
    }
}
