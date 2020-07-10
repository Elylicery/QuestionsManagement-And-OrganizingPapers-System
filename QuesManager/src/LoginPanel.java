import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
public class LoginPanel extends JPanel {
	protected ImageIcon icon;
	public int width,height;
	public LoginPanel() {
		super();
		icon = new ImageIcon(LoginPanel.this.getClass().getResource("image/login.jpg"));
		width = icon.getIconWidth();
		height = icon.getIconHeight();
		setSize(width, height);
	}
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image img = icon.getImage();
		g.drawImage(img, 0, 0,getParent());
	}

//	//test
//	public static void main(String[] args) {
//		LoginPanel loginPanel  = new LoginPanel();
//	}
}