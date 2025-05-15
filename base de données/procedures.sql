CREATE OR REPLACE PROCEDURE marquer_taches_en_retard AS
BEGIN
    UPDATE tache_inventaire
    SET etat = 'retard'
    WHERE date_tache < TRUNC(SYSDATE)
      AND etat NOT IN ('fait', 'annulee', 'retard');
    
    COMMIT;
END;
/