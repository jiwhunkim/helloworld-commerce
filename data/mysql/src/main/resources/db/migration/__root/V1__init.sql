create table if not exists event_publication
(
  id                     varchar(36) not null,
  listener_id            varchar(512) not null,
  event_type             varchar(512) not null,
  serialized_event       varchar(4000) not null,
  publication_date       timestamp(6) not null,
  completion_date        timestamp(6) default null null,
  status                 varchar(20),
  completion_attempts    int,
  last_resubmission_date timestamp(6) default null null,
  primary key (id),
  index event_publication_by_completion_date_idx (completion_date)
  );
