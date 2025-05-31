package insea.neobrain.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import insea.neobrain.entity.Admin;
import insea.neobrain.config.HibernateConfig;

public class AdminDAO {
    public void saveAdmin(Admin admin) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(admin);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to save admin", e);
        }
    }

    public Admin findByUsername(String username) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<Admin> query = session.createQuery("FROM Admin WHERE username = :username", Admin.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find admin by username", e);
        }
    }
}
