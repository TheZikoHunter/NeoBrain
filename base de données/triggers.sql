-- ajout de ligne de vente -> réduction de stock + transaction positive
CREATE OR REPLACE TRIGGER trg_commande_vente_transaction_stock
BEFORE UPDATE
ON commande_vente
FOR EACH ROW
BEGIN
    IF :NEW.etat_echec = '0' AND :OLD.etat_echec = '1' THEN 
        INSERT INTO transaction (montant_transaction, type_transaction)
        VALUES (:NEW.prix_total, 'Commande de vente')
        WHERE id_transaction=:NEW.id_transaction;

        UPDATE ligne_commande_vente SET etat_echec='0'
        WHERE id_commande_vente=:NEW.id_commande_vente;
    END IF;
END;
/
CREATE OR REPLACE TRIGGER trg_ligne_commande_vente_transaction_stock
BEFORE UPDATE
ON ligne_commande_vente
FOR EACH ROW
BEGIN
    IF :NEW.etat_echec = '0' AND :OLD.etat_echec = '1' THEN 
        UPDATE produit SET quantite_stock=quantite_stock + :NEW.qte_vente
        WHERE id_global_produit=:NEW.id_produit;
    END IF;
END;
/
-- etats echec et retour
CREATE OR REPLACE TRIGGER trg_reclamation_etat_ligne_commande_vente
BEFORE INSERT
ON reclamation
FOR EACH ROW
DECLARE
    v_montant_remboursement NUMBER;
    v_id_ligne_commande_vente NUMBER;
    v_qte_concernee NUMBER;
    v_id_produit VARCHAR2(100);
BEGIN

    SELECT id_ligne_commande_vente INTO v_id_ligne_commande_vente FROM ligne_commande_vente
    WHERE id_ligne_commande_vente=:NEW.id_ligne_commande_vente;

    SELECT qte_vente INTO v_qte_concernee FROM ligne_commande_vente
    WHERE id_ligne_commande_vente=:NEW.id_ligne_commande_vente;

    SELECT id_produit INTO v_id_produit FROM ligne_commande_vente
    WHERE id_ligne_commande_vente=:NEW.id_ligne_commande_vente;

    SELECT prix INTO v_montant_remboursement FROM produit
    WHERE id_global_produit=v_id_produit;

    IF :NEW.type_reclamation = 'retour' AND :NEW.etat='valide' THEN
        UPDATE ligne_commande_vente SET etat_retour='1'
        WHERE id_commande_vente=:NEW.id_commande_vente;

        INSERT INTO transaction(montant_transaction, type_transaction)
        VALUES (v_montant_remboursement*v_qte_concernee, 'retour');

        UPDATE produit SET quantite_stock=quantite_stock-v_qte_concernee;

    END IF;
    IF :NEW.type_reclamation = 'echec' AND :NEW.etat='valide' THEN
        UPDATE ligne_commande_vente SET etat_echec='1'
        WHERE id_commande_vente=:NEW.id_commande_vente;

        INSERT INTO transaction(montant_transaction, type_transaction)
        VALUES (v_montant_remboursement*v_qte_concernee, 'echec de reception');
    END IF;
END;
/
--calcul de prix total commande de vente
CREATE OR REPLACE TRIGGER trg_ligne_commande_vente_prix_total
BEFORE INSERT OR UPDATE
ON ligne_commande_vente
FOR EACH ROW
DECLARE
    v_prix_produit NUMBER;
BEGIN
    SELECT prix INTO v_prix_produit FROM produit
    WHERE id_global_produit=:NEW.id_produit;

    UPDATE commande_vente SET prix_total=prix_total+v_prix_produit*:NEW.qte_vente
    WHERE id_commande_vente=:NEW.id_commande_vente;
END;
/
--calcul de prix total commande d'achat
CREATE OR REPLACE TRIGGER trg_ligne_commande_achat_prix_total
BEFORE INSERT OR UPDATE
ON ligne_commande_achat
FOR EACH ROW
DECLARE
    v_prix_produit NUMBER;
BEGIN
    SELECT prix INTO v_prix_produit FROM produit
    WHERE id_global_produit=:NEW.id_produit;

    UPDATE commande_achat SET prix_total=prix_total+v_prix_produit*:NEW.qte_achat
    WHERE id_commande_achat=:NEW.id_commande_achat;
