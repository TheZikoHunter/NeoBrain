package insea.neobrain.repository;

import insea.neobrain.entity.TacheInventaire;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Produit;
import insea.neobrain.entity.Inventaire;
import insea.neobrain.entity.EtatTache;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for TacheInventaire entity
 * Provides data access methods for inventory task management
 */
public interface TacheInventaireRepository extends GenericRepository<TacheInventaire, Long> {
    
    /**
     * Find tasks assigned to a specific personnel
     * @param personnel The personnel assigned to tasks
     * @return List of tasks assigned to the personnel
     */
    List<TacheInventaire> findByPersonnel(Personnel personnel);
    
    /**
     * Find tasks for a specific product
     * @param produit The product
     * @return List of tasks for the product
     */
    List<TacheInventaire> findByProduit(Produit produit);
    
    /**
     * Find tasks for a specific inventory
     * @param inventaire The inventory
     * @return List of tasks for the inventory
     */
    List<TacheInventaire> findByInventaire(Inventaire inventaire);
    
    /**
     * Find tasks by status
     * @param etat The task status
     * @return List of tasks with the specified status
     */
    List<TacheInventaire> findByEtat(EtatTache etat);
    
    /**
     * Find tasks by exact status (alias for findByEtat)
     * @param etatTache The task status
     * @return List of tasks with the specified status
     */
    List<TacheInventaire> findByEtatTache(EtatTache etatTache);
    
    /**
     * Find tasks assigned between dates
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of tasks assigned in the date range
     */
    List<TacheInventaire> findByDateAssignationBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find tasks by date range
     * @param startDateTime Start date time (inclusive)
     * @param endDateTime End date time (inclusive)
     * @return List of tasks in the date range
     */
    List<TacheInventaire> findByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Find completed tasks between dates
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of completed tasks in the date range
     */
    List<TacheInventaire> findCompletedBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find tasks with discrepancies between physical and theoretical quantities
     * @return List of tasks with quantity discrepancies
     */
    List<TacheInventaire> findWithDiscrepancies();
    
    /**
     * Find tasks with significant discrepancies
     * @param threshold Minimum discrepancy threshold
     * @return List of tasks with discrepancies >= threshold
     */
    List<TacheInventaire> findWithSignificantDiscrepancies(int threshold);
    
    /**
     * Find overdue tasks (deadline passed but not completed)
     * @return List of overdue tasks
     */
    List<TacheInventaire> findOverdueTasks();
    
    /**
     * Find pending tasks for a personnel
     * @param personnel The personnel
     * @return List of pending tasks assigned to the personnel
     */
    List<TacheInventaire> findPendingTasksByPersonnel(Personnel personnel);
    
    /**
     * Count tasks by status for a specific inventory
     * @param inventaire The inventory
     * @param etat The task status
     * @return Number of tasks with the specified status
     */
    long countByInventaireAndEtat(Inventaire inventaire, EtatTache etat);
    
    /**
     * Find tasks by priority (deadline within days)
     * @param days Number of days from now
     * @return List of high priority tasks (deadline within specified days)
     */
    List<TacheInventaire> findHighPriorityTasks(int days);
    
    /**
     * Get task completion statistics for personnel
     * @param personnel The personnel
     * @return Array with [completed, total] task counts
     */
    Object[] getTaskStatisticsByPersonnel(Personnel personnel);
    
    /**
     * Find latest task for each product in an inventory
     * @param inventaire The inventory
     * @return List of latest tasks per product
     */
    List<TacheInventaire> findLatestTasksPerProduct(Inventaire inventaire);
    
    /**
     * Find tasks by personnel and status
     * @param personnel The personnel assigned to tasks
     * @param etatTache The task status
     * @return List of tasks assigned to the personnel with the specified status
     */
    List<TacheInventaire> findByPersonnelAndEtatTache(Personnel personnel, EtatTache etatTache);
    
    /**
     * Find tasks by inventory and status
     * @param inventaire The inventory
     * @param etatTache The task status
     * @return List of tasks for the inventory with the specified status
     */
    List<TacheInventaire> findByInventaireAndEtatTache(Inventaire inventaire, EtatTache etatTache);
    
    /**
     * Find unassigned tasks (tasks with no personnel assigned)
     * @return List of unassigned tasks
     */
    List<TacheInventaire> findUnassigned();
}
