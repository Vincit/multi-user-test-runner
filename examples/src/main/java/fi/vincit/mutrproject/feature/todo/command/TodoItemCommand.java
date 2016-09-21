package fi.vincit.mutrproject.feature.todo.command;

public class TodoItemCommand {

    private long listId;

    private String name;

    public TodoItemCommand() {
    }

    public TodoItemCommand(long listId, String name) {
        this.listId = listId;
        this.name = name;
    }

    public long getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }
}
