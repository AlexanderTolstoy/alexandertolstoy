package main;

import UIPackage.MenuBarFunc;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import utils.ConfigCenter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * CheckModule
 * Created by ccwei on 2018/9/22.
 */
public class GUImain {

    private static JFrame mainFrame;
    public static void main(String[] args){

        try
        {
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
            UIManager.put("RootPane.setupButtonVisible", false);
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        }
        catch(Exception e)
        {
            //TODO exception
        }

        //初始化
        init();

        mainFrame = new JFrame("PowerDesigner Tools V1.0");
        mainFrame.setSize(900, 720);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JSplitPane jSplitPane = new JSplitPane();
        mainFrame.getContentPane().add(jSplitPane);
        jSplitPane.setDividerSize(1);
        jSplitPane.setEnabled(true);
        jSplitPane.setContinuousLayout(true);
        jSplitPane.setDividerLocation(240);
        jSplitPane.setOneTouchExpandable(true);

        //left
        JScrollPane jScrollPane = new JScrollPane();
        jSplitPane.setLeftComponent(jScrollPane);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("workspace");
        JTree jTree = new JTree(top);
        jScrollPane.setViewportView(jTree);

        //right
        JScrollPane displayPane = new JScrollPane();
        jSplitPane.setRightComponent(displayPane);

        //menu
        JMenuBar jMenuBar = MenuBarFunc.getMenuBar(mainFrame,top,displayPane);
        mainFrame.setJMenuBar(jMenuBar);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

    }

    private static void init(){
        ConfigCenter.initXmlConfig();
        String rootPath = System.getProperty("user.dir");
        System.setProperty("log.base",rootPath);
    }

}
