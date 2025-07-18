package auth;

import java.io.*;
import java.util.*;

public class UserManager {
    private static final String FILE = "users.txt";
    private List<User> users = new ArrayList<>();

    public UserManager() {
        loadUsers();
    }

    public boolean login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public boolean register(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return false;
        }
        users.add(new User(username, password, 0));
        saveUsers();
        return true;
    }

    public void updateScore(String username, int newScore) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                if (newScore > u.getScore()) {
                    u.setScore(newScore);
                    saveUsers();
                }
                break;
            }
        }
    }

    public int getScore(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return u.getScore();
            }
        }
        return 0;
    }

    public int getHighestScore() {
        if (users.isEmpty()) return 0;
        return Collections.max(users).getScore();
    }

    public String getTopPlayerName() {
        if (users.isEmpty()) return "NO PLAYER";
        return Collections.max(users).getUsername();
    }

    public List<User> getTop5Players() {
        List<User> sortedUsers = new ArrayList<>(users);
        Collections.sort(sortedUsers);
        List<User> topPlayers = new ArrayList<>();
        int count = 0;
        for (User user : sortedUsers) {
            if (count >= 5) break;
            topPlayers.add(user);
            count++;
        }
        Collections.reverse(topPlayers);
        return topPlayers;
    }

    private void loadUsers() {
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    int score = Integer.parseInt(parts[2]);
                    users.add(new User(username, password, score));
                }
            }
        } catch (Exception e) {
 
        }
    }

    private void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (User u : users) {
                pw.println(u.getUsername() + ":" + u.getPassword() + ":" + u.getScore());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 