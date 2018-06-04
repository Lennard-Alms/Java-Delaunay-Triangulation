import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

//import com.sun.java.util.jar.pack.Package.File;


public class DelaunayGuiMain implements ActionListener, ItemListener{

	private DelaunayGuiPanel panel;
    private JTextArea output;
    private JScrollPane scrollPane;
    private JButton clearButton, calculateButton;
    private String newline = "\n";
    private JPanel contentPane;
    
    final JFileChooser fc = new JFileChooser();
    
    public DelaunayGuiMain(int canvasSize){
    	contentPane = new JPanel(new BorderLayout());
    	createOutputPane();
    	panel = new DelaunayGuiPanel(canvasSize, output);
    }
    
    public JPanel createOptionsPanel(){
    	clearButton = new JButton("Clear");
		calculateButton = new JButton("Calculate Triangulation");
		GridLayout experimentLayout = new GridLayout(0,2);
	    JPanel options = new JPanel();
		options.setLayout(experimentLayout);
	    options.add(clearButton);
	    options.add(calculateButton);
	    
	    clearButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){  
	    		panel.clearDelaunay();
	    		output.append("Cleared PointSet" + newline);
                output.setCaretPosition(output.getDocument().getLength());
	    	}
	      });
	    
	    calculateButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){  
	        	new Thread() {
		            public void run() {
		            	panel.drawDelaunay();
		                output.append("Finished Computing" + newline);
		                output.setCaretPosition(output.getDocument().getLength());
		            }
		        }.start();
	    	}
	      });
	    
	    return options;
    }

    public DelaunayGuiPanel getPanel(){
    	return panel;
    }
    public JPanel getOutputPane(){
    	return contentPane;
    }
	public JMenuBar createMenuBar() {
    	JMenuBar menuBar;
        menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("File");
        menuItem = new JMenuItem("Open PointSet",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        		KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
        		"Open a predefined point set");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Save PointSet",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        		KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
        		"Save a predefined point set");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        menuBar.setVisible(true);

        
        menuBar.setVisible(true);
        return menuBar;
    }
	   private static void createAndShowGUI() {
	        //Create and set up the window.
	        JFrame frame = new JFrame("Delaunay Triangulation");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        int size = 1000;
	        frame.setSize(size, size);
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	        frame.setResizable(false);
	        //Create and set up the content pane.
	        DelaunayGuiMain triangulation = new DelaunayGuiMain(size);
	        frame.add(triangulation.getPanel());
	        frame.setJMenuBar(triangulation.createMenuBar());
	        frame.add(triangulation.getOutputPane(), BorderLayout.NORTH);
	        frame.add(triangulation.createOptionsPanel(), BorderLayout.SOUTH);
	    }
	
	    public JPanel createOutputPane() {
	        //Create the content-pane-to-be.
	        contentPane.setOpaque(true);
	 
	        //Create a scrolled text area.
	        output = new JTextArea(5, 30);
	        output.setFont(output.getFont().deriveFont(16f)); // will only change size to 12pt
	        output.setEditable(false);
	        scrollPane = new JScrollPane(output);
	 
	        //Add the text area to the content pane.
	        contentPane.add(scrollPane, BorderLayout.CENTER);
	        contentPane.setSize(new Dimension(0, 200));
	        contentPane.setVisible(true);
	    	output.append("Click anywhere on the canvas to create points OR open a saved pointset and then click 'Calculate'!" + newline);
	        output.setCaretPosition(output.getDocument().getLength());
	        return contentPane;
	    }
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Item event detected."
                   + newline
                   + "    Event source: " + source.getText()
                  // + " (an instance of " + getClassName(source) + ")"
                   + newline
                   + "    New state: "
                   + ((e.getStateChange() == ItemEvent.SELECTED) ?
                     "selected":"unselected");
        output.append(s + newline);
        output.setCaretPosition(output.getDocument().getLength());
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Action event detected."
                   + newline
                   + "    Event source: " + source.getText();
                   //+ " (an instance of " + getClassName(source) + ")";
        
        if (source.getText().equals("Open PointSet")) {
            int returnVal = fc.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                s+=("- Opening: " + file.getName() + "." + newline);
                panel.readFile(file);
            } else {
                s+=("- Open command cancelled by user." + newline);
            }
        }
       
        else if (source.getText().equals("Save PointSet")){
            int returnVal = fc.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                s+=("- Saving: " + file.getName() + "." + newline);
                panel.writeToFile(file);
            } else {
                s+=("- Saving command cancelled by user." + newline);
            }
        }
        
        output.append(s + newline);
        output.setCaretPosition(output.getDocument().getLength());
	}
	
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}