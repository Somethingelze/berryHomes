CREATE TABLE IF NOT EXISTS berryhomes.settings
(
    setting_key   VARCHAR(100) PRIMARY KEY,
    setting_value TEXT NOT NULL,
    updated_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO berryhomes.settings (setting_key, setting_value) VALUES
                                                                        ('site_phone', '(412) 555-0123'),
                                                                        ('site_email', 'info@berryhomespm.com'),
                                                                        ('portal_url', 'https://tenantcloud.com'),
                                                                        ('site_office', 'Pittsburgh, PA 15222'),
                                                                        ('site_hours', 'Mon - Fri: 9:00 AM - 5:00 PM')
ON CONFLICT (setting_key) DO NOTHING;
