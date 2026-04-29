SET FOREIGN_KEY_CHECKS = 0;

RENAME TABLE recommendations TO action_plans;
RENAME TABLE user_recommendations TO user_action_plans;

ALTER TABLE user_action_plans
    CHANGE COLUMN recommendation_id action_plan_id BIGINT NOT NULL;

SET FOREIGN_KEY_CHECKS = 1;