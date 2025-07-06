package session;

import model.Reservation;
import model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Stateless
public class ReservationBean {
    
    @PersistenceContext(unitName = "roomReservationPU")
    private EntityManager em;
    
    public void createReservation(Reservation reservation) {
        em.persist(reservation);
    }
    
    public Reservation findReservation(Long id) {
        return em.find(Reservation.class, id);
    }
    
    public List<Reservation> findAllReservations() {
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findAll", Reservation.class);
        return query.getResultList();
    }
    
    public List<Reservation> findReservationsByUser(User user) {
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findByUser", Reservation.class);
        query.setParameter("user", user);
        return query.getResultList();
    }
    
    public List<Reservation> findReservationsByStatus(String status) {
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findByStatus", Reservation.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public boolean isRoomAvailable(String roomNumber, LocalDate date, LocalTime startTime, LocalTime endTime) {
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findByDateAndRoom", Reservation.class);
        query.setParameter("date", date);
        query.setParameter("room", roomNumber);
        List<Reservation> existingReservations = query.getResultList();
        
        for (Reservation existing : existingReservations) {
            if (timesOverlap(existing.getStartTime(), existing.getEndTime(), startTime, endTime)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean timesOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
    
    public void updateReservation(Reservation reservation) {
        em.merge(reservation);
    }
    
    public void deleteReservation(Long id) {
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation != null) {
            em.remove(reservation);
        }
    }
    
    public void approveReservation(Long id) {
        Reservation reservation = findReservation(id);
        if (reservation != null) {
            reservation.setStatus("APPROVED");
            updateReservation(reservation);
        }
    }
    
    public void rejectReservation(Long id) {
        Reservation reservation = findReservation(id);
        if (reservation != null) {
            reservation.setStatus("REJECTED");
            updateReservation(reservation);
        }
    }
    
    public void cancelReservation(Long id) {
        Reservation reservation = findReservation(id);
        if (reservation != null) {
            reservation.setStatus("CANCELLED");
            updateReservation(reservation);
        }
    }
}