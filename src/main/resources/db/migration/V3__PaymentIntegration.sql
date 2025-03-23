ALTER TABLE `order`
    ADD invoice_number VARCHAR(255) NULL;

ALTER TABLE `order`
    ADD payment_id VARCHAR(255) NULL;

ALTER TABLE `order`
    ADD payment_link VARCHAR(255) NULL;

ALTER TABLE `order`
    ADD payment_order_id VARCHAR(255) NULL;

ALTER TABLE `order`
    ADD refund_id VARCHAR(255) NULL;

ALTER TABLE `order`
DROP
COLUMN transaction_id;