END;
/
-- ajout de ligne d'achat -> augmentation de stock + transaction négative
CREATE OR REPLACE TRIGGER trg_commande_achat_transaction_stock
BEFORE INSERT
ON commande_achat
FOR EACH ROW
BEGIN
    INSERT INTO transaction (montant_transaction, type_transaction)
    VALUES (:NEW.prix_total, 'Commande de achat')
    WHERE id_transaction=:NEW.id_transaction;
END;
/
CREATE OR REPLACE TRIGGER trg_ligne_commande_achat_transaction_stock
BEFORE UPDATE
ON ligne_commande_achat
FOR EACH ROW
BEGIN
    UPDATE produit SET quantite_stock=quantite_stock - :NEW.qte_achat
    WHERE id_global_produit=:NEW.id_produit;
END;
/
-- quantité de produit = 0 -> disponible = '0'
CREATE OR REPLACE TRIGGER trg_produit_non_disponible
BEFORE UPDATE OR INSERT
ON produit
FOR EACH ROW
BEGIN
    IF :NEW.quantite_stock = '0' THEN
        UPDATE produit SET disponible='0'
        WHERE id_global_produit=:NEW.id_produit;
    END IF;
END;
/
-- dernier_inventaire
CREATE OR REPLACE TRIGGER trg_dernier_inventaire_produit
BEFORE UPDATE OR INSERT
ON tache_inventaire
FOR EACH ROW
DECLARE
    v_id_produit VARCHAR2(100);
BEGIN
    IF :NEW.etat = 'fait' THEN
        SELECT id_global_produit INTO v_id_produit FROM produit
        WHERE id_global_produit=:NEW.id_produit;

        UPDATE produit SET besoin_inventaire='0', dernier_inventaire=:NEW.date_tache
        WHERE id_global_produit=v_id_produit;
    END IF;
END;
/
-- Entrée de qte_physique -> etat ='fait'
CREATE OR REPLACE TRIGGER trg_qte_physique_fait
BEFORE UPDATE OR INSERT
ON tache_inventaire
FOR EACH ROW
BEGIN
    IF :NEW.qte_physique IS NOT NULL AND :OLD.qte_physique IS NULL THEN 
        :NEW.etat:='fait';
    END IF;
END;
/
-- etat de inventaire = 'termine' -> date_fin=sysdate
CREATE OR REPLACE TRIGGER trg_inventaire_termine_date_fin
BEFORE UPDATE OR INSERT
ON inventaire
FOR EACH ROW
BEGIN
    IF :NEW.etat='termine' THEN
        :NEW.date_fin:=SYSDATE;
    END IF;
END;
/
-- etat de toute tache = 'fait' soit 'annulée'-> etat inventaire = 'termine'
CREATE OR REPLACE TRIGGER trg_terminaison_inventaire_par_taches
BEFORE INSERT OR UPDATE
ON tache_inventaire
FOR EACH ROW
DECLARE
    v_nombre_taches_restant NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_nombre_taches_restant
    FROM tache_inventaire
    WHERE id_inventaire = :NEW.id_inventaire
    AND etat NOT IN ('fait', 'annulee');

    IF v_nombre_taches_restant = 0 THEN
        UPDATE inventaire SET etat='termine'
        WHERE id_inventaire=:NEW.id_inventaire;
    END IF;
END;
/
-- date de fin automatique apres un jour
CREATE OR REPLACE TRIGGER trg_date_fin_par_defaut
BEFORE INSERT
ON inventaire
BEGIN
    :NEW.date_fin:=SYSDATE+1;
END;
FOR EACH ROW
-- date debut correspond au premiere tache
CREATE OR REPLACE TRIGGER trg_mettre_a_jour_date_debut_inventaire
BEFORE INSERT OR UPDATE
ON tache_inventaire
FOR EACH ROW
DECLARE
    v_date_debut_inventaire DATE;
    v_date_fin_inventaire DATE;

    v_date_min DATE;

    e_erreur EXCEPTION;
