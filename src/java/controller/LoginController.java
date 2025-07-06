package controller;

import model.User;
import session.AdminBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginController implements Serializable {
    
    @Inject
    private AdminBean adminBean;
    
    private String username;
    private String password;
    private String email;
    private User currentUser;
    
    // Login functionality
    public String login() {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            addErrorMessage("Please enter username and password");
            return null;
        }
        
        currentUser = adminBean.authenticateUser(username.trim(), password);
        
        if (currentUser != null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentUser", currentUser);
            
            if (currentUser.isAdmin()) {
                return "adminDashboard.xhtml?faces-redirect=true";
            } else {
                return "reserveRoom.xhtml?faces-redirect=true";
            }
        } else {
            addErrorMessage("Invalid username or password");
            return null;
        }
    }
    
    // Registration functionality
    public String register() {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            addErrorMessage("Please fill in all fields");
            return null;
        }
        
        if (!isValidEmail(email)) {
            addErrorMessage("Please enter a valid email address");
            return null;
        }
        
        boolean success = adminBean.registerUser(username.trim(), password, email.trim());
        
        if (success) {
            addInfoMessage("Registration successful! Please login.");
            clearForm();
            return "index.xhtml?faces-redirect=true";
        } else {
            addErrorMessage("Username already exists. Please choose a different one.");
            return null;
        }
    }
    
    // Logout functionality
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml?faces-redirect=true";
    }
    
    // Navigation
    public String goToRegister() {
        clearForm();
        return "register.xhtml?faces-redirect=true";
    }
    
    public String goToLogin() {
        clearForm();
        return "index.xhtml?faces-redirect=true";
    }
    
    // Utility methods
    private void clearForm() {
        username = "";
        password = "";
        email = "";
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
    
    private void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }
    
    // Check if user is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    // Check if current user is admin
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }
}