# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table twist (
  id                        varchar(255) primary key,
  name                      varchar(255),
  image_url                 varchar(255),
  char_id                   varchar(255),
  like_count                integer,
  neutral_count             integer,
  dislike_count             integer)
;




# --- !Downs

PRAGMA foreign_keys = OFF;

drop table twist;

PRAGMA foreign_keys = ON;

