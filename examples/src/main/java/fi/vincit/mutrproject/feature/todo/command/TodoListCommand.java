package fi.vincit.mutrproject.feature.todo.command;

public class TodoListCommand {
    private String name;
    private ListVisibility listVisibility;

    public TodoListCommand() {
    }

    public TodoListCommand(String name, ListVisibility listVisibility) {
        this.name = name;
        this.listVisibility = listVisibility;
    }

    public String getName() {
        return name;
    }

    public ListVisibility getListVisibility() {
        return listVisibility;
    }
}
