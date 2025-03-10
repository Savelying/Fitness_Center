package savelying;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    static int defaultWidth;
    static int defaultHeight;

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();

    public Frame(int width, int height) {
        defaultWidth = width;
        defaultHeight = height;
        setTitle("Сеть фитнес-клубов 'OZON'");
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
    }
}