package UIPackage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * CheckModule
 * Created by ccwei on 2018/9/23.
 */
public class TreeUpdateActionListener implements ActionListener {

    JFrame frame;

    public TreeUpdateActionListener(JFrame frame) {
        this.frame = frame;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent e) {

    }
}
