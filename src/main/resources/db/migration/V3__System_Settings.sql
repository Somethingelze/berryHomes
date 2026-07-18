CREATE TABLE IF NOT EXISTS berryhomes.system_settings
(
    setting_key   VARCHAR(100) PRIMARY KEY,
    setting_value TEXT NOT NULL,
    updated_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO berryhomes.system_settings (setting_key, setting_value) VALUES
                                                                        ('company_phone', '(412) 555-0123'),
                                                                        ('company_email', 'info@berryhomespm.com'),
                                                                        ('tenant_portal_url', 'https://tenantcloud.com'),
                                                                        ('owner_portal_url', 'https://tenantcloud.comowner-portal')
ON CONFLICT (setting_key) DO NOTHING;
