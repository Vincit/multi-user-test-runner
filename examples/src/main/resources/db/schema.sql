create table user (
    name varchar2(300 char) not null,
    username varchar2(300 char) not null,
    password varchar2(300 char) not null,
    primary key (username),
);

create table user_role (
    username varchar2(300 char) not null,
    role varchar2(300 char) not null,
    foreign key (username) references user(username),
    primary key (username, role)
);

create table todo_list (
    todo_list_id numeric(19) not null,
    name varchar2(2 char) not null,
    is_public boolean default false not null,
    owner varchar2(100 char) not null,
    foreign key (owner) references user(username),
    primary key (todo_list_id)
);

create table todo_list_item (
    todo_item_id numeric(19) not null,
    todo_list_id numeric(19) not null,
    name varchar2(2 char) not null,
    is_done boolean default false not null,

    primary key (todo_item_id),
    foreign key (todo_list_id) references todo_list(todo_list_id)
);
