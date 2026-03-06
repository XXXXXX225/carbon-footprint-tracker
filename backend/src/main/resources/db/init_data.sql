-- 数据初始化脚本

-- 插入测试用户
INSERT INTO users (username, password, name, email, role, created_at, updated_at) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iK7OGq', '测试用户', 'test@example.com', 'INDIVIDUAL', NOW(), NOW()),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iK7OGq', '管理员', 'admin@example.com', 'ENTERPRISE', NOW(), NOW()),
('root', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iK7OGq', '超级管理员', 'root@example.com', 'ADMIN', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  password = VALUES(password),
  role = VALUES(role),
  updated_at = NOW();

-- 插入交通排放记录（用户ID=1）
INSERT INTO transport_emissions (user_id, transport_type, distance, fuel_type, fuel_consumption, emission_amount, emission_date, description, created_at) VALUES
(1, 5, 15.5, '汽油', 8.5, 3.255, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '上班通勤', NOW()),
(1, 5, 12.3, '汽油', 7.8, 2.583, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '购物出行', NOW()),
(1, 2, 8.0, NULL, NULL, 0.712, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '地铁通勤', NOW()),
(1, 5, 20.0, '汽油', 9.2, 4.2, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '周末出行', NOW()),
(1, 3, 5.5, NULL, NULL, 0.4895, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '公交出行', NOW()),
(1, 5, 18.7, '汽油', 8.9, 3.927, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '接送孩子', NOW()),
(1, 2, 10.0, NULL, NULL, 0.89, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '地铁通勤', NOW()),
(1, 5, 14.2, '汽油', 7.5, 2.982, DATE_SUB(CURDATE(), INTERVAL 8 DAY), '就医出行', NOW()),
(1, 1, 3.0, NULL, NULL, 0, DATE_SUB(CURDATE(), INTERVAL 9 DAY), '步行', NOW()),
(1, 5, 16.8, '汽油', 8.2, 3.528, DATE_SUB(CURDATE(), INTERVAL 10 DAY), '商务出行', NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 插入饮食排放记录（用户ID=1）
INSERT INTO diet_emissions (user_id, food_type, specific_food, amount, cooking_method, emission_amount, emission_date, description, created_at) VALUES
(1, 0, '牛肉', 0.3, '煎炒', 16.5, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '晚餐', NOW()),
(1, 3, '鸡肉', 0.4, '炖煮', 2.4, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '午餐', NOW()),
(1, 1, '羊肉', 0.2, '烧烤', 8.2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '聚餐', NOW()),
(1, 7, '蔬菜', 0.5, '清炒', 0.75, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '晚餐', NOW()),
(1, 2, '猪肉', 0.35, '红烧', 5.25, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '午餐', NOW()),
(1, 8, '水果', 0.3, NULL, 0.6, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '早餐', NOW()),
(1, 0, '牛肉', 0.25, '煎炒', 13.75, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '晚餐', NOW()),
(1, 4, '鱼肉', 0.4, '清蒸', 1.6, DATE_SUB(CURDATE(), INTERVAL 8 DAY), '午餐', NOW()),
(1, 10, '谷物', 0.6, NULL, 1.2, DATE_SUB(CURDATE(), INTERVAL 9 DAY), '早餐', NOW()),
(1, 11, '豆类', 0.4, '炖煮', 0.8, DATE_SUB(CURDATE(), INTERVAL 10 DAY), '晚餐', NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 插入用电排放记录（用户ID=1）
INSERT INTO electricity_emissions (user_id, device_type, power, usage_time, usage_days, electricity_amount, emission_amount, emission_date, description, created_at) VALUES
(1, '空调', 1500, 8, 30, 360.0, 194.4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '夏季空调使用', NOW()),
(1, '冰箱', 150, 24, 30, 108.0, 58.32, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '日常使用', NOW()),
(1, '洗衣机', 500, 1, 8, 4.0, 2.16, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '洗衣', NOW()),
(1, '电视', 200, 4, 30, 24.0, 12.96, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '娱乐', NOW()),
(1, '热水器', 2000, 1, 30, 60.0, 32.4, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '洗澡', NOW()),
(1, '微波炉', 800, 0.5, 15, 6.0, 3.24, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '加热食物', NOW()),
(1, '空调', 1500, 6, 30, 270.0, 145.8, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '夏季空调使用', NOW()),
(1, '电饭煲', 800, 1, 30, 24.0, 12.96, DATE_SUB(CURDATE(), INTERVAL 8 DAY), '煮饭', NOW()),
(1, '电脑', 300, 5, 30, 45.0, 24.3, DATE_SUB(CURDATE(), INTERVAL 9 DAY), '办公', NOW()),
(1, '照明', 100, 6, 30, 18.0, 9.72, DATE_SUB(CURDATE(), INTERVAL 10 DAY), '日常照明', NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 插入碳足迹汇总数据（用户ID=1）
INSERT INTO footprint_summaries (user_id, period, start_date, end_date, total_emission, transport_emission, diet_emission, electricity_emission, average_daily_emission, record_count, created_at) VALUES
(1, 'WEEK', DATE_SUB(CURDATE(), INTERVAL 7 DAY), CURDATE(), 25.5, 22.0, 2.5, 0.0, 3.64, 7, NOW()),
(1, 'MONTH', DATE_SUB(CURDATE(), INTERVAL 30 DAY), CURDATE(), 108.5, 22.0, 51.4, 35.1, 3.62, 30, NOW()),
(1, 'YEAR', DATE_SUB(CURDATE(), INTERVAL 365 DAY), CURDATE(), 1302.0, 264.0, 616.8, 421.2, 3.57, 365, NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 插入减排建议
INSERT INTO recommendations (category, title, description, impact, difficulty, cost, created_at) VALUES
('TRANSPORT', '使用公共交通工具', '将私家车出行替换为公共汽车或地铁，每周至少3天，预计每月可减少排放25 kg CO₂e', 25.0, 'MEDIUM', 'LOW', NOW()),
('TRANSPORT', '3公里内步行或骑行', '对于3公里内的短途出行，选择步行或骑行，预计每月可减少排放15 kg CO₂e', 15.0, 'LOW', 'LOW', NOW()),
('TRANSPORT', '拼车出行', '与同事或邻居拼车出行，减少单独驾驶次数，预计每月可减少排放20 kg CO₂e', 20.0, 'MEDIUM', 'LOW', NOW()),
('DIET', '每周增加2顿素食', '每周至少有2天选择素食，减少牛肉和羊肉的摄入，预计每月可减少排放30 kg CO₂e', 30.0, 'LOW', 'LOW', NOW()),
('DIET', '减少食物浪费', '合理规划采购，避免食物浪费，预计每月可减少排放10 kg CO₂e', 10.0, 'LOW', 'LOW', NOW()),
('DIET', '选择本地食材', '优先选择本地生产的食材，减少运输排放，预计每月可减少排放8 kg CO₂e', 8.0, 'LOW', 'LOW', NOW()),
('ELECTRICITY', '更换LED灯泡', '将所有传统灯泡更换为LED节能灯泡，预计每月可减少排放8 kg CO₂e', 8.0, 'LOW', 'MEDIUM', NOW()),
('ELECTRICITY', '使用节能电器', '选择高能效等级的电器，预计每月可减少排放15 kg CO₂e', 15.0, 'LOW', 'MEDIUM', NOW()),
('ELECTRICITY', '错峰用电', '避开用电高峰时段使用大功率电器，预计每月可减少排放12 kg CO₂e', 12.0, 'LOW', 'LOW', NOW()),
('ELECTRICITY', '智能插座管理', '使用智能插座管理待机功耗，预计每月可减少排放5 kg CO₂e', 5.0, 'LOW', 'LOW', NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 插入用户建议关联（用户ID=1）
INSERT INTO user_recommendations (user_id, recommendation_id, status, created_at) VALUES
(1, 1, 'PENDING', NOW()),
(1, 2, 'PENDING', NOW()),
(1, 4, 'PENDING', NOW()),
(1, 7, 'PENDING', NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();