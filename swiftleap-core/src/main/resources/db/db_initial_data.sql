insert
into vch_type
(`color`, `description`, `enabled`, `expire_months`, `message`, `name`, `sell_online`, `tenant_id`, `value`, `value_type`)
values
  ('black', 'Referral made a purchased', 1, 6, 'Thank you for referring.', 'Refer Voucher', 0, 0, 30, 0),
  ('silver', 'Registered online or mobile', 1, 6, 'Thank you for registering.', 'Register Voucher', 0, 0, 10, 0);

insert
into cnf_config
(`section`, `config_key`, `value`)
values
  ('tenant:0', 'referral.loyalty.points', '100'),
  ('tenant:0', 'register.loyalty.points', '100'),
  ('tenant:0', 'register.client.voucher.type', '2'),
  ('tenant:0', 'referral.client.voucher.type', '1');
