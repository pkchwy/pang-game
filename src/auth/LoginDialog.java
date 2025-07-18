package auth;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean loginSuccess = false;
    private String loggedInUser = null;

    public LoginDialog(Frame parent, UserManager userManager) {
        super(parent, "Login", true);
        setLayout(new GridLayout(3, 2, 10, 10));
        
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);
        
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);
        
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        add(loginBtn);
        add(registerBtn);
        
        loginBtn.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            if (userManager.login(user, pass)) {
                loginSuccess = true;
                loggedInUser = user;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong username or password!");
            }
        });
        
        registerBtn.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            if (userManager.register(user, pass)) {
                JOptionPane.showMessageDialog(this, "Registered! Now you can login.");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists!");
            }
        });
        
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }
} 