package session;

import model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class AdminBean {
    
    @PersistenceContext(unitName = "roomReservationPU")
    private EntityManager em;
    
    public void createUser(User user) {
        em.persist(user);
    }
    
    public User findUser(Long id) {
        return em.find(User.class, id);
    }
    
    public User findUserByUsername(String username) {
        TypedQuery<User> query = em.createNamedQuery("User.findByUsername", User.class);
        query.setParameter("username", username);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }
    
    public User authenticateUser(String username, String password) {
        TypedQuery<User> query = em.createNamedQuery("User.findByUsernameAndPassword", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }
    
    public List<User> findAllUsers() {
        TypedQuery<User> query = em.createNamedQuery("User.findAll", User.class);
        return query.getResultList();
    }
    
    public void updateUser(User user) {
        em.merge(user);
    }
    
    public void deleteUser(Long id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        }
    }
    
    public boolean isUsernameAvailable(String username) {
        return findUserByUsername(username) == null;
    }
    
    public boolean registerUser(String username, String password, String email) {
        if (!isUsernameAvailable(username)) {
            return false;
        }
        
        User user = new User(username, password, email);
        createUser(user);
        return true;
    }
}
