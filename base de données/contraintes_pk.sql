ALTER TABLE produit ADD CONSTRAINT pk_produit PRIMARY KEY (id_global_produit);

ALTER TABLE pays ADD CONSTRAINT pk_pays PRIMARY KEY (id_pays);
ALTER TABLE ville ADD CONSTRAINT pk_ville PRIMARY KEY (id_ville);


ALTER TABLE condidat ADD CONSTRAINT pk_condidat PRIMARY KEY (id_personne);
ALTER TABLE personnel ADD CONSTRAINT pk_personnel PRIMARY KEY (id_personne);
ALTER TABLE personne ADD CONSTRAINT pk_personne PRIMARY KEY (id_personne);


ALTER TABLE responsable_achat ADD CONSTRAINT pk_responsable_achat PRIMARY KEY (id_personnel);
ALTER TABLE resposnable_vente ADD CONSTRAINT pk_resposnable_vente PRIMARY KEY (id_personnel);
ALTER TABLE responsable_CRM ADD CONSTRAINT pk_responsable_CRM PRIMARY KEY (id_personnel);
ALTER TABLE Ressources_Humaines ADD CONSTRAINT pk_rh PRIMARY KEY (id_personnel);
ALTER TABLE commercant ADD CONSTRAINT pk_commercant PRIMARY KEY (id_personnel);
ALTER TABLE employe_stock ADD CONSTRAINT pk_employe_stock PRIMARY KEY (id_personnel);
ALTER TABLE responsable_stock ADD CONSTRAINT pk_responsable_stock PRIMARY KEY (id_personnel);

ALTER TABLE prospect ADD CONSTRAINT pk_prospect PRIMARY KEY (id_prospect);
ALTER TABLE entreprise ADD CONSTRAINT pk_entreprise PRIMARY KEY (id_entreprise);


ALTER TABLE client ADD CONSTRAINT pk_client PRIMARY KEY (id_personne);
ALTER TABLE fournisseur ADD CONSTRAINT pk_fournisseur PRIMARY KEY (id_personne);

ALTER TABLE commande_vente ADD CONSTRAINT pk_commande_vente PRIMARY KEY (id_mouvement_stock);
ALTER TABLE commande_achat ADD CONSTRAINT pk_commande_achat PRIMARY KEY (id_mouvement_stock);
ALTER TABLE ligne_achat ADD CONSTRAINT pk_ligne_achat PRIMARY KEY (id_ligne_achat, id_commande_achat);
ALTER TABLE ligne_vente ADD CONSTRAINT pk_ligne_vente PRIMARY KEY (id_ligne_vente, id_commande_vente);


ALTER TABLE inventaire ADD CONSTRAINT pk_inventaire PRIMARY KEY (id_inventaire);
ALTER TABLE tache_inventaire ADD CONSTRAINT pk_tache_inventaire PRIMARY KEY (id_tache_inventaire);


ALTER TABLE reclamation ADD CONSTRAINT pk_reclamation PRIMARY KEY (id_reclamation);
ALTER TABLE retour ADD CONSTRAINT pk_retour PRIMARY KEY (id_reclamation);
ALTER TABLE echec_reception ADD CONSTRAINT pk_echec_reception PRIMARY KEY (id_reclamation);


ALTER TABLE transaction ADD CONSTRAINT pk_transaction PRIMARY KEY (id_transaction);
ALTER TABLE classe ADD CONSTRAINT pk_classe PRIMARY KEY (id_classe);
ALTER TABLE entreprise ADD CONSTRAINT pk_entreprise PRIMARY KEY (id_entreprise);

ALTER TABLE image ADD CONSTRAINT pk_image PRIMARY KEY (id_image);

ALTER TABLE utilisateur ADD CONSTRAINT pk_utilisateur PRIMARY KEY (id_utilisateur);