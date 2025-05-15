--Produit
ALTER TABLE produit ADD CONSTRAINT fk_produit_categorie FOREIGN KEY (id_categorie)
REFERENCES categorie(id_categorie);

ALTER TABLE produit ADD CONSTRAINT fk_produit_entreprise FOREIGN KEY (id_entreprise)
REFERENCES rentreprise(id_entreprise);

--Ligne_commande_vente

ALTER TABLE ligne_commande_vente ADD CONSTRAINT fk_ligne_vente_commande_vente FOREIGN KEY (id_commande_vente)
REFERENCES commande_vente(id_commande_vente) DELETE ON CASCADE;

ALTER TABLE ligne_commande_vente ADD CONSTRAINT fk_ligne_vente_produit FOREIGN KEY (id_produit)
REFERENCES produit(id_global_produit);

--Ligne_commande_achat

ALTER TABLE ligne_commande_achat ADD CONSTRAINT fk_ligne_achat_commande_achat FOREIGN KEY (id_commande_achat)
REFERENCES commande_achat(id_commande_achat) DELETE ON CASCADE;

ALTER TABLE ligne_commande_achat ADD CONSTRAINT fk_ligne_achat_produit FOREIGN KEY (id_produit)
REFERENCES produit(id_global_produit);

--client

ALTER TABLE client ADD CONSTRAINT fk_client_personne FOREIGN KEY (id_personne)
REFERENCES personne(id_personne) DELETE ON CASCADE;

ALTER TABLE client ADD CONSTRAINT fk_client_classe FOREIGN KEY (id_classe)
REFERENCES classe(id_classe);

ALTER TABLE client ADD CONSTRAINT fk_client_utilisateur FOREIGN KEY (id_utilisateur)
REFERENCES utilisateur(id_utilisateur);

--Commande_vente

ALTER TABLE commande_vente ADD CONSTRAINT fk_commande_vente_mouvement_stock FOREIGN KEY (id_mouvement_stock)
REFERENCES mouvement_stock(id_mouvement_stock) DELETE ON CASCADE;

ALTER TABLE commande_vente ADD CONSTRAINT fk_commande_vente_responsable_vente FOREIGN KEY (id_responsable_vente)
REFERENCES responsable_vente(id_responsable_vente);

ALTER TABLE commande_vente ADD CONSTRAINT fk_commande_vente_client FOREIGN KEY (id_client)
REFERENCES client(id_client);

--Commande_achat

ALTER TABLE commande_achat ADD CONSTRAINT fk_commande_achat_mouvement_stock FOREIGN KEY (id_mouvement_stock)
REFERENCES mouvement_stock(id_mouvement_stock) DELETE ON CASCADE;

ALTER TABLE commande_achat ADD CONSTRAINT fk_commande_achat_responsable_achat FOREIGN KEY (id_responsable_achat)
REFERENCES responsable_achat(id_responsable_achat);

ALTER TABLE commande_achat ADD CONSTRAINT fk_commande_achat_fournisseur FOREIGN KEY (id_fournisseur)
REFERENCES fournisseur(id_fournisseur);

--Responsable_vente

ALTER TABLE responsable_vente ADD CONSTRAINT fk_responsaable_vente_personnel FOREIGN KEY (id_personnel)
REFERENCES personnel(id_personnel) DELETE ON CASCADE;

--Responsable_achat

ALTER TABLE responsable_achat ADD CONSTRAINT fk_responsaable_achat_personnel FOREIGN KEY (id_personnel)
REFERENCES personnel(id_personnel) DELETE ON CASCADE;

--Personnel

ALTER TABLE personnel ADD CONSTRAINT fk_personnel_personne FOREIGN KEY (id_personne)
REFERENCES personne(id_personne) DELETE ON CASCADE;

ALTER TABLE personnel ADD CONSTRAINT fk_personnel_utilisateur FOREIGN KEY (id_utilisateur)
REFERENCES utilisateur(id_utilisateur);

--Condidat

ALTER TABLE condidat ADD CONSTRAINT fk_condidat_personne FOREIGN KEY (id_personne)
REFERENCES personne(id_personne) DELETE ON CASCADE;

ALTER TABLE condidat ADD CONSTRAINT fk_condidat_ville FOREIGN KEY (id_ville)
REFERENCES ville(id_ville);

ALTER TABLE condidat ADD CONSTRAINT fk_condidat_Ressources_Humaines FOREIGN KEY (id_Ressources_Humaines)
REFERENCES Ressources_Humaines(id_Ressources_Humaines);


