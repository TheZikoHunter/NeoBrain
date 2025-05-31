// filepath: /home/zakaria/projets/neobrain/desktop_erp/neobrain/src/main/java/insea/neobrain/config/HibernateConfig.java
package insea.neobrain.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import insea.neobrain.entity.Admin;

public class HibernateConfig {
    
    private static SessionFactory sessionFactory;
    
    static {
        try {
            Configuration configuration = new Configuration();
            
            // Database connection properties
            configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/neobrain");
            configuration.setProperty("hibernate.connection.username", "postgres");
            configuration.setProperty("hibernate.connection.password", "highlevel");
            
            // Hibernate properties
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.format_sql", "true");
            
            // Connection pool
            configuration.setProperty("hibernate.connection.pool_size", "10");
            
            // Add entity classes
            configuration.addAnnotatedClass(Admin.class);
            
            sessionFactory = configuration.buildSessionFactory();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Failed to create SessionFactory: " + e);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}