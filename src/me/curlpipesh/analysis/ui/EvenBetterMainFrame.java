package me.curlpipesh.analysis.ui;

import me.curlpipesh.analysis.Main;
import me.curlpipesh.analysis.descs.ClassDesc;
import me.curlpipesh.analysis.impl.BetterClassAnalyser;
import me.curlpipesh.analysis.util.DescHelper;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("FieldCanBeLocal")
public class EvenBetterMainFrame extends JFrame {
    private JButton jButton1;
    private JButton jButton2;
    private JScrollPane jScrollPane4;
    private JScrollPane jScrollPane6;
    private JSplitPane jSplitPane3;
    private JTabbedPane jTabbedPane1;
    private JTextArea jTextArea2;
    private JToolBar jToolBar1;
    private JTree jTree1;
    private SearchFrame searchFrame;

    public EvenBetterMainFrame() {
        super("JAR Analyser");
        initialise();
        setLocationRelativeTo(null);
    }

    public void initialise() {
        initComponents();
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(EvenBetterMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    FileUtils.deleteDirectory(Main.getExtractor().getExtractionDirectory());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        searchFrame = new SearchFrame();
        jToolBar1 = new JToolBar();
        jButton1 = new JButton();
        jButton2 = new JButton();
        jSplitPane3 = new JSplitPane();
        jTabbedPane1 = new JTabbedPane();
        jScrollPane6 = new JScrollPane();
        jTextArea2 = new JTextArea();
        jScrollPane4 = new JScrollPane();
        jTree1 = new JTree();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jScrollPane4.setViewportView(jTree1);
        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        jTree1.setModel(new FileSystemModel());
        jTree1.setCellRenderer((jTree, o, b, b1, b2, i, b3) -> new JLabel(((File)o).getName()));
        jTree1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                File filee = null;
                try {
                    filee = (File) jTree1.getPathForRow(jTree1.getRowForLocation(mouseEvent.getX(), mouseEvent.getY())).getLastPathComponent();
                } catch(NullPointerException ignored) {
                }
                File file = filee;
                if(file != null) {
                    if (file.getName().endsWith(".class")) {
                        Optional<Map.Entry<JarEntry, BetterClassAnalyser>> n = Main.getAnalysers().entrySet().parallelStream()
                                .filter(p -> {
                                    String[] q = p.getKey().getName().split(File.separator);
                                    return q[q.length - 1].equalsIgnoreCase(file.getName());
                                }).findFirst();
                        if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
                            if (n.isPresent()) {
                                boolean tabExists = false;
                                for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
                                    String title = jTabbedPane1.getTitleAt(i);
                                    if (title.equalsIgnoreCase(file.getName())) {
                                        tabExists = true;
                                        break;
                                    }
                                }
                                if (!tabExists) {
                                    JTextPane pane = new JTextPane();
                                    pane.setEditable(false);
                                    pane.setFont(Font.getFont("Monospaced"));
                                    pane.setText(n.get().getValue().toString());

                                    JScrollPane pane2 = new JScrollPane();
                                    pane2.setViewportView(pane);

                                    // Java keyword highlighting
                                    JavaSyntaxHighlighterHelper.applyRegex(JavaSyntaxHighlighterHelper.KEYWORD_REGEX, pane, Color.BLUE);
                                    // Comment highlighting
                                    JavaSyntaxHighlighterHelper.applyRegex("\\/\\/(.*)", pane, new Color(0x80, 0x80, 0x80));
                                    // Multiline comment highlighting
                                    JavaSyntaxHighlighterHelper.applyRegex("(?s)/\\*.*?\\*/", pane, new Color(0x80, 0x80, 0x80));
                                    // Naive String highlighting
                                    JavaSyntaxHighlighterHelper.applyRegex("\"(.*)\"", pane, new Color(0x0, 0x80, 0x0));
                                    // @interface highlighting
                                    JavaSyntaxHighlighterHelper.applyRegex("@interface", pane, new Color(0x83, 0xBA, 0xFF));

                                    jTabbedPane1.addTab(file.getName(), pane2);
                                    for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
                                        String title = jTabbedPane1.getTitleAt(i);
                                        if (title.equalsIgnoreCase(file.getName())) {
                                            jTabbedPane1.setTabComponentAt(i, getClosePanel(title, jTabbedPane1));
                                            jTabbedPane1.setSelectedIndex(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if(mouseEvent.getButton() == MouseEvent.BUTTON3) {
                            if(n.isPresent()) {
                                TreeContextMenu menu = new TreeContextMenu(n);
                                menu.show(jTree1, mouseEvent.getX(), mouseEvent.getY());
                            }
                        }
                    }
                }
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButton1.setText("Search");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(SwingConstants.BOTTOM);

        jButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    if(!searchFrame.isVisible()) {
                        searchFrame.setVisible(true);
                    }
                }
            }
        });

        jToolBar1.add(jButton1);

        jButton2.setText("Dump to stdout");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(SwingConstants.BOTTOM);

        jButton2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    File text = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath() + File.separator + "dump.txt");
                    if(text.exists()) {
                        if(!text.delete()) {
                            throw new RuntimeException("Unable to delete dump file!");
                        }
                    }
                    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(text, false)))) {
                        out.println("");
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    for(Map.Entry<JarEntry, BetterClassAnalyser> entry : Main.getAnalysers().entrySet()) {
                        BetterClassAnalyser analyser = entry.getValue();
                        StringBuilder sb = new StringBuilder();

                        // Class
                        ClassDesc cd = analyser.getClassDesc();
                        sb.append("Dump of: ").append(cd.getClassName()).append("\nExtending: ")
                                .append(cd.getSuperClassName()).append("\n");
                        if(cd.getInterfaceNames().length > 0) {
                            sb.append("Implementing:\n");
                            for(String iface : cd.getInterfaceNames()) {
                                sb.append(" * ").append(iface).append("\n");
                            }
                        }
                        sb.append("Access level: ").append(DescHelper.classAccessLevelToString(cd.getAccessLevel()))
                        .append("\n");
                        // Fields
                        if(analyser.getFields().size() > 0) {
                            sb.append(analyser.getFieldAnalysis()).append("\n");
                        }
                        // Methods
                        if(analyser.getMethods().size() > 0) {
                            sb.append(analyser.getMethodAnalysis()).append("\n");
                        }
                        sb.append("================================================================================\n");
                        //System.out.println(sb.toString());
                        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(text, true)))) {
                            out.println(sb.toString());
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("done");
                    }
                }
            }
        });

        jToolBar1.add(jButton2);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane6.setViewportView(jTextArea2);

        jSplitPane3.setRightComponent(jTabbedPane1);

        jScrollPane4.setViewportView(jTree1);

        jSplitPane3.setLeftComponent(jScrollPane4);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                        .addComponent(jSplitPane3)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSplitPane3, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
        );

        setBounds(0, 0, 610, 430);
    }

    /**
     * http://stackoverflow.com/questions/11553112/how-to-add-close-button-to-a-jtabbedpane-tab
     * @param title Title of the given tab
     * @return JTabbedPane to replace things and stuff in
     */
    private Component getClosePanel(String title, JTabbedPane pane) {
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        JButton btnClose = new JButton("x");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);
        btnClose.addActionListener(new TabCloseActionHandler(title, pane));

        return pnlTab;
    }

    private class TabCloseActionHandler implements ActionListener {
        private String tabName;
        private JTabbedPane pane;

        public TabCloseActionHandler(String tabName, JTabbedPane pane) {
            this.tabName = tabName;
            this.pane = pane;
        }

        public String getTabName() {
            return tabName;
        }

        public void actionPerformed(ActionEvent evt) {
            int index = pane.indexOfTab(getTabName());
            if (index >= 0) {
                pane.removeTabAt(index);
            }
        }
    }
}
