-- 创建碳足迹追踪平台数据库
CREATE DATABASE IF NOT EXISTS carbon_footprint;

USE carbon_footprint;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('INDIVIDUAL', 'ENTERPRISE', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 减排建议表
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category ENUM('TRANSPORT', 'DIET', 'ELECTRICITY', 'OTHER') NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    impact DECIMAL(10,2) NOT NULL,
    difficulty ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL,
    cost ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 用户建议关联表
CREATE TABLE IF NOT EXISTS user_recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recommendation_id BIGINT NOT NULL,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recommendation_id) REFERENCES recommendations(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_transport_user_id ON transport_emissions(user_id);
CREATE INDEX idx_transport_date ON transport_emissions(emission_date);
CREATE INDEX idx_diet_user_id ON diet_emissions(user_id);
CREATE INDEX idx_diet_date ON diet_emissions(emission_date);
CREATE INDEX idx_electricity_user_id ON electricity_emissions(user_id);
CREATE INDEX idx_electricity_date ON electricity_emissions(emission_date);
CREATE INDEX idx_summary_user_id ON footprint_summary(user_id);
CREATE INDEX idx_summary_period ON footprint_summary(period, period_start_date);
CREATE INDEX idx_user_recommendations_user_id ON user_recommendations(user_id);
CREATE INDEX idx_user_recommendations_status ON user_recommendations(status);

-- 插入一些基础的减排建议数据
INSERT INTO recommendations (category, title, description, impact, difficulty, cost)
VALUES
('TRANSPORT', '每周步行或骑行2次', '对于3公里内的短途出行，选择步行或骑行代替交通工具，减少碳排放', 5.2, 'LOW', 'LOW'),
('TRANSPORT', '使用公共交通工具', '每周使用公共交通工具代替私家车1-2次，减少交通拥堵和碳排放', 8.7, 'MEDIUM', 'LOW'),
('DIET', '每周增加2顿素食', '减少肉类消费，每周增加2顿素食，降低饮食碳足迹', 7.5, 'LOW', 'LOW'),
('DIET', '减少食物浪费', '合理规划采购，使用剩余食材，减少食物浪费', 3.2, 'LOW', 'LOW'),
('ELECTRICITY', '使用节能电器', '更换为节能灯具和电器，减少待机功耗', 4.8, 'MEDIUM', 'MEDIUM'),
('ELECTRICITY', '调整空调温度', '夏季空调设置26°C，冬季20°C，减少能源消耗', 6.3, 'LOW', 'LOW'),
('OTHER', '参与碳抵消项目', '通过植树或购买碳信用额度来抵消无法减少的碳排放', 10.0, 'HIGH', 'HIGH'),
('OTHER', '推广环保理念', '向家人朋友宣传低碳生活方式，扩大环保影响', 2.5, 'LOW', 'LOW');