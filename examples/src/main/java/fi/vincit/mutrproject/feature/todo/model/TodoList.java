package fi.vincit.mutrproject.feature.todo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import fi.vincit.mutrproject.feature.user.model.User;

@Entity(name = "todo_list")
public class TodoList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "todo_list_id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "is_public")
    private boolean publicList;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User owner;
    @OneToMany(targetEntity = TodoItem.class, fetch = FetchType.EAGER, mappedBy = "todoListId")
    private List<TodoItem> items;

    public TodoList() {
    }

    public TodoList(String name, boolean publicList, User owner) {
        this.name = name;
        this.publicList = publicList;
        this.owner = owner;
        this.items = new ArrayList<>();
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

    public User getOwner() {
        return owner;
    }

    public List<TodoItem> getItems() {
        return items;
    }
}
