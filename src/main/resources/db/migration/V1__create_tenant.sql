CREATE TABLE tb_tenant (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    owner_email VARCHAR(255) NOT NULL UNIQUE,
    tax_id VARCHAR(20) UNIQUE,
    phone VARCHAR(20),
    status VARCHAR(30) NOT NULL DEFAULT 'TRIAL',
    plan VARCHAR(30) NOT NULL DEFAULT 'STARTER',
    baileys_app_name VARCHAR(100),
    trial_ends_at TIMESTAMP,
    subscription_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_tenant_status CHECK (status IN ('TRIAL', 'ACTIVE', 'SUSPENDED', 'CANCELLED', 'EXPIRED')),
    CONSTRAINT chk_tenant_plan CHECK (plan IN ('STARTER', 'PRO', 'BUSINESS'))
);

-- Index for fast lookup by email (login)
CREATE INDEX idx_tenant_owner_email ON tb_tenant(owner_email);

-- Index for filtering active tenants (used in many queries)
CREATE INDEX idx_tenant_status ON tb_tenant(status);

-- Comment documenting the table purpose (visible in DBeaver and pg_dump)
COMMENT ON TABLE tb_tenant IS 'Root entity representing a customer business subscribing to WhatsBotAI';
COMMENT ON COLUMN tb_tenant.baileys_app_name IS 'Fly.io app name for the dedicated Baileys instance of this tenant';
COMMENT ON COLUMN tb_tenant.subscription_id IS 'AbacatePay subscription ID for billing reference';


