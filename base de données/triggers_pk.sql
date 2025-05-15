CREATE OR REPLACE TRIGGER trg_produit_id BEFORE INSERT ON produit FOR EACH ROW WHEN (NEW.id_produit IS NULL)
BEGIN
    SELECT seq_produit.NEXTVAL INTO :NEW.id_produit FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_pays_id BEFORE INSERT ON pays FOR EACH ROW WHEN (NEW.id_pays IS NULL)
BEGIN
    SELECT seq_pays.NEXTVAL INTO :NEW.id_pays FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_ville_id BEFORE INSERT ON ville FOR EACH ROW WHEN (NEW.id_ville IS NULL)
BEGIN
    SELECT seq_ville.NEXTVAL INTO :NEW.id_ville FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_condidat_id BEFORE INSERT ON condidat FOR EACH ROW WHEN (NEW.id_condidat IS NULL)
BEGIN
    SELECT seq_condidat.NEXTVAL INTO :NEW.id_condidat FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_personnel_id BEFORE INSERT ON personnel FOR EACH ROW WHEN (NEW.id_personnel IS NULL)
BEGIN
    SELECT seq_personnel.NEXTVAL INTO :NEW.id_personnel FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_personne_id BEFORE INSERT ON personne FOR EACH ROW WHEN (NEW.id_personne IS NULL)
BEGIN
    SELECT seq_personne.NEXTVAL INTO :NEW.id_personne FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_responsable_CRM_id BEFORE INSERT ON responsable_CRM FOR EACH ROW WHEN (NEW.id_responsable_CRM IS NULL)
BEGIN
    SELECT seq_responsable_CRM.NEXTVAL INTO :NEW.id_responsable_CRM FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_responsable_achat_id BEFORE INSERT ON responsable_achat FOR EACH ROW WHEN (NEW.id_responsable_achat IS NULL)
BEGIN
    SELECT seq_responsable_achat.NEXTVAL INTO :NEW.id_responsable_achat FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_responsable_vente_id BEFORE INSERT ON responsable_vente FOR EACH ROW WHEN (NEW.id_responsable_vente IS NULL)
BEGIN
    SELECT seq_responsable_vente.NEXTVAL INTO :NEW.id_responsable_vente FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_responsable_stock_id BEFORE INSERT ON responsable_stock FOR EACH ROW WHEN (NEW.id_responsable_stock IS NULL)
BEGIN
    SELECT seq_responsable_stock.NEXTVAL INTO :NEW.id_responsable_stock FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_employe_stock_id BEFORE INSERT ON employe_stock FOR EACH ROW WHEN (NEW.id_employe_stock IS NULL)
BEGIN
    SELECT seq_employe_stock.NEXTVAL INTO :NEW.id_employe_stock FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_client_id BEFORE INSERT ON client FOR EACH ROW WHEN (NEW.id_client IS NULL)
BEGIN
    SELECT seq_client.NEXTVAL INTO :NEW.id_client FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_fournisseur_id BEFORE INSERT ON fournisseur FOR EACH ROW WHEN (NEW.id_fournisseur IS NULL)
BEGIN
    SELECT seq_fournisseur.NEXTVAL INTO :NEW.id_fournisseur FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_categorie_id BEFORE INSERT ON categorie FOR EACH ROW WHEN (NEW.id_categorie IS NULL)
BEGIN
    SELECT seq_categorie.NEXTVAL INTO :NEW.id_categorie FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_inventaire_id BEFORE INSERT ON inventaire FOR EACH ROW WHEN (NEW.id_inventaire IS NULL)
BEGIN
    SELECT seq_inventaire.NEXTVAL INTO :NEW.id_inventaire FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_tache_inventaire_id BEFORE INSERT ON tache_inventaire FOR EACH ROW WHEN (NEW.id_tache_inventaire IS NULL)
