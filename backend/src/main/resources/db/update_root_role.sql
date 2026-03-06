-- 更新 root 用户为 ADMIN 角色
UPDATE users SET role = 'ADMIN' WHERE username = 'root';
