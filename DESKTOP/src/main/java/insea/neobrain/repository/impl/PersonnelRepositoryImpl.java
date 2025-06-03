package insea.neobrain.repository.impl;

import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Role;
import insea.neobrain.repository.PersonnelRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of PersonnelRepository
 */
public class PersonnelRepositoryImpl extends GenericRepositoryImpl<Personnel, Long> implements PersonnelRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(PersonnelRepositoryImpl.class);
    private final BCryptPasswordEncoder passwordEncoder;
    
    public PersonnelRepositoryImpl() {
        super();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public Optional<Personnel> findByIdPersonnel(String idPersonnel) {
        try (Session session = openSession()) {
            Query<Personnel> query = session.createQuery(
                "FROM Personnel p WHERE p.idPersonnel = :idPersonnel", Personnel.class);
            query.setParameter("idPersonnel", idPersonnel);
            Personnel personnel = query.uniqueResult();
            logger.debug("Personnel found by ID personnel {}: {}", idPersonnel, personnel != null);
            return Optional.ofNullable(personnel);
        } catch (Exception e) {
            logger.error("Error finding personnel by ID personnel: {}", idPersonnel, e);
            throw new RuntimeException("Error finding personnel by ID personnel", e);
        }
    }
    
    @Override
    public Optional<Personnel> findByEmail(String email) {
        try (Session session = openSession()) {
            Query<Personnel> query = session.createQuery(
                "FROM Personnel p WHERE p.email = :email", Personnel.class);
            query.setParameter("email", email);
            Personnel personnel = query.uniqueResult();
            logger.debug("Personnel found by email {}: {}", email, personnel != null);
            return Optional.ofNullable(personnel);
        } catch (Exception e) {
            logger.error("Error finding personnel by email: {}", email, e);
            throw new RuntimeException("Error finding personnel by email", e);
        }
    }
    
    @Override
    public List<Personnel> findByRole(Role role) {
        try (Session session = openSession()) {
            Query<Personnel> query = session.createQuery(
                "FROM Personnel p WHERE p.role = :role ORDER BY p.nom, p.prenom", Personnel.class);
            query.setParameter("role", role);
            List<Personnel> personnel = query.getResultList();
            logger.debug("Found {} personnel with role: {}", personnel.size(), role);
            return personnel;
        } catch (Exception e) {
            logger.error("Error finding personnel by role: {}", role, e);
            throw new RuntimeException("Error finding personnel by role", e);
        }
    }
    
    @Override
    public List<Personnel> findActive() {
        try (Session session = openSession()) {
            Query<Personnel> query = session.createQuery(
                "FROM Personnel p WHERE p.actif = true ORDER BY p.nom, p.prenom", Personnel.class);
            List<Personnel> personnel = query.getResultList();
            logger.debug("Found {} active personnel", personnel.size());
            return personnel;
        } catch (Exception e) {
            logger.error("Error finding active personnel", e);
            throw new RuntimeException("Error finding active personnel", e);
        }
    }
    
    @Override
    public List<Personnel> findByName(String nom, String prenom) {
        try (Session session = openSession()) {
            Query<Personnel> query = session.createQuery(
                "FROM Personnel p WHERE p.nom = :nom AND p.prenom = :prenom", Personnel.class);
            query.setParameter("nom", nom);
            query.setParameter("prenom", prenom);
            List<Personnel> personnel = query.getResultList();
            logger.debug("Found {} personnel with name: {} {}", personnel.size(), nom, prenom);
            return personnel;
        } catch (Exception e) {
            logger.error("Error finding personnel by name: {} {}", nom, prenom, e);
            throw new RuntimeException("Error finding personnel by name", e);
        }
    }
    
    @Override
    public List<Personnel> findInventoryCapablePersonnel() {
        try (Session session = openSession()) {
            Query<Personnel> query = session.createQuery(
                "FROM Personnel p WHERE p.actif = true AND " +
                "(p.role = :responsableStock OR p.role = :employeStock) " +
                "ORDER BY p.role, p.nom, p.prenom", Personnel.class);
            query.setParameter("responsableStock", Role.RESPONSABLE_STOCK);
            query.setParameter("employeStock", Role.EMPLOYE_STOCK);
            List<Personnel> personnel = query.getResultList();
            logger.debug("Found {} inventory capable personnel", personnel.size());
            return personnel;
        } catch (Exception e) {
            logger.error("Error finding inventory capable personnel", e);
            throw new RuntimeException("Error finding inventory capable personnel", e);
        }
    }
    
    @Override
    public List<Personnel> searchByName(String searchTerm) {
        try (Session session = openSession()) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            Query<Personnel> query = session.createQuery(
                "FROM Personnel p WHERE LOWER(CONCAT(p.nom, ' ', p.prenom)) LIKE :searchTerm " +
                "OR LOWER(CONCAT(p.prenom, ' ', p.nom)) LIKE :searchTerm " +
                "ORDER BY p.nom, p.prenom", Personnel.class);
            query.setParameter("searchTerm", searchPattern);
            List<Personnel> personnel = query.getResultList();
            logger.debug("Found {} personnel matching search term: {}", personnel.size(), searchTerm);
            return personnel;
        } catch (Exception e) {
            logger.error("Error searching personnel by name: {}", searchTerm, e);
            throw new RuntimeException("Error searching personnel by name", e);
        }
    }
    
    @Override
    public Optional<Personnel> authenticate(String email, String motDePasse) {
        try (Session session = openSession()) {
            Query<Personnel> query = session.createQuery(
                "FROM Personnel p WHERE p.email = :email AND p.actif = true", Personnel.class);
            query.setParameter("email", email);
            Personnel personnel = query.uniqueResult();
            
            if (personnel != null && passwordEncoder.matches(motDePasse, personnel.getMotDePasse())) {
                logger.debug("Authentication successful for: {}", email);
                return Optional.of(personnel);
            } else {
                logger.debug("Authentication failed for: {}", email);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error authenticating personnel: {}", email, e);
            throw new RuntimeException("Error authenticating personnel", e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Personnel p WHERE p.email = :email", Long.class);
            query.setParameter("email", email);
            Long count = query.getSingleResult();
            boolean exists = count != null && count > 0;
            logger.debug("Email {} exists: {}", email, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking email existence: {}", email, e);
            throw new RuntimeException("Error checking email existence", e);
        }
    }
    
    @Override
    public boolean existsByIdPersonnel(String idPersonnel) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Personnel p WHERE p.idPersonnel = :idPersonnel", Long.class);
            query.setParameter("idPersonnel", idPersonnel);
            Long count = query.getSingleResult();
            boolean exists = count != null && count > 0;
            logger.debug("ID personnel {} exists: {}", idPersonnel, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking ID personnel existence: {}", idPersonnel, e);
            throw new RuntimeException("Error checking ID personnel existence", e);
        }
    }
    
    @Override
    public Optional<Personnel> findByNumeroPersonnel(String numeroPersonnel) {
        return findByIdPersonnel(numeroPersonnel); // Alias method
    }
    
    @Override
    public List<Personnel> findByNomAndPrenom(String nom, String prenom) {
        return findByName(nom, prenom); // Alias method
    }
    
    @Override
    public java.util.Map<Role, Long> getPersonnelCountByRole() {
        try (Session session = openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT p.role, COUNT(p) FROM Personnel p GROUP BY p.role", Object[].class);
            List<Object[]> results = query.getResultList();
            
            java.util.Map<Role, Long> countByRole = new java.util.HashMap<>();
            for (Object[] result : results) {
                Role role = (Role) result[0];
                Long count = (Long) result[1];
                countByRole.put(role, count);
            }
            
            logger.debug("Personnel count by role: {}", countByRole);
            return countByRole;
        } catch (Exception e) {
            logger.error("Error getting personnel count by role", e);
            throw new RuntimeException("Error getting personnel count by role", e);
        }
    }
    
    @Override
    public List<Personnel> searchPersonnel(String nom, String prenom, Role role, String email, String telephone) {
        try (Session session = openSession()) {
            StringBuilder hql = new StringBuilder("FROM Personnel p WHERE 1=1");
            
            if (nom != null && !nom.trim().isEmpty()) {
                hql.append(" AND LOWER(p.nom) LIKE :nom");
            }
            if (prenom != null && !prenom.trim().isEmpty()) {
                hql.append(" AND LOWER(p.prenom) LIKE :prenom");
            }
            if (role != null) {
                hql.append(" AND p.role = :role");
            }
            if (email != null && !email.trim().isEmpty()) {
                hql.append(" AND LOWER(p.email) LIKE :email");
            }
            if (telephone != null && !telephone.trim().isEmpty()) {
                hql.append(" AND p.telephone LIKE :telephone");
            }
            
            hql.append(" ORDER BY p.nom, p.prenom");
            
            Query<Personnel> query = session.createQuery(hql.toString(), Personnel.class);
            
            if (nom != null && !nom.trim().isEmpty()) {
                query.setParameter("nom", "%" + nom.toLowerCase() + "%");
            }
            if (prenom != null && !prenom.trim().isEmpty()) {
                query.setParameter("prenom", "%" + prenom.toLowerCase() + "%");
            }
            if (role != null) {
                query.setParameter("role", role);
            }
            if (email != null && !email.trim().isEmpty()) {
                query.setParameter("email", "%" + email.toLowerCase() + "%");
            }
            if (telephone != null && !telephone.trim().isEmpty()) {
                query.setParameter("telephone", "%" + telephone + "%");
            }
            
            List<Personnel> personnel = query.getResultList();
            logger.debug("Found {} personnel matching search criteria", personnel.size());
            return personnel;
        } catch (Exception e) {
            logger.error("Error searching personnel", e);
            throw new RuntimeException("Error searching personnel", e);
        }
    }
    
    @Override
    public long countByPersonnelNumberPrefix(String prefix) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Personnel p WHERE p.idPersonnel LIKE :prefix", Long.class);
            query.setParameter("prefix", prefix + "%");
            Long count = query.getSingleResult();
            logger.debug("Count of personnel with prefix {}: {}", prefix, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting personnel by prefix: {}", prefix, e);
            throw new RuntimeException("Error counting personnel by prefix", e);
        }
    }
    
    @Override
    public long countByRole(Role role) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Personnel p WHERE p.role = :role", Long.class);
            query.setParameter("role", role);
            Long count = query.getSingleResult();
            logger.debug("Count of personnel with role {}: {}", role, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting personnel by role: {}", role, e);
            throw new RuntimeException("Error counting personnel by role", e);
        }
    }
    
    @Override
    public boolean canDeletePersonnel(Long personnelId) {
        try (Session session = openSession()) {
            // Check if personnel has any related records that would prevent deletion
            // Only check inventory tasks, since CommandeVente does not reference personnel
            Query<Long> taskQuery = session.createQuery(
                "SELECT COUNT(t) FROM TacheInventaire t WHERE t.personnel.idPersonne = :personnelId", Long.class);
            taskQuery.setParameter("personnelId", personnelId);
            Long taskCount = taskQuery.getSingleResult();

            boolean canDelete = (taskCount == null || taskCount == 0);
            logger.debug("Personnel {} can be deleted: {} (tasks: {})", 
                personnelId, canDelete, taskCount);
            return canDelete;
        } catch (Exception e) {
            logger.error("Error checking if personnel can be deleted: {}", personnelId, e);
            throw new RuntimeException("Error checking if personnel can be deleted", e);
        }
    }
    
    @Override
    public List<Personnel> findByDateNaissanceBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        try (Session session = openSession()) {
            Query<Personnel> query = session.createQuery(
                "FROM Personnel p WHERE p.dateNaissance BETWEEN :startDate AND :endDate " +
                "ORDER BY p.dateNaissance, p.nom, p.prenom", Personnel.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<Personnel> personnel = query.getResultList();
            logger.debug("Found {} personnel born between {} and {}", personnel.size(), startDate, endDate);
            return personnel;
        } catch (Exception e) {
            logger.error("Error finding personnel by date of birth range: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error finding personnel by date of birth range", e);
        }
    }
    
    // Find Personnel by username (nom_utilisateur) via join with Personne
    public Optional<Personnel> findByNomUtilisateur(String nomUtilisateur) {
        try (Session session = openSession()) {
            Query<Personnel> query = session.createQuery(
                "SELECT p FROM Personnel p WHERE p.nomUtilisateur = :nomUtilisateur", Personnel.class);
            query.setParameter("nomUtilisateur", nomUtilisateur);
            Personnel personnel = query.uniqueResult();
            logger.debug("Personnel found by username {}: {}", nomUtilisateur, personnel != null);
            return Optional.ofNullable(personnel);
        } catch (Exception e) {
            logger.error("Error finding personnel by username: {}", nomUtilisateur, e);
            throw new RuntimeException("Error finding personnel by username", e);
        }
    }
}