--Employe_stock

ALTER TABLE employe_stock ADD CONSTRAINT fk_employe_stock_personnel FOREIGN KEY (id_personnel)
REFERENCES personnel(id_personnel) DELETE ON CASCADE;

--Responsable_stock

ALTER TABLE responsable_stock ADD CONSTRAINT fk_responsable_stock_personnel FOREIGN KEY (id_personnel)
REFERENCES personnel(id_personnel) DELETE ON CASCADE;

--Commercant

ALTER TABLE commercant ADD CONSTRAINT fk_commercant_personnel FOREIGN KEY (id_personnel)
REFERENCES personnel(id_personnel) DELETE ON CASCADE;

ALTER TABLE commercant ADD CONSTRAINT fk_commercant_responsable_CRM FOREIGN KEY (id_responsable_CRM)
REFERENCES responsable_CRM(id_responsable_CRM);

--Responsable_crm

ALTER TABLE responsable_crm ADD CONSTRAINT fk_responsable_crm_personnel FOREIGN KEY (id_personnel)
REFERENCES personnel(id_personnel) DELETE ON CASCADE;

--Prospect

ALTER TABLE prospect ADD CONSTRAINT fk_prospect_personne FOREIGN KEY (id_personne)
REFERENCES personne(id_personne) DELETE ON CASCADE;

--Transaction

ALTER TABLE transaction ADD CONSTRAINT fk_transaction_mouvement_stock FOREIGN KEY (id_mouvement_stock)
REFERENCES mouvement_stock(id_mouvement_stock);

ALTER TABLE transaction ADD CONSTRAINT fk_transaction_ligne_commande_vente FOREIGN KEY (id_ligne_commande_vente)
REFERENCES ligne_commande_vente(id_ligne_commande_vente);

--Fournisseur
ALTER TABLE Fournisseur ADD CONSTRAINT fk_Fournisseur_personne FOREIGN KEY (id_personne)
REFERENCES personne(id_personne);
ALTER TABLE Fournisseur ADD CONSTRAINT fk_Fournisseur_classe FOREIGN KEY (id_classe)
REFERENCES classe(id_classe);
--Inventaire
ALTER TABLE inventaire ADD CONSTRAINT fk_inventaire_responsable_stock FOREIGN KEY (id_responsable_stock)
REFERENCES responsable_stock(id_responsable_stock);
--Tache_inventaire
ALTER TABLE tache_inventaire ADD CONSTRAINT fk_tache_inventaire_employe_stock FOREIGN KEY (id_employe_stock)
REFERENCES employe_stock(id_employe_stock);
ALTER TABLE tache_inventaire ADD CONSTRAINT fk_tache_inventaire_inventaire FOREIGN KEY (id_inventaire)
REFERENCES inventaire(id_inventaire);
ALTER TABLE tache_inventaire ADD CONSTRAINT fk_tache_inventaire_produit FOREIGN KEY (id_produit)
REFERENCES produit(id_produit);
--Ressources_humaines
ALTER TABLE Ressources_humaines ADD CONSTRAINT fk_Ressources_humaines_personnel FOREIGN KEY (id_personnel)
REFERENCES personnel(id_personnel) DELETE ON CASCADE;

--Ville
ALTER TABLE ville ADD CONSTRAINT fk_ville_pays FOREIGN KEY (id_pays)
REFERENCES pays(id_pays);

--Retour

ALTER TABLE retour ADD CONSTRAINT fk_retour_reclamation FOREIGN KEY (id_reclamation)
REFERENCES transaction(id_reclamation);

--Echec_de_reception

ALTER TABLE Echec_de_reception ADD CONSTRAINT fk_Echec_de_reception_reclamation FOREIGN KEY (id_reclamation)
REFERENCES transaction(id_reclamation);

--Responsable_SAV
ALTER TABLE responsable_sav ADD CONSTRAINT fk_responsable_SAV_personnel FOREIGN KEY (id_personnel)
REFERENCES personnel (id_personnel);

--Image
ALTER TABLE image ADD CONSTRAINT fk_image_produit FOREIGN KEY (id_produit)
REFERENCES produit (id_global_produit);

--Reclamation
ALTER TABLE reclamation ADD CONSTRAINT fk_reclamation_transaction FOREIGN KEY (id_transaction)
REFERENCES transaction(id_transaction) DELETE ON CASCADE;