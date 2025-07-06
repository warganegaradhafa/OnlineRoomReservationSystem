package controller;

import model.Reservation;
import model.User;
import session.ReservationBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Arrays;

@Named
@SessionScoped
public class ReservationController implements Serializable {
    
    @Inject
    private ReservationBean reservationBean;
    
    @Inject
    private LoginController loginController;
    
    // Form fields
    private String roomNumber;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String purpose;
    
    // Lists for display
    private List<Reservation> userReservations;
    private List<Reservation> allReservations;
    
    // Available rooms and time slots
    private final List<String> availableRooms = Arrays.asList(
        "A101", "A102", "A103", "B101", "B102", "B103", 
        "C101", "C102", "C103", "Conference Room 1", "Conference Room 2"
    );
    
    private final List<String> timeSlots = Arrays.asList(
        "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", 
        "14:00", "15:00", "16:00", "17:00", "18:00"
    );
    
    // Create new reservation
    public String createReservation() {
        if (!validateReservationForm()) {
            return null;
        }
        
        User currentUser = loginController.getCurrentUser();
        if (currentUser == null) {
            addErrorMessage("Please login to make a reservation");
            return "index.xhtml?faces-redirect=true";
        }
        
        // Check if room is available
        if (!reservationBean.isRoomAvailable(roomNumber, reservationDate, startTime, endTime)) {
            addErrorMessage("Room is not available for the selected time slot");
            return null;
        }
        
        Reservation reservation = new Reservation(roomNumber, currentUser, reservationDate, 
                                                startTime, endTime, purpose);
        
        try {
            reservationBean.createReservation(reservation);
            addInfoMessage("Reservation created successfully! Waiting for admin approval.");
            clearForm();
            return "reservationStatus.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addErrorMessage("Error creating reservation: " + e.getMessage());
            return null;
        }
    }
    
    // Load user reservations
    public void loadUserReservations() {
        User currentUser = loginController.getCurrentUser();
        if (currentUser != null) {
            userReservations = reservationBean.findReservationsByUser(currentUser);
        }
    }
    
    // Load all reservations (for admin)
    public void loadAllReservations() {
        allReservations = reservationBean.findAllReservations();
    }
    
    // Cancel reservation
    public String cancelReservation(Long reservationId) {
        try {
            reservationBean.cancelReservation(reservationId);
            addInfoMessage("Reservation cancelled successfully");
            loadUserReservations();
            return null;
        } catch (Exception e) {
            addErrorMessage("Error cancelling reservation: " + e.getMessage());
            return null;
        }
    }
    
    // Admin actions
    public String approveReservation(Long reservationId) {
        try {
            reservationBean.approveReservation(reservationId);
            addInfoMessage("Reservation approved successfully");
            loadAllReservations();
            return null;
        } catch (Exception e) {
            addErrorMessage("Error approving reservation: " + e.getMessage());
            return null;
        }
    }
    
    public String rejectReservation(Long reservationId) {
        try {
            reservationBean.rejectReservation(reservationId);
            addInfoMessage("Reservation rejected successfully");
            loadAllReservations();
            return null;
        } catch (Exception e) {
            addErrorMessage("Error rejecting reservation: " + e.getMessage());
            return null;
        }
    }
    
    // Form validation
    private boolean validateReservationForm() {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            addErrorMessage("Please select a room");
            return false;
        }
        
        if (reservationDate == null) {
            addErrorMessage("Please select a date");
            return false;
        }
        
        if (reservationDate.isBefore(LocalDate.now())) {
            addErrorMessage("Reservation date cannot be in the past");
            return false;
        }
        
        if (startTime == null || endTime == null) {
            addErrorMessage("Please select start and end times");
            return false;
        }
        
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            addErrorMessage("Start time must be before end time");
            return false;
        }
        
        return true;
    }
    
    // Utility methods
    private void clearForm() {
        roomNumber = null;
        reservationDate = null;
        startTime = null;
        endTime = null;
        purpose = null;
    }
    
    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
    
    private void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }
    
    // Getters and Setters
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public LocalDate getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public List<Reservation> getUserReservations() { return userReservations; }
    public void setUserReservations(List<Reservation> userReservations) { this.userReservations = userReservations; }
    
    public List<Reservation> getAllReservations() { return allReservations; }
    public void setAllReservations(List<Reservation> allReservations) { this.allReservations = allReservations; }
    
    public List<String> getAvailableRooms() { return availableRooms; }
    public List<String> getTimeSlots() { return timeSlots; }
}