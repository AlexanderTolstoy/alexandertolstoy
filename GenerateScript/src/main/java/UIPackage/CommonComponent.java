package UIPackage;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * CheckModule
 * Created by ccwei on 2018/9/22.
 */
public class CommonComponent extends JDialog {

    public static void popWindow(String msg) {
        JDialog dialog = new JDialog();
        dialog.setSize(360,270);
        JLabel label = new JLabel(msg);
        label.setHorizontalAlignment(0);
        label.setPreferredSize(new Dimension(240, 160));
        dialog.add(label);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static File scanFile(){
        JFileChooser jfc=new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
        jfc.showDialog(new JLabel(), "选择");
        File file=jfc.getSelectedFile();
        if(null == file || file.isDirectory()){
            popWindow("Not a File!");
            return null;
        }
        return file;
    }
}
