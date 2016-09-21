package fi.vincit.mutrproject.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.vincit.mutrproject.feature.todo.TodoService;
import fi.vincit.mutrproject.feature.todo.command.TodoItemCommand;
import fi.vincit.mutrproject.feature.todo.command.TodoListCommand;
import fi.vincit.mutrproject.feature.todo.dto.TodoListDto;

@RestController
public class TodoController {

    @Autowired
    private TodoService todoService;

    @RequestMapping(value = "/api/todo/lists", method = RequestMethod.GET)
    public List<TodoListDto> getLists() {
        return todoService.getTodoLists();
    }

    @RequestMapping(value = "/api/todo/list/{listId}", method = RequestMethod.GET)
    public TodoListDto getList(@PathVariable("listId") long id) {
        return todoService.getTodoList(id);
    }

    @RequestMapping(value = "/api/todo/list/item", method = RequestMethod.POST)
    public long createItemTask(@RequestBody TodoItemCommand todoItemCommand) {
        return todoService.addItemToList(todoItemCommand.getListId(), todoItemCommand.getName());
    }

    @RequestMapping(value = "/api/todo/list", method = RequestMethod.POST, consumes = "application/json")
    public long createPublicList(@RequestBody TodoListCommand todoListCommand) {
        return todoService.createTodoList(todoListCommand.getName(), todoListCommand.isPublicList());
    }

    @RequestMapping(value = "/api/todo/list/{listId}/{itemId}/done", method = RequestMethod.POST, consumes = "application/json")
    public void saveItem(@PathVariable("listId") long listId, @PathVariable("itemId") long itemId) {
        todoService.setItemStatus(listId, itemId, true);
    }

}
