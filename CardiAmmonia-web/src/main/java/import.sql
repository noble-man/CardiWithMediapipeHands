INSERT INTO roles (role_id, description) VALUES  ('ROLE_RADIOPHARMACIST', 'Radiopharmacist');
INSERT INTO roles (role_id, description) VALUES  ('ROLE_ADMIN', 'Administrator');
INSERT INTO roles (role_id, description) VALUES  ('ROLE_TECHNOLOGIST', 'Technologist');
INSERT INTO roles (role_id, description) VALUES  ('ROLE_TECHNICIAN', 'Technician');
--INSERT INTO roles (role_id, description) VALUES  ('ROLE_VISITOR', 'Visitor');

	
INSERT INTO permission (permission_id, description) VALUES  ('START_END_BATCH', 'start and end a batch');
INSERT INTO permission (permission_id, description) VALUES  ('PAUSE_STOP_RESUME_SUBBATCH', 'Pause, stop and resume a sub-batch');
INSERT INTO permission (permission_id, description) VALUES  ('MONITOR', 'Monitor the system parameters, status, alarms and error messages');
INSERT INTO permission (permission_id, description) VALUES  ('QP_RELAESE_QUERY_QC_HISTORY', 'Release the QP and view history of QPs ');
INSERT INTO permission (permission_id, description) VALUES  ('ENCODE_QC_RESULT', 'Encode QC results for a batch');
INSERT INTO permission (permission_id, description) VALUES  ('VIEW_BATCH_RECORD', 'View the batch record');
INSERT INTO permission (permission_id, description) VALUES  ('SEND_TO_WASTE', 'Send the sub-batch to the waste');
INSERT INTO permission (permission_id, description) VALUES  ('START_SUBBATCH', 'start a sub-batch');
INSERT INTO permission (permission_id, description) VALUES  ('ROUTE_TO_QC', 'Send the sub-batch to the QC machine');
INSERT INTO permission (permission_id, description) VALUES  ('MANAGE_USERS', 'Manage users');
--INSERT INTO permission (permission_id, description) VALUES  ('MANAGE_PROFILE', 'Manage own profile');


INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_RADIOPHARMACIST', 'START_END_BATCH');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_RADIOPHARMACIST', 'MONITOR');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_RADIOPHARMACIST', 'QP_RELAESE_QUERY_QC_HISTORY');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_RADIOPHARMACIST', 'ENCODE_QC_RESULT');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_RADIOPHARMACIST', 'VIEW_BATCH_RECORD');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_RADIOPHARMACIST', 'START_SUBBATCH');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_RADIOPHARMACIST', 'ROUTE_TO_QC');
--INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_RADIOPHARMACIST', 'MANAGE_PROFILE');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_ADMIN', 'MANAGE_USERS');
--INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_ADMIN', 'MANAGE_PROFILE');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNOLOGIST', 'START_END_BATCH');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNOLOGIST', 'PAUSE_STOP_RESUME_SUBBATCH');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNOLOGIST', 'MONITOR');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNOLOGIST', 'ENCODE_QC_RESULT');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNOLOGIST', 'SEND_TO_WASTE');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNOLOGIST', 'START_SUBBATCH');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNOLOGIST', 'ROUTE_TO_QC');
--INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNOLOGIST', 'MANAGE_PROFILE');
--INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNICIAN', 'MONITOR');
INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_TECHNICIAN', 'MANAGE_PROFILE');
--INSERT INTO role_permission (role_id, permission_id) VALUES  ('ROLE_VISITOR', 'MANAGE_PROFILE');

INSERT INTO restricted_permission (permission_id, role_id, restriction_type) VALUES  ('VIEW_BATCH_RECORD', 'ROLE_TECHNOLOGIST',0);--Not recommended
INSERT INTO restricted_permission (permission_id, role_id, restriction_type) VALUES  ('QP_RELAESE_QUERY_QC_HISTORY', 'ROLE_TECHNOLOGIST',-1);--Not possible/error




INSERT INTO users ( username, password,role_id,enabled) VALUES ('radio', '$2a$10$iYeQUOBt4iRNBTDYD2/oLuAoxEKd.FjkTmewok9kU.kALhqM2fV16','ROLE_RADIOPHARMACIST',true);
INSERT INTO users ( username, password, role_id,enabled) VALUES ('admin', '$2a$10$iYeQUOBt4iRNBTDYD2/oLuAoxEKd.FjkTmewok9kU.kALhqM2fV16', 'ROLE_ADMIN',true);
INSERT INTO users ( username, password,role_id,enabled) VALUES ('nurse', '$2a$10$iYeQUOBt4iRNBTDYD2/oLuAoxEKd.FjkTmewok9kU.kALhqM2fV16','ROLE_TECHNOLOGIST',true);
INSERT INTO users ( username, password,role_id,enabled) VALUES ('geek', '$2a$10$iYeQUOBt4iRNBTDYD2/oLuAoxEKd.FjkTmewok9kU.kALhqM2fV16','ROLE_TECHNICIAN',true);
--INSERT INTO users ( username, password,role_id,enabled) VALUES ('visitor', '$2a$10$iYeQUOBt4iRNBTDYD2/oLuAoxEKd.FjkTmewok9kU.kALhqM2fV16','ROLE_VISITOR',true);


INSERT INTO machine( machine_id, state) VALUES ('CYCLONE', 'CYCLONE_START');
INSERT INTO machine( machine_id, state, server_url) VALUES ('DOSECALIBRATOR', 'CYCLONE_START','');
INSERT INTO machine( machine_id, state, server_url) VALUES ('DISPENSING', 'CYCLONE_START','');
INSERT INTO machine( machine_id, state, server_url) VALUES ('QC', 'CYCLONE_START','');
