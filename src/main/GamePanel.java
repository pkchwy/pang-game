package main;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import inputs.KeyboardInputs;

public class GamePanel extends JPanel {
	private Game game;

	public GamePanel(Game game) {
		this.game = game;
		setPanelSize();
		setFocusable(true);
		addKeyListener(new KeyboardInputs(this));
	}

	private void setPanelSize() {
		Dimension size = new Dimension(800, 600);
		setPreferredSize(size);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		game.render(g);
	}

	public Game getGame() {
		return game;
	}
}