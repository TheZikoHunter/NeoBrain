package insea.neobrain.service.impl;

import insea.neobrain.entity.EtatTache;
import insea.neobrain.entity.Inventaire;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Produit;
import insea.neobrain.entity.TacheInventaire;
import insea.neobrain.repository.TacheInventaireRepository;
import insea.neobrain.repository.impl.TacheInventaireRepositoryImpl;
import insea.neobrain.service.TacheInventaireService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;

/**
 * Implementation of TacheInventaireService
 */
public class TacheInventaireServiceImpl implements TacheInventaireService {
    
    private static final Logger LOGGER = Logger.getLogger(TacheInventaireServiceImpl.class.getName());
    private final TacheInventaireRepository repository;
    
    public TacheInventaireServiceImpl() {
        this.repository = new TacheInventaireRepositoryImpl();
    }
    
    @Override
    public TacheInventaire findById(Long id) {
        try {
            return repository.findById(id).orElse(null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding task by ID: " + id, e);
            return null;
        }
    }
    
    @Override
    public TacheInventaire save(TacheInventaire task) {
        try {
            if (task.getDateTache() == null) {
                task.setDateTache(LocalDateTime.now());
            }
            
            if (task.getEtatTache() == null) {
                task.setEtatTache(EtatTache.EN_ATTENTE);
            }
            
            return repository.save(task);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving task", e);
            throw new RuntimeException("Failed to save task: " + e.getMessage(), e);
        }
    }
    
    @Override
    public TacheInventaire update(TacheInventaire task) {
        try {
            if (task.getEtatTache() == EtatTache.TERMINEE && task.getQuantitePhysique() == null) {
                throw new IllegalArgumentException("Physical quantity must be specified for completed tasks");
            }
            
            TacheInventaire result = repository.update(task);
            
            // If task is completed, update the product's inventory date
            if (task.getEtatTache() == EtatTache.TERMINEE && task.getProduit() != null) {
                task.getProduit().setDernierInventaire(LocalDateTime.now());
            }
            
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating task", e);
            throw new RuntimeException("Failed to update task: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void delete(TacheInventaire task) {
        try {
            repository.delete(task);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting task", e);
            throw new RuntimeException("Failed to delete task: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<TacheInventaire> findAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding all tasks", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<TacheInventaire> findByInventaire(Inventaire inventaire) {
        try {
            return repository.findByInventaire(inventaire);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding tasks by inventory", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<TacheInventaire> findByProduit(Produit produit) {
        try {
            return repository.findByProduit(produit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding tasks by product", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<TacheInventaire> findByPersonnel(Personnel personnel) {
        try {
            return repository.findByPersonnel(personnel);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding tasks by personnel", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<TacheInventaire> findByStatus(EtatTache status) {
        try {
            return repository.findByEtatTache(status);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding tasks by status", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<TacheInventaire> findByPersonnelAndStatus(Personnel personnel, EtatTache status) {
        try {
            return repository.findByPersonnelAndEtatTache(personnel, status);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding tasks by personnel and status", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<TacheInventaire> findByInventaireAndStatus(Inventaire inventaire, EtatTache status) {
        try {
            return repository.findByInventaireAndEtatTache(inventaire, status);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding tasks by inventory and status", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<TacheInventaire> findUnassignedTasks() {
        try {
            return repository.findUnassigned();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding unassigned tasks", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public TacheInventaire assignTask(TacheInventaire task, Personnel personnel) {
        try {
            if (task == null || personnel == null) {
                throw new IllegalArgumentException("Task and personnel cannot be null");
            }
            
            task.setPersonnel(personnel);
            task.setEtatTache(EtatTache.EN_ATTENTE);
            
            return repository.update(task);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error assigning task", e);
            throw new RuntimeException("Failed to assign task: " + e.getMessage(), e);
        }
    }
    
    @Override
    public TacheInventaire completeTask(TacheInventaire task, Integer physicalCount) {
        try {
            if (task == null) {
                throw new IllegalArgumentException("Task cannot be null");
            }
            
            if (physicalCount == null) {
                throw new IllegalArgumentException("Physical count cannot be null for completed tasks");
            }
            
            task.setQuantitePhysique(physicalCount);
            task.setEtatTache(EtatTache.TERMINEE);
            
            // Update the product's inventory date
            if (task.getProduit() != null) {
                task.getProduit().setDernierInventaire(LocalDateTime.now());
                
                // Handle inventory discrepancy if needed
                if (task.getProduit().getQuantiteStock() != physicalCount) {
                    task.getProduit().setQuantiteStock(physicalCount);
                    task.getProduit().setDerniereModification(LocalDateTime.now());
                }
            }
            
            return repository.update(task);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error completing task", e);
            throw new RuntimeException("Failed to complete task: " + e.getMessage(), e);
        }
    }
}
