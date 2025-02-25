package savelying;

import javax.swing.*;
import java.awt.*;

public class Item extends JComponent {

    public void paintComponent(Graphics g) {
        Font font = new Font("Bitstream Charter", Font.BOLD, 14);
        Image image = new ImageIcon("img's/WLive48x48.png").getImage();
        g.drawImage(new ImageIcon("img's/WLive48x48.png").getImage(), 0, 0, null);

        g.setFont(new Font("Bitstream Charter", Font.BOLD, 20));
        g.drawString("ФЦ 'СОЛНЕЧНЫЙ'", 600 / 2 - 90, 20);

        g.setFont(new Font("Bitstream Charter", Font.BOLD, 14));
        g.drawString("ДОБРО ПОЖАЛОВАТЬ!!!", 600 / 2 - 83, 40);
    }
}