create table rle_group
(
    group_id  bigint       not null,
    tenant_id integer      not null,
    version   integer      not null,
    name      varchar(255) not null,
    parent_id bigint       not null,
    primary key (tenant_id, version, group_id)
) engine = InnoDB;

create table rle_repo
(
    tenant_id         integer      not null,
    version           integer      not null,
    created_by        varchar(255),
    created_time      datetime,
    updated_time      datetime,
    updated_by        varchar(255),
    current_version   integer      not null,
    description       varchar(255) not null,
    published_version integer      not null,
    primary key (tenant_id, version)
) engine = InnoDB;

create table rle_schema_column
(
    column_name  varchar(255) not null,
    dataset_name varchar(255) not null,
    tenant_id    integer      not null,
    version      integer      not null,
    column_type  integer      not null,
    primary key (tenant_id, version, dataset_name, column_name)
) engine = InnoDB;

create table rle_term
(
    type             varchar(31)  not null,
    callable_id      varchar(255) not null,
    tenant_id        integer      not null,
    version          integer      not null,
    aliases          varchar(255),
    created_by       varchar(255),
    created_time     datetime,
    updated_time     datetime,
    updated_by       varchar(255),
    code             longtext     not null,
    dependencies     varchar(255),
    documentation    varchar(255) not null,
    group_id         bigint       not null,
    language         varchar(255) not null,
    name             varchar(255) not null,
    term_type        varchar(4),
    enabled          bit          not null,
    inverse          bit          not null,
    last_tested_by   varchar(255),
    last_tested_time datetime,
    mapped_code      varchar(255),
    message          varchar(255),
    priority         integer      not null,
    rule_code        varchar(255) not null,
    severity         integer      not null,
    test_id          bigint       not null,
    test_successful  bit          not null,
    primary key (tenant_id, version, callable_id)
) engine = InnoDB;

create table rle_test
(
    test_id      bigint       not null,
    tenant_id    integer      not null,
    version      integer      not null,
    created_by   varchar(255),
    created_time datetime,
    updated_time datetime,
    updated_by   varchar(255),
    inverse      bit          not null,
    name         varchar(255) not null,
    callable_id  varchar(255) not null,
    primary key (tenant_id, version, test_id)
) engine = InnoDB;

create table rle_test_data
(
    dataset_name  varchar(255) not null,
    test_id       bigint       not null,
    tenant_id     integer      not null,
    version       integer      not null,
    column_values varchar(255),
    primary key (tenant_id, version, test_id, dataset_name)
) engine = InnoDB;

