package insea.neobrain.repository.impl;

import insea.neobrain.config.HibernateUtil;
import insea.neobrain.repository.GenericRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

/**
 * Generic repository implementation providing basic CRUD operations
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public abstract class GenericRepositoryImpl<T, ID> implements GenericRepository<T, ID> {
    
    private static final Logger logger = LoggerFactory.getLogger(GenericRepositoryImpl.class);
    
    protected final SessionFactory sessionFactory;
    protected final Class<T> entityClass;
    
    @SuppressWarnings("unchecked")
    public GenericRepositoryImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    @Override
    public T save(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            logger.debug("Entity saved: {}", entity);
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            // Enhanced debug logging for root cause
            logger.error("Error saving entity: {}\nEntity class: {}\nEntity toString: {}", entity, entity != null ? entity.getClass().getName() : "null", entity);
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            logger.error("Root cause: {}: {}", cause.getClass().getName(), cause.getMessage(), cause);
            // --- BEGIN: Write error to error.log file ---
            try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter("error.log", true))) {
                out.println("[ERROR] Failed to save entity: " + entity);
                out.println("[ERROR] Entity class: " + (entity != null ? entity.getClass().getName() : "null"));
                out.println("[ERROR] Root cause: " + cause.getClass().getName() + ": " + cause.getMessage());
                cause.printStackTrace(out);
                out.println("------------------------------");
            } catch (Exception fileEx) {
                fileEx.printStackTrace();
            }
            // --- END: Write error to error.log file ---
            throw new RuntimeException("Error saving entity: " + cause.getMessage(), e);
        }
    }
    
    @Override
    public T update(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T mergedEntity = session.merge(entity);
            transaction.commit();
            logger.debug("Entity updated: {}", mergedEntity);
            return mergedEntity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating entity: {}", entity, e);
            throw new RuntimeException("Error updating entity", e);
        }
    }
    
    @Override
    public T saveOrUpdate(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
            logger.debug("Entity saved or updated: {}", entity);
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving or updating entity: {}", entity, e);
            throw new RuntimeException("Error saving or updating entity", e);
        }
    }
    
    @Override
    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
            logger.debug("Entity deleted: {}", entity);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting entity: {}", entity, e);
            throw new RuntimeException("Error deleting entity", e);
        }
    }
    
    @Override
    public void deleteById(ID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.remove(entity);
                logger.debug("Entity deleted by ID: {}", id);
            } else {
                logger.warn("Entity not found for deletion with ID: {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting entity by ID: {}", id, e);
            throw new RuntimeException("Error deleting entity by ID", e);
        }
    }
    
    @Override
    public Optional<T> findById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            T entity = session.get(entityClass, id);
            logger.debug("Entity found by ID {}: {}", id, entity != null);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            logger.error("Error finding entity by ID: {}", id, e);
            throw new RuntimeException("Error finding entity by ID", e);
        }
    }
    
    @Override
    public boolean existsById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            T entity = session.get(entityClass, id);
            boolean exists = entity != null;
            logger.debug("Entity exists by ID {}: {}", id, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking entity existence by ID: {}", id, e);
            throw new RuntimeException("Error checking entity existence by ID", e);
        }
    }
    
    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM " + entityClass.getSimpleName();
            List<T> entities = session.createQuery(hql, entityClass).getResultList();
            logger.debug("Found {} entities of type {}", entities.size(), entityClass.getSimpleName());
            return entities;
        } catch (Exception e) {
            logger.error("Error finding all entities of type: {}", entityClass.getSimpleName(), e);
            throw new RuntimeException("Error finding all entities", e);
        }
    }
    
    @Override
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(*) FROM " + entityClass.getSimpleName();
            Long count = session.createQuery(hql, Long.class).getSingleResult();
            logger.debug("Count of entities of type {}: {}", entityClass.getSimpleName(), count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting entities of type: {}", entityClass.getSimpleName(), e);
            throw new RuntimeException("Error counting entities", e);
        }
    }
    
    @Override
    public List<T> findWithPagination(int offset, int limit) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM " + entityClass.getSimpleName();
            List<T> entities = session.createQuery(hql, entityClass)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
            logger.debug("Found {} entities with pagination (offset: {}, limit: {})", 
                        entities.size(), offset, limit);
            return entities;
        } catch (Exception e) {
            logger.error("Error finding entities with pagination (offset: {}, limit: {})", offset, limit, e);
            throw new RuntimeException("Error finding entities with pagination", e);
        }
    }
    
    /**
     * Get current session for custom queries
     * @return current session
     */
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    /**
     * Open a new session for custom operations
     * @return new session
     */
    protected Session openSession() {
        return sessionFactory.openSession();
    }
}
