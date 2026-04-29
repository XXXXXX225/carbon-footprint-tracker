SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE footprint_summary;
TRUNCATE TABLE carbon_prediction_history;
TRUNCATE TABLE electricity_emissions;
TRUNCATE TABLE diet_emissions;
TRUNCATE TABLE transport_emissions;
TRUNCATE TABLE user_action_plans;
TRUNCATE TABLE action_plans;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, username, password, name, email, role, created_at, updated_at) VALUES
(1, 'testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iK7OGq', '测试用户', 'test@example.com', 'INDIVIDUAL', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

UPDATE users SET total_points = 3500 WHERE id = 1;

INSERT INTO action_plans (title, description, category, difficulty, impact, cost, created_at) VALUES
('公共交通出行', '建议使用公交或地铁，减少私家车使用，显著降低个人碳足迹。', 'TRANSPORT', 'MEDIUM', 30.5, 'LOW', NOW()),
('降低红肉摄入', '减少牛肉等红肉摄入，增加蔬菜谷物比例，有效减少温室气体排放。', 'DIET', 'MEDIUM', 25.0, 'MEDIUM', NOW()),
('优化空调使用', '夏季空调控制在26度，使用节能模式，从源头减少电力资源浪费。', 'ELECTRICITY', 'LOW', 15.0, 'LOW', NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);
