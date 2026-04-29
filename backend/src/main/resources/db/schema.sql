-- 创建碳足迹追踪平台数据库
CREATE DATABASE IF NOT EXISTS carbonfootprint CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE carbonfootprint;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('INDIVIDUAL', 'ENTERPRISE', 'ADMIN') NOT NULL DEFAULT 'INDIVIDUAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 交通排放表
CREATE TABLE IF NOT EXISTS transport_emissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    transport_type VARCHAR(50) NOT NULL,
    distance DECIMAL(10,2) NOT NULL,
    fuel_type VARCHAR(50),
    fuel_consumption DECIMAL(10,2),
    emission_amount DECIMAL(10,2) NOT NULL,
    emission_date DATE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_transport_user_id (user_id),
    INDEX idx_transport_date (emission_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 饮食排放表
CREATE TABLE IF NOT EXISTS diet_emissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    food_type VARCHAR(50) NOT NULL,
    specific_food VARCHAR(100) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    cooking_method VARCHAR(50),
    emission_amount DECIMAL(10,2) NOT NULL,
    emission_date DATE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_diet_user_id (user_id),
    INDEX idx_diet_date (emission_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用电排放表
CREATE TABLE IF NOT EXISTS electricity_emissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    power DECIMAL(10,2) NOT NULL,
    usage_time DECIMAL(10,2) NOT NULL,
    usage_days INT NOT NULL,
    electricity_amount DECIMAL(10,2) NOT NULL,
    emission_amount DECIMAL(10,2) NOT NULL,
    emission_date DATE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_electricity_user_id (user_id),
    INDEX idx_electricity_date (emission_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 碳足迹汇总表
CREATE TABLE IF NOT EXISTS footprint_summary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    period ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY') NOT NULL,
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,
    transport_emission DOUBLE DEFAULT 0,
    diet_emission DOUBLE DEFAULT 0,
    electricity_emission DOUBLE DEFAULT 0,
    total_emission DOUBLE DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_summary_user_id (user_id),
    INDEX idx_summary_period (period, period_start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 预测历史表
CREATE TABLE IF NOT EXISTS carbon_prediction_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_month VARCHAR(7) NOT NULL,
    prediction_date DATE NOT NULL,
    predicted_emission DOUBLE NOT NULL,
    confidence DOUBLE NOT NULL,
    trend VARCHAR(100),
    actual_emission DOUBLE,
    absolute_error DOUBLE,
    error_rate DOUBLE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_prediction_user_target_month UNIQUE (user_id, target_month),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 碳行动计划表
CREATE TABLE IF NOT EXISTS action_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category ENUM('TRANSPORT', 'DIET', 'ELECTRICITY', 'OTHER') NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    impact DECIMAL(10,2) NOT NULL,
    difficulty ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL,
    cost ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_action_plan_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户行动计划关联表
CREATE TABLE IF NOT EXISTS user_action_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action_plan_id BIGINT NOT NULL,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
    adopted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (action_plan_id) REFERENCES action_plans(id) ON DELETE CASCADE,
    INDEX idx_user_action_plans_user_id (user_id),
    INDEX idx_user_action_plans_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
