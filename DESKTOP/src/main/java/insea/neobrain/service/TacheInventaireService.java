package insea.neobrain.service;

import insea.neobrain.entity.EtatTache;
import insea.neobrain.entity.Inventaire;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Produit;
import insea.neobrain.entity.TacheInventaire;

import java.util.List;

/**
 * Service for managing inventory tasks
 */
public interface TacheInventaireService {
    
    /**
     * Find a task by its ID
     */
    TacheInventaire findById(Long id);
    
    /**
     * Save a new task
     */
    TacheInventaire save(TacheInventaire task);
    
    /**
     * Update an existing task
     */
    TacheInventaire update(TacheInventaire task);
    
    /**
     * Delete a task
     */
    void delete(TacheInventaire task);
    
    /**
     * Find all tasks
     */
    List<TacheInventaire> findAll();
    
    /**
     * Find tasks by inventory
     */
    List<TacheInventaire> findByInventaire(Inventaire inventaire);
    
    /**
     * Find tasks by product
     */
    List<TacheInventaire> findByProduit(Produit produit);
    
    /**
     * Find tasks by personnel
     */
    List<TacheInventaire> findByPersonnel(Personnel personnel);
    
    /**
     * Find tasks by status
     */
    List<TacheInventaire> findByStatus(EtatTache status);
    
    /**
     * Find tasks by personnel and status
     */
    List<TacheInventaire> findByPersonnelAndStatus(Personnel personnel, EtatTache status);
    
    /**
     * Find tasks by inventory and status
     */
    List<TacheInventaire> findByInventaireAndStatus(Inventaire inventaire, EtatTache status);
    
    /**
     * Find unassigned tasks
     */
    List<TacheInventaire> findUnassignedTasks();
    
    /**
     * Assign a task to a personnel
     */
    TacheInventaire assignTask(TacheInventaire task, Personnel personnel);
    
    /**
     * Mark a task as complete with physical count
     */
    TacheInventaire completeTask(TacheInventaire task, Integer physicalCount);
}
