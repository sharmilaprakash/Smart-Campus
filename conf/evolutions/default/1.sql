# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table beacon (
  id                        varchar(255) not null,
  description               varchar(255),
  location_id               integer,
  constraint pk_beacon primary key (id))
;

create table beacon_category (
  beacon_id                 varchar(255) not null,
  category                  varchar(255),
  constraint pk_beacon_category primary key (beacon_id))
;

create table category (
  id                        integer auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_category primary key (id))
;

create table event (
  id                        integer auto_increment not null,
  name                      varchar(255),
  location                  varchar(255),
  start_time                datetime(6),
  end_time                  datetime(6),
  description               varchar(255),
  category                  varchar(255),
  external_link             varchar(255),
  is_active                 tinyint(1) default 0,
  created_by                varchar(255),
  constraint pk_event primary key (id))
;

create table location (
  id                        integer auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_location primary key (id))
;

create table user (
  device_id                 varchar(255) not null,
  user_name                 varchar(255),
  role                      varchar(255),
  categories                varchar(255),
  constraint pk_user primary key (device_id))
;


create table beacon_events (
  beacon_id                      varchar(255) not null,
  event_id                       integer not null,
  constraint pk_beacon_events primary key (beacon_id, event_id))
;

create table user_events (
  event_id                       integer not null,
  user_device_id                 varchar(255) not null,
  constraint pk_user_events primary key (event_id, user_device_id))
;
alter table beacon add constraint fk_beacon_location_1 foreign key (location_id) references location (id) on delete restrict on update restrict;
create index ix_beacon_location_1 on beacon (location_id);



alter table beacon_events add constraint fk_beacon_events_beacon_01 foreign key (beacon_id) references beacon (id) on delete restrict on update restrict;

alter table beacon_events add constraint fk_beacon_events_event_02 foreign key (event_id) references event (id) on delete restrict on update restrict;

alter table user_events add constraint fk_user_events_event_01 foreign key (event_id) references event (id) on delete restrict on update restrict;

alter table user_events add constraint fk_user_events_user_02 foreign key (user_device_id) references user (device_id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table beacon;

drop table beacon_events;

drop table beacon_category;

drop table category;

drop table event;

drop table user_events;

drop table location;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

