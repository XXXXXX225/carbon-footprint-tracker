-- 修复admin用户角色为ADMIN
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';

-- 确保root用户也是ADMIN角色
UPDATE users SET role = 'ADMIN' WHERE username = 'root';

-- 查看修复结果
SELECT username, role FROM users WHERE username IN ('admin', 'root');
