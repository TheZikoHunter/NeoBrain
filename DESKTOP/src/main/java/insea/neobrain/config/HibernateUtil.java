package insea.neobrain.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import insea.neobrain.entity.*;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 */
public class HibernateUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;
    
    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            
            // Add entity classes
            configuration.addAnnotatedClass(Personne.class);
            configuration.addAnnotatedClass(Personnel.class);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Produit.class);
            configuration.addAnnotatedClass(Inventaire.class);
            configuration.addAnnotatedClass(TacheInventaire.class);
            configuration.addAnnotatedClass(CommandeVente.class);
            configuration.addAnnotatedClass(LigneCommande.class);
            configuration.addAnnotatedClass(Reclamation.class);
            
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            logger.info("Hibernate SessionFactory created successfully");
            
        } catch (Throwable ex) {
            logger.error("Initial SessionFactory creation failed", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * Get the SessionFactory instance
     * @return SessionFactory instance
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    /**
     * Close the SessionFactory
     */
    public static void shutdown() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
                logger.info("Hibernate SessionFactory closed successfully");
            } catch (Exception e) {
                logger.error("Error closing SessionFactory", e);
            }
        }
    }
    
    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            sessionFactory.openSession().close();
            logger.info("Database connection test successful");
            return true;
        } catch (Exception e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }
}
