create table IF NOT EXISTS GENRE
(
    ID   INTEGER not null,
    NAME CHARACTER VARYING(50),
    constraint "GENRE_pk"
        primary key (ID)
);

create table  IF NOT EXISTS RATING
(
    ID   INTEGER auto_increment,
    NAME CHARACTER VARYING(10),
    constraint RATING_PK
        primary key (ID)
);

create table  IF NOT EXISTS FILMS
(
    ID           INTEGER                not null,
    NAME         CHARACTER VARYING(100) not null,
    DESCRIPTION  CHARACTER VARYING(200) not null,
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATING       INTEGER,
    RATE         INTEGER,
    constraint "FILMS_pk"
        primary key (ID),
    constraint "FILMS_RATING_ID_fk"
        foreign key (RATING) references RATING
);

create table  IF NOT EXISTS GENRE_FILMS
(
    ID_FILM  INTEGER,
    ID_GENRE INTEGER,
    constraint "GENRE_FILMS_FILMS_ID_fk"
        foreign key (ID_FILM) references FILMS,
    constraint "GENRE_FILMS_GENRE_ID_fk"
        foreign key (ID_GENRE) references GENRE
);

create table  IF NOT EXISTS USERS
(
    ID       INTEGER                not null,
    EMAIL    CHARACTER VARYING(100) not null,
    LOGIN    CHARACTER VARYING(100),
    NAME     CHARACTER VARYING(100),
    BIRTHDAY DATE,
    constraint "USERS_pk"
        primary key (ID)
);

create table  IF NOT EXISTS FRIENDS
(
    USER_1 INTEGER not null,
    USER_2 INTEGER not null,
    STATUS BOOLEAN not null,
    constraint FRIENDS_USERS_ID_FK
        foreign key (USER_1) references USERS,
    constraint FRIENDS_USERS_ID_FK_2
        foreign key (USER_2) references USERS
);

create table  IF NOT EXISTS LIKE_FILM
(
    ID      INTEGER not null,
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint "LIKE_FILM_FILMS_ID_fk"
        foreign key (FILM_ID) references FILMS,
    constraint "LIKE_USER_fk"
        foreign key (USER_ID) references USERS
);


