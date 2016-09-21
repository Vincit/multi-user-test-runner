package fi.vincit.mutrproject.feature.todo.command;

public class TodoListCommand {
    private String name;
    private boolean publicList;

    public TodoListCommand() {
    }

    public TodoListCommand(String name, boolean publicList) {
        this.name = name;
        this.publicList = publicList;
    }

    public String getName() {
        return name;
    }

    public boolean isPublicList() {
        return publicList;
    }
}
