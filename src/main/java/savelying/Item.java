package savelying;

import javax.swing.*;
import java.awt.*;

public class Item extends JComponent {

    public void paintComponent(Graphics g) {
        g.drawImage(new ImageIcon("src/main/resources/WLive48x48.png").getImage(), 0, 0, null);

        g.setFont(new Font("Bitstream Charter", Font.BOLD, 20));
        g.drawString("ФЦ 'СОЛНЕЧНЫЙ'", Frame.defaultWidth/ 2 - 90, 20);

        g.setFont(new Font("Bitstream Charter", Font.BOLD, 14));
        g.drawString("ДОБРО ПОЖАЛОВАТЬ!!!", Frame.defaultWidth / 2 - 80, 40);
    }
}