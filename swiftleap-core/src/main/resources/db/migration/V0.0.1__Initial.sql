create table cnf_config
(
    config_key varchar(255) not null,
    section    varchar(255) not null,
    value      varchar(255),
    primary key (config_key, section)
) ENGINE = InnoDB;


create table pty_org
(
    long_name varchar(255),
    name      varchar(255) not null,
    id        bigint       not null,
    primary key (id)
) ENGINE = InnoDB;

create table pty_party
(
    type         varchar(31) not null,
    id           bigint      not null auto_increment,
    created_by   varchar(255),
    created_time datetime,
    updated_time datetime,
    updated_by   varchar(255),
    country_code varchar(3),
    reg_channel  integer,
    role         integer,
    tenant_id    integer     not null,
    party_type   varchar(3),
    primary key (id)
) ENGINE = InnoDB;

create table pty_person
(
    dob           datetime,
    email         varchar(128),
    email_alt     varchar(128),
    first_name    varchar(64) not null,
    gender        integer,
    mobile_no     varchar(24),
    mobile_no_alt varchar(24),
    ref_tenant_id integer,
    surname       varchar(64) not null,
    id            bigint      not null,
    primary key (id)
) ENGINE = InnoDB;

create table sec_role_def
(
    id          bigint       not null auto_increment,
    code        varchar(255) not null,
    description varchar(255) not null,
    name        varchar(255) not null,
    status      integer      not null,
    primary key (id)
) ENGINE = InnoDB;

create table sec_role_del
(
    id          bigint not null auto_increment,
    del_role_id bigint not null,
    role_id     bigint not null,
    primary key (id)
) ENGINE = InnoDB;

create table sec_role_user
(
    id        bigint  not null auto_increment,
    role_id   bigint  not null,
    tenant_id integer not null,
    user_id   bigint  not null,
    primary key (id)
) ENGINE = InnoDB;

create table sec_tenant
(
    id           integer      not null,
    country_code varchar(255) not null,
    fqdn         varchar(255),
    name         varchar(255) not null,
    party_id     bigint,
    primary key (id)
) ENGINE = InnoDB;

create table sec_user
(
    id               bigint  not null auto_increment,
    created_by       varchar(255),
    created_time     datetime,
    updated_time     datetime,
    updated_by       varchar(255),
    bin              integer not null,
    bio_req          bit,
    description      varchar(128),
    email            varchar(128),
    enc_password     varchar(128),
    party_id         bigint,
    password_changed datetime,
    status           integer not null,
    tenant_id        integer not null,
    user_name        varchar(32),
    primary key (id)
) ENGINE = InnoDB;

create table seq_seq
(
    id       bigint not null auto_increment,
    seq_name varchar(255),
    seq      integer,
    primary key (id)
) ENGINE = InnoDB;

create table seq_tenant
(
    id        bigint  not null auto_increment,
    seq_name  varchar(255),
    seq       integer,
    tenant_id integer not null,
    primary key (id)
) ENGINE = InnoDB;



alter table pty_person
    add constraint pty_person_email unique (ref_tenant_id, email);

alter table pty_person
    add constraint pty_person_email_alt unique (ref_tenant_id, email_alt);

alter table sec_role_def
    add constraint UK_sec_role_def_code unique (code);

alter table sec_role_del
    add constraint UK_sec_role_del_role_id unique (del_role_id, role_id);

alter table sec_tenant
    add constraint UK_sec_tenant_fqd unique (fqdn);

alter table sec_user
    add constraint UK_sec_user_user_name unique (tenant_id, user_name);

alter table sec_user
    add constraint UK_sec_user_email unique (tenant_id, email);

alter table seq_seq
    add constraint UK_seq_seq unique (seq_name);

alter table seq_tenant
    add constraint UK_seq_seq_seq unique (tenant_id, seq_name);



alter table pty_org
    add constraint FKpbvrd5a9f5dv0wgnx35g00tho
        foreign key (id)
            references pty_party (id);

alter table pty_person
    add constraint FKi2yvvwa84grjag1wxhd3yh3w0
        foreign key (id)
            references pty_party (id);

alter table sec_role_del
    add constraint FK_sec_role_del_del
        foreign key (del_role_id)
            references sec_role_def (id);

alter table sec_role_del
    add constraint FK_sec_role_del_role
        foreign key (role_id)
            references sec_role_def (id);

alter table sec_role_user
    add constraint FK_sec_role_user_role
        foreign key (role_id)
            references sec_role_def (id);

alter table sec_role_user
    add constraint FK_sec_role_user_user
        foreign key (user_id)
            references sec_user (id);
