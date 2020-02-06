
create table bin_journal (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  cost decimal(19,2),
  price decimal(19,2),
  provider_id integer,
  quantity integer,
  reference varchar(255),
  status integer,
  tenant_id integer not null,
  type integer,
  bin_id bigint not null,
  bin_item_id bigint not null,
  ref_bin_id bigint,
  ref_bin_item_id bigint,
  stock_id bigint not null,
  bin_tx_id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

create table cnf_config (
  config_key varchar(255) not null,
  section varchar(255) not null,
  value varchar(255),
  primary key (config_key, section)
) ENGINE=InnoDB;

create table env_bin_item (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  bin_id bigint,
  cost decimal(19,2),
  held_quantity integer,
  quantity integer,
  tenant_id integer not null,
  stock_id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

create table inv_bin (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  name varchar(255),
  tenant_id integer not null,
  primary key (id)
) ENGINE=InnoDB;

create table inv_bin_tx (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  status integer,
  tenant_id integer not null,
  bin_id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

create table inv_stock (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  code varchar(255),
  cost decimal(19,2),
  description varchar(255),
  image_id varchar(255),
  name varchar(255),
  price decimal(19,2),
  sell_online bit,
  tenant_id integer not null,
  primary key (id)
) ENGINE=InnoDB;

create table lyl_account (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  balance bigint,
  client_id varchar(255) not null,
  description varchar(255),
  tenant_id integer not null,
  primary key (id)
) ENGINE=InnoDB;

create table lyl_ledger (
  id bigint not null auto_increment,
  balance bigint,
  created_time datetime,
  message varchar(255),
  points bigint,
  ref varchar(255),
  ref_type varchar(255),
  type integer,
  account_id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

create table lyl_referral (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  client_id varchar(255) not null,
  display_name varchar(255),
  email varchar(128),
  email_alt varchar(128),
  first_name varchar(255),
  mobile_no varchar(24),
  mobile_no_alt varchar(24),
  referred_client_id varchar(255),
  surname varchar(255),
  tenant_id integer not null,
  primary key (id)
) ENGINE=InnoDB;

create table pty_org (
  long_name varchar(255),
  name varchar(255) not null,
  id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

create table pty_party (
  type varchar(31) not null,
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  country_code varchar(3),
  reg_channel integer,
  role integer,
  tenant_id integer not null,
  party_type varchar(3),
  primary key (id)
) ENGINE=InnoDB;

create table pty_person (
  dob datetime,
  email varchar(128),
  email_alt varchar(128),
  first_name varchar(64) not null,
  gender integer,
  mobile_no varchar(24),
  mobile_no_alt varchar(24),
  ref_tenant_id integer,
  surname varchar(64) not null,
  id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

create table sec_role_def (
  id bigint not null auto_increment,
  code varchar(255) not null,
  description varchar(255) not null,
  name varchar(255) not null,
  status integer not null,
  primary key (id)
) ENGINE=InnoDB;

create table sec_role_del (
  id bigint not null auto_increment,
  del_role_id bigint not null,
  role_id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

create table sec_role_user (
  id bigint not null auto_increment,
  role_id bigint not null,
  tenant_id integer not null,
  user_id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

create table sec_tenant (
  id integer not null,
  country_code varchar(255) not null,
  fqdn varchar(255) not null,
  name varchar(255) not null,
  party_id bigint,
  primary key (id)
) ENGINE=InnoDB;

create table sec_user (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  bin integer not null,
  bio_req bit,
  description varchar(128),
  email varchar(128),
  enc_password varchar(128),
  party_id bigint,
  password_changed datetime,
  status integer not null,
  tenant_id integer not null,
  user_name varchar(32),
  primary key (id)
) ENGINE=InnoDB;

create table seq_seq (
  id bigint not null auto_increment,
  seq_name varchar(255),
  seq integer,
  primary key (id)
) ENGINE=InnoDB;

create table seq_tenant (
  id bigint not null auto_increment,
  seq_name varchar(255),
  seq integer,
  tenant_id integer not null,
  primary key (id)
) ENGINE=InnoDB;

create table vch_batch (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  sequence_count integer not null,
  sequence_start integer not null,
  status integer not null,
  tenant_id integer not null,
  voucher_type_id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

create table vch_promo (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  description varchar(255),
  name varchar(255) not null,
  tenant_id integer not null,
  primary key (id)
) ENGINE=InnoDB;

create table vch_type (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  color varchar(255),
  description varchar(255),
  enabled bit not null,
  expire_months integer not null,
  message varchar(255),
  name varchar(255) not null,
  partner_id bigint,
  sell_online bit not null,
  tenant_id integer not null,
  value bigint not null,
  value_type integer not null,
  promotion_id bigint,
  primary key (id)
) ENGINE=InnoDB;

create table vch_voucher (
  id bigint not null auto_increment,
  created_by varchar(255),
  created_time datetime,
  updated_time datetime,
  updated_by varchar(255),
  client_id varchar(255),
  expires date not null,
  message varchar(255),
  name varchar(255) not null,
  number bigint not null,
  partner_id bigint,
  status integer not null,
  tenant_id integer not null,
  value bigint not null,
  value_type integer not null,
  promotion_id bigint,
  voucher_type_id bigint not null,
  primary key (id)
) ENGINE=InnoDB;

alter table env_bin_item
  add constraint UK_env_bin_item_stock_cost unique (tenant_id, bin_id, stock_id, cost);

alter table inv_stock
  add constraint UK_inv_stock_code unique (tenant_id, code);

alter table lyl_referral
  add constraint lyl_referral_email unique (tenant_id, email);

alter table lyl_referral
  add constraint lyl_referral_email_alt unique (tenant_id, email_alt);

alter table lyl_referral
  add constraint lyl_referral_email_mobile unique (tenant_id, mobile_no);

alter table lyl_referral
  add constraint lyl_referral_email_mobile_alt unique (tenant_id, mobile_no_alt);

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

alter table vch_promo
  add constraint UK_vch_promo_name unique (tenant_id, name);

alter table bin_journal
  add constraint FK_bin_journal_bin
foreign key (bin_id)
references inv_bin (id);

alter table bin_journal
  add constraint FK_bin_journal_bin_item
foreign key (bin_item_id)
references env_bin_item (id);

alter table bin_journal
  add constraint FK_bin_journal_ref_bin
foreign key (ref_bin_id)
references inv_bin (id);

alter table bin_journal
  add constraint FK_bin_journal_ref_bin_item
foreign key (ref_bin_item_id)
references env_bin_item (id);

alter table bin_journal
  add constraint FK_bin_journal_stock
foreign key (stock_id)
references inv_stock (id);

alter table bin_journal
  add constraint FK_bin_journal_tx
foreign key (bin_tx_id)
references inv_bin_tx (id);

alter table env_bin_item
  add constraint FK_bin_item_stock
foreign key (stock_id)
references inv_stock (id);

alter table inv_bin_tx
  add constraint FK_inv_bin_tx_bin
foreign key (bin_id)
references inv_bin (id);

alter table lyl_ledger
  add constraint FK_lyl_ledger_account
foreign key (account_id)
references lyl_account (id);

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

alter table vch_batch
  add constraint FK_vch_batch_type
foreign key (voucher_type_id)
references vch_type (id);

alter table vch_type
  add constraint FK_vch_type_promotion
foreign key (promotion_id)
references vch_promo (id);

alter table vch_voucher
  add constraint FK_vch_voucher_promo
foreign key (promotion_id)
references vch_promo (id);

alter table vch_voucher
  add constraint FK_vch_voucher_type
foreign key (voucher_type_id)
references vch_type (id);