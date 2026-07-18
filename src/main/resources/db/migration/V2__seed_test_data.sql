-- 1. ГЕНЕРАЦИЯ ПРОЕКТОВ (10 штук)
INSERT INTO berryhomes.projects (
    id, address, city_zip, purchase_price, monthly_rent, renovation_budget,
    est_noi_annual, total_investment, cash_on_cash_return, est_payback, created_at
)
SELECT
    gen_random_uuid() AS id,
    address_arr[i] AS address,
    'Pittsburgh, PA ' || (15200 + i) AS city_zip,
    '$' || (80000 + (i * 5000)) AS purchase_price,
    '$' || (1200 + (i * 100)) AS monthly_rent,
    '$' || (20000 + (i * 2000)) AS renovation_budget,
    '$' || (10000 + (i * 800)) AS est_noi_annual,
    '$' || (100000 + (i * 7000)) AS total_investment,
    (8 + (i * 0.5)) || '%' AS cash_on_cash_return,
    (12 - (i * 0.3))::numeric(3,1) || ' yrs' AS est_payback,
    CURRENT_TIMESTAMP - (i || ' days')::interval AS created_at
FROM (
         SELECT ARRAY[
                    '123 Main St', '456 Penn Ave', '789 Fifth Ave', '101 Centre Ave',
                    '202 Forbes Ave', '303 Craig St', '404 Baum Blvd', '505 Butler St',
                    '606 Carson St', '707 Smithfield St'
                    ] AS address_arr
     ) f,
     generate_series(1, 10) AS i;


INSERT INTO berryhomes.project_images (id, project_id, file_path, sort_order)
SELECT
    gen_random_uuid(),
    p.id AS project_id,
    '/uploads/images/' || p.id || '/img_' || img_index || '.jpg' AS file_path,
    img_index AS sort_order
FROM berryhomes.projects p
         CROSS JOIN generate_series(1, 3) AS img_index;


-- 3. ГЕНЕРАЦИЯ ДОКУМЕНТОВ
INSERT INTO berryhomes.project_documents (id, project_id, file_path)
SELECT
    gen_random_uuid(),
    p.id AS project_id,
    '/uploads/documents/' || p.id || '_presentation.pdf' AS file_path
FROM berryhomes.projects p;


-- 4. ГЕНЕРАЦИЯ КОНТАКТОВ / ЗАЯВОК (30 штук)
INSERT INTO berryhomes.contacts (id, name, email, phone, type, status, message, created_at)
SELECT
    gen_random_uuid() AS id,
    names[floor(random() * 5 + 1)::int] || ' ' || last_names[floor(random() * 5 + 1)::int] AS name,
    'user_' || i || '@example.com' AS email,
    '+7999' || lpad(i::text, 7, '0') AS phone,
    types[floor(random() * 3 + 1)::int] AS type,
    statuses[floor(random() * 3 + 1)::int] AS status,
    'Здравствуйте! Меня интересуют подробности по вашим объектам недвижимости. Свяжитесь со мной, пожалуйста. Тестовое сообщение №' || i AS message,
    CURRENT_TIMESTAMP - (i || ' hours')::interval AS created_at
FROM (
         SELECT
             ARRAY['Иван', 'Алексей', 'Мария', 'Елена', 'Дмитрий'] AS names,
             ARRAY['Иванов', 'Петров', 'Смирнова', 'Кузнецова', 'Попов'] AS last_names,
             ARRAY['TENANT', 'HOMEOWNER', 'INVESTOR'] AS types,
             ARRAY['NEW', 'IN_PROGRESS', 'CLOSED'] AS statuses
     ) f,
     generate_series(1, 30) AS i;
