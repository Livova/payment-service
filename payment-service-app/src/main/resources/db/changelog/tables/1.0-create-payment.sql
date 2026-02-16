--liquibase formatted sql

--changeset tiger:1.0-create-payment

create table payment (
  guid uuid not null primary key,
  inquiry_ref_id uuid not null,
  amount numeric(5,2) not null,
  currency varchar(3) not null,
  transaction_ref_id uuid,
  status varchar(255) not null,
  note varchar(255) not null,
  created_at timestamp with time zone not null,
  updated_at timestamp with time zone not null
);