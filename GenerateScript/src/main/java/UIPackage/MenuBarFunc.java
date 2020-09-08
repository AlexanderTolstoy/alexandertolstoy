package UIPackage;

import DataStructure.Diagram;
import DataStructure.Entity;
import DataStructure.EntitySymbol;
import DataStructure.ResourcePool;
import function.*;
import info.clearthought.layout.TableLayout;
import org.dom4j.DocumentException;
import utils.PoiTools;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

/**
 * CheckModule
 * Created by ccwei on 2018/9/22.
 */
public class MenuBarFunc {

    private final static int COUNT = 16;

    public static JMenuBar getMenuBar(final JFrame mainFrame,final DefaultMutableTreeNode root,JScrollPane displayPane) {
        JMenuBar jMenuBar = new JMenuBar();

        final JMenu fileMenu = new JMenu("文件");
        jMenuBar.add(fileMenu);
        JMenuItem loadFileItem = setLoadFileItem(root);
        fileMenu.add(loadFileItem);

        JMenu jMenu = new JMenu("功能");

        jMenuBar.add(jMenu);
        JMenuItem jCheck = new JMenuItem("校验名字");
        jMenu.add(jCheck);

        JMenuItem jMapping = getMappingItem(displayPane);
        jMenu.add(jMapping);

        JMenuItem jETLScript = getETLScriptItem(displayPane);
        jMenu.add(jETLScript);

        JMenuItem jDVScript = getJDVScriptItem(displayPane);
        jMenu.add(jDVScript);

        JMenuItem jDMCreateScripts = getJDMCreateScriptsItem(displayPane);
        jMenu.add(jDMCreateScripts);

        JMenuItem jCheckCDMItem = getCheckCDMItem(displayPane);
        jMenu.add(jCheckCDMItem);

        JMenuItem jFreeETLItem = getFreeETLItem(displayPane);
        jMenu.add(jFreeETLItem);

        JMenuItem jDVCreationDirect = getDVCreationDirect(displayPane);
        jMenu.add(jDVCreationDirect);


        return jMenuBar;
    }

    /**
     * create by: ccwei
     * create time: 23:38 2018/11/18
     * description: 增加生成Free表ETL脚本功能
     * @return
     */
    private static JMenuItem getFreeETLItem(final JScrollPane displayPane) {
        JMenuItem jDVScriptItem= new JMenuItem("生成Free表ETL脚本");
        jDVScriptItem.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
                File file = CommonComponent.scanFile();
                GenerateOdsDvFreeETLSql.loadXLSXFile(file.getAbsolutePath(),file.getName());
                CommonComponent.popWindow("输出到：" + file.getAbsolutePath());
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        return jDVScriptItem;
    }

    /**
     * create by: ccwei
     * create time: 23:38 2018/11/18
     * description: 增加检查CDM与字段映射功能
     * @return
     */
    private static JMenuItem getCheckCDMItem(final JScrollPane displayPane) {
        JMenuItem jDVScriptItem= new JMenuItem("检查CDM与字段映射");
        jDVScriptItem.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
                File file = CommonComponent.scanFile();
                CheckCDMandMapping.loadXLSXFile(file.getAbsolutePath(),file.getName());
                CommonComponent.popWindow("输出到：" + file.getAbsolutePath());
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        return jDVScriptItem;
    }

    /**
     * create by: ccwei
     * create time: 21:06 2018/11/13
     * description: 生成DM建表脚本功能菜单
     * @return
     */
    private static JMenuItem getJDMCreateScriptsItem(final JScrollPane displayPane) {
        JMenuItem jDVScriptItem= new JMenuItem("生成DM建表脚本");
        jDVScriptItem.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
                File file = CommonComponent.scanFile();
                GenerateDMCreateSQL.loadXLSXFile(file.getAbsolutePath(),file.getName());
                CommonComponent.popWindow("输出到：" + file.getAbsolutePath());
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        return jDVScriptItem;
    }
    /**
     * create by: ccwei
     * create time: 21:06 2018/11/13
     * description: 生成DV建表脚本功能菜单
     * @return
     */
    private static JMenuItem getJDVScriptItem(final JScrollPane displayPane) {
        JMenuItem jDVScriptItem= new JMenuItem("生成DV-free建表脚本");
        jDVScriptItem.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
                File file = CommonComponent.scanFile();
                GenerateDVFreeCreateSQL.loadXLSXFile(file.getAbsolutePath(),file.getName());
                CommonComponent.popWindow("输出到：" + file.getAbsolutePath());
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        return jDVScriptItem;
    }

    /**
     * create by: ccwei
     * create time: 21:07 2018/11/13
     * description: 生成ETL脚本功能菜单
     * @return
     */
    private static JMenuItem getETLScriptItem(final JScrollPane displayPane) {
        JMenuItem jEtlItem = new JMenuItem("生成ETL脚本");
        jEtlItem.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
                File file = CommonComponent.scanFile();
                GenerateETLSQL.loadXLSXFile(file.getAbsolutePath(),file.getName());
                CommonComponent.popWindow("输出到：" + file.getAbsolutePath());

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        return jEtlItem;
    }

