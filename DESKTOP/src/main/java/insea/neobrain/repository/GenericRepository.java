package insea.neobrain.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface providing basic CRUD operations
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface GenericRepository<T, ID> {
    
    /**
     * Save an entity
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);
    
    /**
     * Update an entity
     * @param entity the entity to update
     * @return the updated entity
     */
    T update(T entity);
    
    /**
     * Save or update an entity
     * @param entity the entity to save or update
     * @return the saved/updated entity
     */
    T saveOrUpdate(T entity);
    
    /**
     * Delete an entity
     * @param entity the entity to delete
     */
    void delete(T entity);
    
    /**
     * Delete an entity by its ID
     * @param id the ID of the entity to delete
     */
    void deleteById(ID id);
    
    /**
     * Find an entity by its ID
     * @param id the ID of the entity
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Check if an entity exists by its ID
     * @param id the ID to check
     * @return true if entity exists, false otherwise
     */
    boolean existsById(ID id);
    
    /**
     * Find all entities
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Count total number of entities
     * @return total count
     */
    long count();
    
    /**
     * Find entities with pagination
     * @param offset the offset for pagination
     * @param limit the limit for pagination
     * @return list of entities for the specified page
     */
    List<T> findWithPagination(int offset, int limit);
}
