package main;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;

public class GameWindow {
	private JFrame jframe;

	public GameWindow(GamePanel gamePanel) {
		jframe = new JFrame("Pang");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.add(gamePanel);
		jframe.setResizable(false);
		
		try {
			Image icon = ImageIO.read(new File("res/icon.png"));
			jframe.setIconImage(icon);
		} catch (Exception e) {
			System.err.println("Error loading window icon: " + e.getMessage());
		}
		
		jframe.pack();
		jframe.setLocationRelativeTo(null);
		jframe.setVisible(true);
	}

	public void setJMenuBar(JMenuBar menuBar) {
		jframe.setJMenuBar(menuBar);
		menuBar.setVisible(true);
		jframe.validate();
		jframe.repaint();
	}
}
