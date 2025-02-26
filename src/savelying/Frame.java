package savelying;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    public final int defaultWidth;
    public final int defaultHeight;

    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();

    public Frame(int defaultWidth, int defaultHeight) throws HeadlessException {
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        setTitle("Сеть фитнес-клубов 'OZON'");
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds((screenSize.width / 2 - defaultWidth / 2), (screenSize.height / 2 - defaultHeight / 2), defaultWidth, defaultHeight);
    }
}