ALTER TABLE produit ADD CONSTRAINT crnt_produit_disponible
CHECK (disponible IN ('0', '1'));

ALTER TABLE ligne_commande_vente ADD CONSTRAINT crnt_ligne_commande_vente_etat_retour 
CHECK (etat_retour IN ('0', '1'));
ALTER TABLE ligne_commande_vente ADD CONSTRAINT crnt_ligne_commande_vente_etat_echec 
CHECK (etat_echec IN ('0', '1'));

ALTER TABLE commande_vente ADD CONSTRAINT crnt_commande_vente_etat_echec 
CHECK (etat_echec IN ('0', '1'));

ALTER TABLE reclamation ADD CONSTRAINT crnt_reclamation_etat 
CHECK (etat IN ('en attente', 'valide'));

ALTER TABLE reclamation ADD CONSTRAINT crnt_reclamation_type_reclamation 
CHECK (type_reclamation  IN ('retour', 'echec'));

ALTER TABLE condidat ADD CONSTRAINT crnt_condidat_etat_condidature 
CHECK (etat_condidature IN ('en attente', 'valide', 'embauche'));

ALTER TABLE inventaire ADD CONSTRAINT crnt_inventaire_etat
CHECK (etat IN ('en attente', 'valide', 'termine', 'annule', 'en retard'));

ALTER TABLE inventaire ADD CONSTRAINT crnt_inventaire_clos
CHECK (est_clos IN ('0', '1'));

ALTER TABLE tache_inventaire ADD CONSTRAINT crnt_tache_inventaire_etat
CHECK (etat IN ('en attente', 'fait', 'retard', 'annulee'));

ALTER TABLE personne ADD CONSTRAINT crnt_personne_type_utilisateur
CHECK (type IN ('condidat', 'personnel'));

ALTER TABLE personne ADD CONSTRAINT crnt_personne_email 
CHECK (
    REGEXP_LIKE(email, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

ALTER TABLE prospect ADD CONSTRAINT crnt_prospect_status
CHECK (status_prospect IN ('propose', 'valide'));

ALTER TABLE transaction ADD CONSTRAINT crnt_transaction_type_transaction
CHECK (type_transaction IN ('Commande de vente', 'Commande d achat', 'retour', 'echec de reception'));

ALTER TABLE personnel ADD CONSTRAINT crnt_personnel_role
CHECK (role IN ('Responsable achat', 'Responsable vente', 'Responsable stock', 
                'Employe stock', 'Responsable SAV', 'Ressources Humaines', 
                'Responsable CRM', 'Commercant'));
