insert into roles (id, name) values (1, 'ROLE_ADMIN');
insert into roles (id, name) values (2, 'ROLE_DOCTOR');

insert into privileges (id, name) values (1, 'READ_PRIVILEGE');
insert into privileges (id, name) values (2, 'CREATE_PRIVILEGE');
insert into privileges (id, name) values (3, 'UPDATE_PRIVILEGE');
insert into privileges (id, name) values (4, 'DELETE_PRIVILEGE');
insert into privileges (id, name) values (5, 'READ_PATIENT_PRIVILEGE');
insert into privileges (id, name) values (6, 'CREATE_PATIENT_PRIVILEGE');
insert into privileges (id, name) values (7, 'UPDATE_PATIENT_PRIVILEGE');


insert into roles_privileges (role_id, privilege_id) values (1, 1);
insert into roles_privileges (role_id, privilege_id) values (1, 2);
insert into roles_privileges (role_id, privilege_id) values (1, 3);
insert into roles_privileges (role_id, privilege_id) values (1, 4);
insert into roles_privileges (role_id, privilege_id) values (2, 5);
insert into roles_privileges (role_id, privilege_id) values (2, 6);
insert into roles_privileges (role_id, privilege_id) values (2, 7);

-- password == name
insert into users (name, surname, username, password, enabled, role, last_password_reset_date, last_login_date) values ('admin1', 'admin1', 'admin1@gmail.com', '$2y$12$LdA5w1xM5qBwt1l.Srv62etWXUpyfCbU/usi3EprOEdZ3ZbCiDx/K', true, 'ADMIN', '2020-12-07 16:00:00.508-07', '2021-06-06 16:00:00.508-07');
insert into users (name, surname, username, password, enabled, role, last_password_reset_date, last_login_date) values ('admin2', 'admin2', 'admin2@gmail.com', '$2y$12$fcgxX0RiMeL1jbEI/wKt/uD3b1nCrCY22O6Y5ecR28btz4miOnI5q', true, 'ADMIN', '2020-12-07 16:00:00.508-07', '2021-06-06 16:00:00.508-07');
insert into users (name, surname, username, password, enabled, role, last_password_reset_date) values ('doctor1', 'doctor1', 'doctor1@gmail.com', '$2y$12$LdA5w1xM5qBwt1l.Srv62etWXUpyfCbU/usi3EprOEdZ3ZbCiDx/K', true, 'DOCTOR', '2020-12-07 16:00:00.508-07');

insert into user_roles (user_id, role_id) values (1, 1);
insert into user_roles (user_id, role_id) values (2, 1);
insert into user_roles (user_id, role_id) values (3, 2);


insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Natasa', 'Natasic', '2107991815031', 'Female', '1991-07-21', '171', '56', 'A', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Mario', 'Mariovic', '2208991817033', 'Male', '1983-07-21', '196', '89', 'B', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Stefan', 'Stefic', '2309991816834', 'Male', '1928-07-21', '175', '70', 'AB', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Van', 'Gog', '2410991816934', 'Male', '1853-03-30', '182', '88', 'O', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Pacijento', 'Pacijentkic', '2506991816138', 'Male', '1901-07-25', '175', '91', 'AB', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Karen', 'Karenovic', '2605996816034', 'Female', '1981-08-21', '165', '54', 'AB', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Mikelandjelo', 'Mikelandjelic', '27991816635', 'Male', '1051-07-21', '189', '65', 'A', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Rafaelo', 'Rafaelic', '2807981816834', 'Male', '1991-08-21', '190', '90', 'B', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Dusko', 'Dugousko', '2907981816032', 'Male', '1993-06-11', '179', '56', 'B', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Mini', 'Maus', '3007991816434', 'Female', '1998-05-31', '162', '59', 'B', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');
insert into patients (name, surname, jmbg, gender, date_of_birth, height, weight, blood_type, blood_pressure, illness, vaccination) values ('Vuk', 'Karadzic', '3107981816038', 'Male', '1994-07-12', '171', '60', 'A', '140/90', 'Ilness 1, Ilness 2', 'Vaccine1, Vaccine2');

