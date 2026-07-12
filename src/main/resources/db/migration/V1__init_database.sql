CREATE TABLE IF NOT EXISTS public.products
(
    id               UUID PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    price            DECIMAL(10, 2),
    sale             DECIMAL(10, 2),
    quantity         INTEGER,
    reservedQuantity INTEGER
);


