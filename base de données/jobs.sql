BEGIN
    DBMS_SCHEDULER.create_job (
        job_name        => 'job_marquer_taches_en_retard',
        job_type        => 'PLSQL_BLOCK',
        job_action      => 'BEGIN marquer_taches_en_retard; END;',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=DAILY; BYHOUR=0; BYMINUTE=1',
        enabled         => TRUE,
        comments        => 'Met à jour les tâches en retard automatiquement chaque jour.'
    );
END;
/