    /**
     * create by: ccwei
     * create time: 21:00 2018/11/13
     * description: 从PowerDesigner的cdm到excel的字段映射
     * @return
     */
    private static JMenuItem getMappingItem(final JScrollPane displayPane) {
        JMenuItem jMapping = new JMenuItem("生成字段映射");
        jMapping.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
                initLeftPanel(displayPane);
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        return jMapping;
    }

    private static void initLeftPanel(JScrollPane displayPane) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[] rowSize = {f};
        double[] columnSize = {f};

        Box vBoxOut = Box.createVerticalBox();

        JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new TableLayout(rowSize,columnSize));
        final Box vBox = Box.createVerticalBox();

        for(String key :ResourcePool.getDiagram().keySet() ) {
            if(!ResourcePool.getDiagramLinks().containsKey(key)){
                continue;
            }
            Diagram diagram = ResourcePool.getDiagram().get(key);
            JCheckBox jCheckBox = new JCheckBox(diagram.getName());
            vBox.add(jCheckBox);
        }
        checkPanel.add(vBox, "0, 0,LEFT, TOP");


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new TableLayout(rowSize,columnSize));
        JButton button = new JButton("Convert");
        button.addMouseListener(new MouseInputListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
                HashSet<String> tmp = new HashSet<String>();
                int selected = 0 ;
                for(int i =0 ; i < vBox.getComponentCount(); i++){
                    JCheckBox box = (JCheckBox) vBox.getComponent(i);
                    if(box.isSelected()){
                        selected++;
                        tmp.add(box.getText());
                    }
                }
                HashSet<String> res = new HashSet<String>();
                for(String key :ResourcePool.getDiagram().keySet() ) {
                    Diagram diagram = ResourcePool.getDiagram().get(key);
                    if(!tmp.contains(diagram.getName())){
                        continue;
                    }
                    res.add(diagram.getId());
                }
                PoiTools.writeBatchSheet2Excel(res,COUNT);

                CommonComponent.popWindow("convert completed!");
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }

            public void mouseDragged(MouseEvent e) {

            }

            public void mouseMoved(MouseEvent e) {

            }
        });
        buttonPanel.add(button,"0, 0, RIGHT, BOTTOM");

        vBoxOut.add(checkPanel);
        vBoxOut.add(buttonPanel);

        displayPane.setViewportView(vBoxOut);



    }

    /**
     * create by: ccwei
     * create time: 23:38 2019/02/22
     * description: 直接生成DV建表脚本
     * @return
     */
    private static JMenuItem getDVCreationDirect(final JScrollPane displayPane) {
        JMenuItem jDVScriptItem= new JMenuItem(" 直接生成DV建表脚本");
        jDVScriptItem.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
                File file = CommonComponent.scanFile();
                DVTableCreateDirect.loadXLSXFile(file.getAbsolutePath(),file.getName());
                CommonComponent.popWindow("输出到：" + file.getAbsolutePath());
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        return jDVScriptItem;
    }

    private static JMenuItem setLoadFileItem(final DefaultMutableTreeNode root) {
        JMenuItem jMenuItem = new JMenuItem("加载文件");

        jMenuItem.addMouseListener(new MouseInputListener(){

            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}

            public void mouseReleased(MouseEvent e) {
                File file = scanFile();
                //save file name
                ResourcePool.setFileName(file.getName());
                ResourcePool.setFilePath(file.getAbsolutePath());

                if(!ResourcePool.getDiagram().isEmpty()) {
                    HashMap<String, Diagram> diagramHashMap = ResourcePool.getDiagram();
                    DefaultMutableTreeNode subRoot = new DefaultMutableTreeNode(ResourcePool.getModel().getName());
                    root.add(subRoot);
                    for(String key : diagramHashMap.keySet()){
                        Diagram diagram = diagramHashMap.get(key);
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(diagram.getName());
                        subRoot.add(node);
                        for(String key1 : diagram.getEntitySymbolHashMap().keySet()){
                            EntitySymbol entitySymbol = diagram.getEntitySymbol(key1);
                            Entity real = ResourcePool.getEntitiy(entitySymbol.getRef());
                            DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(real.getName());
                            node1.setUserObject(real.getId());
                            node.add(node1);
                        }
                    }
                }
            }

            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseDragged(MouseEvent e) {}
            public void mouseMoved(MouseEvent e) {}});
        return jMenuItem;
    }

    private static File scanFile() {
        File file = CommonComponent.scanFile();
        try {
            LoadXmlFile.load(file.getAbsolutePath());
        } catch (DocumentException e1) {
            CommonComponent.popWindow("load file error！");
        }
        CommonComponent.popWindow("load file success！");
        return file;
    }

}
