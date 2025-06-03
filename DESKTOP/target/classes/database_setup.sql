-- Database creation script for NeoBrain Inventory Management System
-- Run this script as PostgreSQL superuser

-- Create database
CREATE DATABASE neobrain_inventory
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Connect to the database
\c neobrain_inventory;

-- Create custom ENUM types
CREATE TYPE civilite_enum AS ENUM ('M', 'MLLE', 'MME');
CREATE TYPE nationalite_enum AS ENUM ('MAROCCAINE', 'FRANCAISE', 'AMERICAINE');
CREATE TYPE role_enum AS ENUM ('RESPONSABLE_STOCK', 'EMPLOYE_STOCK', 'ADMIN');
CREATE TYPE mode_paiement_enum AS ENUM ('CHEQUE', 'ESPECE', 'CARTE');
CREATE TYPE categorie_produit_enum AS ENUM ('INFORMATIQUE', 'ELECTROMENAGER', 'VETEMENTS', 'SPORTS');
CREATE TYPE etat_tache_enum AS ENUM ('EN_ATTENTE', 'EN_COURS', 'TERMINEE', 'ANNULEE');
CREATE TYPE etat_reclamation_enum AS ENUM ('EN_ATTENTE', 'VALIDEE', 'REFUSEE');
CREATE TYPE type_reclamation_enum AS ENUM ('RETOUR', 'ECHEC_RECEPTION');

-- Enable UUID extension for better ID generation (optional)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant privileges (adjust as needed for your setup)
GRANT ALL PRIVILEGES ON DATABASE neobrain_inventory TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