BEGIN
    SELECT seq_tache_inventaire.NEXTVAL INTO :NEW.id_tache_inventaire FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_prospect_id BEFORE INSERT ON prospect FOR EACH ROW WHEN (NEW.id_prospect IS NULL)
BEGIN
    SELECT seq_prospect.NEXTVAL INTO :NEW.id_prospect FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_Ressources_Humaines_id BEFORE INSERT ON Ressources_Humaines FOR EACH ROW WHEN (NEW.id_Ressources_Humaines IS NULL)
BEGIN
    SELECT seq_Ressources_Humaines.NEXTVAL INTO :NEW.id_Ressources_Humaines FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_commercant_id BEFORE INSERT ON commercant FOR EACH ROW WHEN (NEW.id_commercant IS NULL)
BEGIN
    SELECT seq_commercant.NEXTVAL INTO :NEW.id_commercant FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_transaction_id BEFORE INSERT ON transaction FOR EACH ROW WHEN (NEW.id_transaction IS NULL)
BEGIN
    SELECT seq_transaction.NEXTVAL INTO :NEW.id_transaction FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_classe_id BEFORE INSERT ON classe FOR EACH ROW WHEN (NEW.id_classe IS NULL)
BEGIN
    SELECT seq_classe.NEXTVAL INTO :NEW.id_classe FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_retour_id BEFORE INSERT ON retour FOR EACH ROW WHEN (NEW.id_retour IS NULL)
BEGIN
    SELECT seq_retour.NEXTVAL INTO :NEW.id_retour FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_reclamation_id BEFORE INSERT ON reclamation FOR EACH ROW WHEN (NEW.id_reclamation IS NULL)
BEGIN
    SELECT seq_reclamation.NEXTVAL INTO :NEW.id_reclamation FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_echec_de_reception_id BEFORE INSERT ON echec_de_reception FOR EACH ROW WHEN (NEW.id_echec_de_reception IS NULL)
BEGIN
    SELECT seq_echec_de_reception.NEXTVAL INTO :NEW.id_echec_de_reception FROM dual;
END;


CREATE OR REPLACE TRIGGER trg_commande_achat_id BEFORE INSERT ON commande_achat FOR EACH ROW WHEN (NEW.id_commande_achat IS NULL)
BEGIN
    SELECT seq_commande_achat.NEXTVAL INTO :NEW.id_commande_achat FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_commande_vente_id BEFORE INSERT ON commande_vente FOR EACH ROW WHEN (NEW.id_ligne_commande_vente IS NULL)
BEGIN
    SELECT seq_commande_vente.NEXTVAL INTO :NEW.id_commande_vente FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_ligne_commande_vente_id BEFORE INSERT ON ligne_commande_vente FOR EACH ROW WHEN (NEW.id_ligne_commande_vente IS NULL)
BEGIN
    SELECT seq_ligne_commande_vente.NEXTVAL INTO :NEW.id_ligne_commande_vente FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_ligne_commande_achat_id BEFORE INSERT ON ligne_commande_achat FOR EACH ROW WHEN (NEW.id_ligne_commande_achat IS NULL)
BEGIN
    SELECT seq_ligne_commande_achat.NEXTVAL INTO :NEW.id_ligne_commande_achat FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_mouvement_stock_id BEFORE INSERT ON mouvement_stock FOR EACH ROW WHEN (NEW.id_mouvement_stock IS NULL)
BEGIN
    SELECT seq_mouvement_stock.NEXTVAL INTO :NEW.id_mouvement_stock FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_responsable_sav_id BEFORE INSERT ON responsable_sav FOR EACH ROW WHEN (NEW.id_responsable_sav IS NULL)
BEGIN
    SELECT seq_responsable_sav.NEXTVAL INTO :NEW.id_responsable_sav FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_image_id BEFORE INSERT ON image FOR EACH ROW WHEN (NEW.id_image IS NULL)
BEGIN
    SELECT seq_image.NEXTVAL INTO :NEW.id_image FROM dual;
END;