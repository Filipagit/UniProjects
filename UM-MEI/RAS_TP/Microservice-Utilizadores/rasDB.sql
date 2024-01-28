-- Create Schema
CREATE SCHEMA ras;

-- Use the ras Schema
USE ras;

-- Create User Table
CREATE TABLE user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Insert data into the "user" table without specifying user_id
INSERT INTO user (username, password, email, role)
VALUES
    ('joao', '$2b$12$jYUw.mytuxpGAzzkeRJb1.AAN8bvfBfr0e2lS89QpKNzZInuvav3e', 'joao@uminho.pt', 'estudante'),
    ('admin', '$2b$12$E79aa7uM.AieEtc6L7ejHuvuX6XFapz37NbHxYU56dG8N8B6VZcQy', 'admin@uminho.pt', 'admin'),
    ('alberto', '$2b$12$eIoQxJWJCoTIt4suDU8pIesjxdhTwqRR/l3mJY1QXsKPgXr55yVDS', 'alberto@uminho.pt', 'docente'),
    ('simao', '$2b$12$CuaBl1mgEIWM8v2.WlPAH.n1qvPeNhOKKIb.VoJgTiK1W4s6JqU/6', 'simao@uminho.pt', 'docente'),
    ('carlos', '$2b$12$W6rEdptpxdaWltPQVVLo4.J/CqvaoAAhmtOuhSuvAZ5NaVWXnuhYW', 'carlos@uminho.pt', 'docente'),
    ('maria', '$2b$12$vPUS1IlcjEsqHCFP51XhguO9AYx9WwAZE94qucMOXuT9F9gbiPQIq', 'maria@uminho.pt', 'estudante'),
    ('gustavo', '$2b$12$TU3yFR2AZVTEr2/c0yDzHOLIDgPLqKm1blSR8CbTA3FlRy07L6QC2', 'gustavo@uminho.pt', 'estudante'),
    ('admin1', '$2b$12$VqsQ6C7dWPEVupUyx/qk1.8cxTFGiRl7s9ifoo/vXASxax5CwyLlu', 'admin1@uminho.pt', 'admin'),
    ('paulo', '$2b$12$lta9IazSe/EHAp5HA/KpkOWX8WALt3Fo6mYsSTk02upwkCEk4NTp2', 'paulo@uminho.pt', 'docente'),
    ('paula', '$2b$12$btSZ04qZlsYgdDgWag8sGOO6fp0drpW.83oeO3eAgQ3zqPaj3Kzne', 'paula@uminho.pt', 'docente');