BEGIN
    SELECT date_debut INTO v_date_debut_inventaire FROM inventaire
    WHERE id_inventaire=:NEW.id_inventaire;

    SELECT date_fin INTO v_date_fin_inventaire FROM inventaire
    WHERE id_inventaire=:NEW.id_inventaire;

    SELECT MIN(date_tache) INTO v_date_min FROM tache_inventaire;

    IF :NEW.date_tache < v_date_debut_inventaire OR :NEW.date_tache > v_date_fin_inventaire THEN
        RAISE e_erreur;
    END IF;
    
    
    IF v_date_min > v_date_inventaire THEN
        UPDATE inventaire SET date_debut=v_date_min
        WHERE id_inventaire=:NEW.id_inventaire;
    END IF;


    EXCEPTION
        WHEN e_erreur THEN 
            DBMS_OUTPUT.PUT_LINE('Date de tache conflit avec date de debut ou de fin d inventaire !');
END;
/
-- date de derniere tache depasse la date -> inventaire 'retard'
CREATE OR REPLACE TRIGGER trg_retard_inventaire
BEFORE UPDATE
ON tache_inventaire
FOR EACH ROW
DECLARE
    v_etat_derniere_tache VARCHAR2(10);
BEGIN
    SELECT etat INTO v_etat_derniere_tache FROM tache_inventaire
    WHERE date_tache = (SELECT MAX(date_tache) FROM tache_inventaire);

    IF v_etat_derniere_tache='retard' THEN
        UPDATE inventaire SET etat='en retard'
        WHERE id_inventaire=:NEW.id_inventaire;
    END IF;
END;
/
-- ajout de personne selon le type (condidat, personnel, client, fournisseur)
CREATE OR REPLACE TRIGGER trg_ajout_personne
BEFORE INSERT OR UPDATE
ON personne
FOR EACH ROW
DECLARE
    e_exception_age EXCEPTION;
    v_age DATE;
BEGIN
    
    v_age := SYSDATE - :NEW.date_naissance;

    IF v_age<18 THEN
        RAISE e_exception_age;
    END IF;

    IF type_utilisateur='condidat' THEN
        INSERT INTO condidat (id_personne, date_condidature, etat_condidature) 
        VALUES (:NEW.id_personne, SYSDATE, 'en attente');
    END IF;
    IF type_utilisateur='personnel' THEN
        INSERT INTO personnel (id_personne) VALUES (:NEW.id_personne);
    END IF;
    IF type_utilisateur='fournisseur' THEN
        INSERT INTO fournisseur (id_personne) VALUES (:NEW.id_personne);
    END IF;
    IF type_utilisateur='client' THEN
        INSERT INTO client (id_personne) VALUES (:NEW.id_personne);
    END IF;
    EXCEPTION
        WHEN e_exception_age THEN
            DBMS_OUTPUT.PUT_LINE('Age de la personne interdit. Seul un age de 18 ans ou plus est permis !')
END;
/
-- ajout de personnel selon le type (roles)
CREATE OR REPLACE TRIGGER trg_ajout_personne
BEFORE INSERT OR UPDATE
ON personne
FOR EACH ROW
BEGIN
    IF role='Responsable stock' THEN
        INSERT INTO responsable_stock (id_personnel)
        VALUES (:NEW.id_personnel);
    END IF;
    IF role='Responsable vente' THEN
        INSERT INTO responsable_vente (id_personnel)
        VALUES (:NEW.id_personnel);
    END IF;
    IF role='Responsable achat' THEN
        INSERT INTO responsable_achat (id_personnel)
        VALUES (:NEW.id_personnel);
    END IF;
    IF role='Responsable CRM' THEN
        INSERT INTO responsable_crm (id_personnel)
        VALUES (:NEW.id_personnel);
    END IF;
    IF role='Responsable SAV' THEN
        INSERT INTO responsable_sav (id_personnel)
        VALUES (:NEW.id_personnel);
    END IF;
    IF role='Ressources Humaines' THEN
        INSERT INTO Ressources_humaines (id_personnel)
        VALUES (:NEW.id_personnel);
    END IF;
    IF role='Employe stock' THEN
        INSERT INTO employe_stock (id_personnel)
        VALUES (:NEW.id_personnel);
    END IF;
    IF role='Commercant' THEN
        INSERT INTO commercant (id_personnel)
        VALUES (:NEW.id_personnel);
    END IF;
END;
/