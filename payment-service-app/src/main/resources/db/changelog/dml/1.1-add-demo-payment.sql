--liquibase formatted sql

--changeset tiger:1.1-add-demo-payment
insert into payment (
    guid,
    inquiry_ref_id,
    amount,
    currency,
    transaction_ref_id,
    status,
    note,
    created_at,
    updated_at
) values (
             'ac328a1a-1e60-4dd3-bee5-ed573d74c841',
             '607ed0ea-cb8a-4ff8-a694-1213c314e65c',
             99.99,
             'USD',
             'f113e373-b7b0-4f38-abf6-ccc3a89b8236',
             'RECEIVED',
             'Initial test payment',
             '2025-01-01T12:00:00Z',
             '2025-01-01T12:00:00Z'
         );