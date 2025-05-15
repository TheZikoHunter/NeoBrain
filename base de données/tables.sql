CREATE TABLE produit(
    id_global_produit VARCHAR2(100),
    id_produit NUMBER,
    nom_produit VARCHAR2(30) NOT NULL,
    description VARCHAR2(100),
    prix NUMBER NOT NULL,
    quantite_stock NUMBER DEFAULT 0,
    date_ajout DATE DEFAULT SYSDATE,
    disponible NUMBER DEFAULT 0,
    dernier_inventaire DATE,
    besoin_inventaire NUMBER,
    id_categorie NUMBER NOT NULL,
    id_entreprise NUMBER NOT NULL
);
CREATE TABLE categorie(
    id_categorie NUMBER UNIQUE,
    nom_categorie VARCHAR2(20) NOT NULL
);


CREATE TABLE classe (
    id_classe NUMBER PRIMARY KEY,
    classe VARCHAR2(50)
);

CREATE TABLE client(
    id_personne NUMBER,
    id_client VARCHAR2(10) UNIQUE,
    classe VARCHAR2(2) DEFAULT 'C'
);

CREATE TABLE commande_achat (
    id_commande_achat NUMBER,
    date DATE DEFAULT SYSDATE,
    prixtotal NUMBER,
    id_responsable_achat NUMBER,
    id_fournisseur NUMBER,
    id_transaction NUMBER
);

CREATE TABLE commande_vente(
    id_commande_vente NUMBER UNIQUE,
    date_vente DATE DEFAULT SYSDATE,
    prix_total NUMBER DEFAULT 0,
    etat_echec NUMBER DEFAULT 1,
    id_client VARCHAR2(10),
    id_responsable_ventes NUMBER,
    id_transaction NUMBER
);

CREATE TABLE Commercant (
    id_personnel NUMBER PRIMARY KEY,
    id_commercant NUMBER UNIQUE,
    id_responsable_crm NUMBER
);

CREATE TABLE entreprise (
    id_entreprise NUMBER UNIQUE,
    nom_entreprise VARCHAR2(20)
);

CREATE TABLE fournisseur (
    id_personne NUMBER PRIMARY KEY,
    id_fournisseur NUMBER,
    adresse_fournisseur VARCHAR(100),
    classe NUMBER DEFAULT 'C'
);

CREATE TABLE ligne_commande_achat(
    id_commande_achat NUMBER,
    id_ligne_achat NUMBER,
    qte_achat NUMBER,
    id_produit VARCHAR2(100)
);

CREATE TABLE ligne_commande_vente(
    id_ligne_commande_vente NUMBER UNIQUE,
    id_commande_vente NUMBER,
    qte_vente NUMBER DEFAULT 0,
    etat_retour NUMBER DEFAULT 0,
    etat_echec NUMBER DEFAULT 1,
    id_produit VARCHAR2(100)
);

CREATE TABLE Personne (
    id_personne NUMBER PRIMARY KEY,
    nom_personne VARCHAR2(100),
    prenom_personne VARCHAR2(100),
    date_naissance DATE,
    email VARCHAR2(100),
    telephone VARCHAR2(20),
    civilite VARCHAR2(10),
    nationalite VARCHAR2(50),
    nom_utilisateur VARCHAR2(100),
    mot_de_passe VARCHAR2(100),
    type_utilisateur VARCHAR2(10)
);

CREATE TABLE personnel (
    id_personne NUMBER PRIMARY KEY,
    id_personnel NUMBER,
    role VARCHAR2(50),
    salaire NUMBER,
    date_embauche DATE DEFAULT SYSDATE,
    mot_de_passe VARCHAR2(100)
);

CREATE TABLE Prospect (
    id_prospect NUMBER UNIQUE,
    information VARCHAR2(500),
    status_prospect VARCHAR2(100),
    id_commercant NUMBER
);

CREATE TABLE Responsable_CRM (
    id_personnel NUMBER PRIMARY KEY,
    id_responsable_crm NUMBER UNIQUE
);

CREATE TABLE reponsable_achat (
    id_personnel NUMBER,
    id_responsable_achat NUMBER
);

CREATE TABLE Ressources_Humaines (
    id_personnel NUMBER PRIMARY KEY,
    id_rh NUMBER UNIQUE
);

CREATE TABLE transaction(
    id_transaction NUMBER UNIQUE,
    montant_transaction NUMBER DEFAULT 0,
    type_transaction VARCHAR2(10),
    id_commande_vente NUMBER NOT NULL
);

CREATE TABLE reclamation (
    id_reclamation NUMBER,
    type_reclamation VARCHAR2(10)
    etat VARCHAR2(10),
    date_reclamation DATE DEFAULT SYSDATE
);

CREATE TABLE echec_de_reception (
    id_echec_reception NUMBER,
    id_reclamation NUMBER,
    id_reclamation NUMBER
);

CREATE TABLE retour (
    id_retour NUMBER,
    id_reclamation NUMBER,
    id_ligne_vente NUMBER
);

CREATE TABLE invetaire (
    id_inventaire NUMBER,
    date_debut DATE DEFAULT SYSDATE,
    date_fin DATE,
    etat VARCHAR2(10),
    est_clos VARCHAR2(1),
    id_responsable_stock NUMBER
);

CREATE TABLE tache_inventaire (
    id_tache_inventaire NUMBER,
    date_tache DATE,
    etat VARCHAR2(10),
    qte_physique NUMBER,
    id_employe_stock NUMBER,
    id_inventaire NUMBER,
    id_produit VARCHAR2(100)
);

CREATE TABLE responsable_stock (
    id_personnel NUMBER,
    id_responsable_stock NUMBER
);

CREATE TABLE employe_stock (
    id_personnel NUMBER,
    id_employe NUMBER
);

CREATE TABLE condidat (
    id_personne NUMBER,
    id_condidat NUMBER,
    date_condidature DATE,
    poste_souhaite VARCHAR2(100),
    etat_condidature VARCHAR2(10),
    id_ville NUMBER,
    id_rh NUMBER
);

CREATE TABLE ville (
    id_ville NUMBER,
    nom_ville VARCHAR(50),
    id_pays NUMBER
);

CREATE TABLE pays (
    id_pays NUMBER,
    nom_pays VARCHAR(50)
);

CREATE TABLE responsable_SAV (
    id_responsable_SAV NUMBER,
    id_personnel NUMBER
);

CREATE TABLE image(
    id_image NUMBER,
    uri_image VARCHAR2(200)
);

CREATE TABLE role(
    titre VARCHAR2(50);
);