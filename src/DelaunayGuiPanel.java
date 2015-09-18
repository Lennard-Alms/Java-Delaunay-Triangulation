
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.*;



public class DelaunayGuiPanel extends JPanel {

	private Point2D p1, p2, query;
	private int size;
	private TriangleNode root, foundFace;
	private ArrayList<Point2D.Double> pointList;
    private BufferedImage baseImage, circle, searchPath;	
    private JTextArea output;
    private boolean dtCalculated, dtInProgress, searching;
    final static Charset ENCODING = StandardCharsets.UTF_8;
    private Color panelColor;
    
 public DelaunayGuiPanel (int newSize, JTextArea text){
	  size=newSize;
	  pointList = new ArrayList<Point2D.Double>();
	  dtCalculated = false;
	  dtInProgress = false;
	  foundFace = null;
	  searching = false;  
	  panelColor = new Color((float)229.0/255, (float)204.0/255, (float)255.0/255);
	  output = text;
	  
	  //(float)290.0, (float)27.0, (float)82.0
	    addMouseListener(new MouseAdapter() {
             public void mousePressed(MouseEvent e) {
            	int d=12;
            	Graphics g2d;
            	if(!dtInProgress && !searching){
	            	if (!dtCalculated){
		            	if(baseImage == null){
			            	baseImage = new BufferedImage(size, size,
				      	            BufferedImage.TYPE_INT_ARGB);
			             	g2d = baseImage.createGraphics();
			            	g2d.setColor(panelColor);
				      	    g2d.fillRect(0,0,size, size);
				      	    g2d.dispose();
		            	}
		            	
		            	g2d = baseImage.createGraphics();
		            	pointList.add(new Point2D.Double(e.getPoint().getX(), e.getPoint().getY()));
		            	g2d.setColor(Color.black);
		            	g2d.fillOval((int)e.getPoint().getX()-d/2, (int)e.getPoint().getY()-d/2, d, d);
		            	g2d.dispose();
		            	update();
		            	
	            	}
	            	else{
	            		int n = 3;
	            		if(foundFace!=null){
	            			paintFoundFace(panelColor, true);
	            		}
	            		query = e.getPoint();
	            		drawSearchPath();
	            		foundFace = root.findPoint(e.getPoint(), p1, p2, false);
	            		paintFoundFace(Color.green, false);
	            		g2d = baseImage.createGraphics();
	            		g2d.setColor(Color.blue);
	            		g2d.fillOval((int)e.getPoint().getX()-d/2, (int)e.getPoint().getY()-d/2, d, d);
	            		g2d.dispose();
	            		update();
	            	}
            	}
            }
        });
	    
	    }
 public void drawDelaunay(){
	// Contents of your existing handler go here, unchanged!
 	dtInProgress = true;
 	System.out.println("Running thread");
 	baseImage = new BufferedImage(size, size,
	            BufferedImage.TYPE_INT_ARGB);
 	Graphics2D g2d = baseImage.createGraphics();
    g2d.setColor(panelColor);
    g2d.fillRect(0,0,size, size);
    g2d.dispose();

    drawPoints();
    root = computeDelaunay();
    dtCalculated = true;
    update();
    dtInProgress = false;

 	circle = new BufferedImage(size, size,
	            BufferedImage.TYPE_INT_ARGB);
 }
 
public void clearDelaunay(){
	foundFace = null;
	baseImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	circle = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = baseImage.createGraphics();
    g2d.setColor(panelColor);
    g2d.fillRect(0,0,size, size);
    g2d.dispose();
    pointList.clear();
    dtCalculated = false;
    update();
}
 
 public void paintFace(TriangleFace f, Color c, BufferedImage img){
	 int n=3;
		// A simple triangle.
	 		//img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB); 	
	 		Graphics2D g2d = img.createGraphics();
		 	
		 	g2d.setColor(c);
			Double[] x = new Double[n]; 
			Double[] y = new Double[n];
			x[0]=f.getPoint(0).getX(); x[1]=f.getPoint(1).getX(); x[2]=f.getPoint(2).getX();
			y[0]=f.getPoint(0).getY(); y[1]=f.getPoint(1).getY(); y[2]=f.getPoint(2).getY();
			Path2D.Double path = new Path2D.Double();
			path.moveTo(x[0], y[0]);
	
			for(int i = 1; i < n; ++i) {
			   path.lineTo(x[i], y[i]);
			}
			path.closePath();
	
			g2d.fill(path);
			
	
			f.drawFace(this, Color.black, img);		 	
		 	update();
	 	}
		
 
 
 public void paintFoundFace(Color c, boolean resetEdges){
		TriangleFace f = foundFace.getTriangle();
		
		Point2D center = f.getEdge(0).circleThroughPoints(f.getPoint(0), f.getPoint(1), f.getPoint(2));
		Double diameter = 2*f.getEdge(0).radius(center, f.getPoint(1));
		f.drawFace(this, Color.black, baseImage);
		
		circle= new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = circle.createGraphics();
		
		g2d.setComposite(AlphaComposite.Clear);
		g2d.fillRect(0, 0, size, size);
		g2d.setComposite(AlphaComposite.Src);
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Color.blue);
		
		
	 	g2d.drawOval((int)Math.round(center.getX()-diameter/2), (int)Math.round(center.getY()-diameter/2), (int)Math.round(diameter), (int)Math.round(diameter));
		g2d.dispose();
	 	System.out.println(center.getX()+","+ center.getY());
	 	
	 	update();
	 	paintFace(f, c, baseImage);
	 	
	 	/*if(!resetEdges){
	 		drawPoints();
	 		if (!f.getEdge(0).isLegal(p1, p2, this)) f.getEdge(0).drawEdge(this, Color.red);
	 		f.getEdge(0).isLegal(p1, p2, this, Color.red, Color.orange);	
	 		if (!f.getEdge(1).isLegal(p1, p2, this)) f.getEdge(1).drawEdge(this, Color.red);
	 		f.getEdge(1).isLegal(p1, p2, this, Color.blue, Color.magenta);
	 		if (!f.getEdge(2).isLegal(p1, p2, this)) f.getEdge(2).drawEdge(this, Color.red);
	 		f.getEdge(2).isLegal(p1, p2, this, Color.yellow, Color.green);
	 		
	 	}
	 	else{
	 		for(int i=0; i < 3; ++i){
	 			f.getEdge(i).drawEdge(this, Color.black);
	 		}
	 	}*/
 }
 public void drawPoint(Point2D q, Color c, BufferedImage i, int width){
	Graphics2D g2d = i.createGraphics();
  	g2d.setColor(c);
  	g2d.fillOval((int)q.getX()-width/2, (int)q.getY()-width/2, width, width);
  	update();
 }
 public void drawPoints(){
	 int d=12;
	 for (Point2D.Double p : pointList){
		drawPoint(p, Color.black, baseImage, d);
	 }
 }
 
 public TriangleNode computeDelaunay(){
	 Point2D.Double lexHighestPoint = new Point2D.Double(0,0);
	 Point2D.Double xRMPoint = new Point2D.Double(0,0);
	 for (Point2D.Double p : pointList){
		  if(p.getY() > lexHighestPoint.getY()){
			  lexHighestPoint = p;
		  }
		  else if (p.getY()==lexHighestPoint.getY() && p.getX() >= lexHighestPoint.getX()){
			  lexHighestPoint = p;
		  }
		  if (p.getX() >= xRMPoint.getX()){
			  xRMPoint = p;
		  }
	 }
	 
	 Point2D.Double p0 = lexHighestPoint;
	 p1 = new Point2D.Double(600*xRMPoint.getX(), -p0.getY());
	 p2 = new Point2D.Double(-600*xRMPoint.getX(), 0);
	 TriangleFace f0 =new TriangleFace(p0, p2, p1);
	 TriangleNode root = new TriangleNode(f0, this);
	 update();
	 
	 for (Point2D.Double p : pointList){
		 root.findPoint(p, p1, p2, true);
		 update();
	 }
	 return root;
 }
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.drawImage(baseImage,0,0,size,size,null);
    
    if(searching){
    	g2d.drawImage(searchPath, 0, 0, size, size, null);
    }
    g2d.drawImage(circle,0,0,size,size,null);
  }
  
  public void walkFace(Graphics2D g, TriangleFace f){
	  int d = 8;
	  HalfEdge walk = f.getEdge(0);
	  Line2D line = new Line2D.Double();
	  if (walk!=null){
		  do {
			  line.setLine(walk.getpOne().getX(), walk.getpOne().getY(), walk.getpTwo().getX(), walk.getpTwo().getY());
			  g.draw(line);
			  g.fillOval((int)walk.getpOne().getX()-d/2, (int)walk.getpOne().getY()-d/2, d, d);
			  g.fillOval((int)walk.getpTwo().getX()-d/2, (int)walk.getpTwo().getY()-d/2, d, d);
			  walk = walk.getNext();
		   
		  } while (walk != f.getEdge(0));
	  }
  }
  
  public void writeToFile(File file){
	  PrintWriter writer;
	  try {
		writer = new PrintWriter(file, "UTF-8");
		for (Point2D.Double p : pointList){
			writer.println(p.getX() +","+p.getY());
		}
			
		writer.close();
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  public void readFile(File file){
	  clearDelaunay();  
	  Path path = Paths.get(file.getPath());
	    try (Scanner scanner =  new Scanner(path, ENCODING.name())){
	      while (scanner.hasNextLine()){
	        String[] pointString = scanner.nextLine().split(",");
	        Double x = Double.parseDouble(pointString[0]);
	        Double y = Double.parseDouble(pointString[1]);
	        pointList.add(new Point2D.Double(x, y));
	    	//process each line in some way
	        //log(scanner.nextLine());
	      }
	      drawPoints();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  
  
  public void recurseDrawGraph(TriangleNode currentNode, Graphics2D g, String tab){
	  tab=tab+" ";
	  if (currentNode.isEmpty()){ 
		  System.out.println("leaf    "+tab + currentNode.getTriangle());
		  walkFace(g, currentNode.getTriangle());
	  }
	  else{
		  System.out.println ("internal"+tab+currentNode.getTriangle());
		  for (TriangleNode temp : currentNode.getChildren()){
			  recurseDrawGraph(temp, g, tab);
		  }
	  }
  }
  public BufferedImage getImage(){
	  return baseImage;
  }
  public BufferedImage updateCircleImage(){
	  circle = new BufferedImage(size, size,
	            BufferedImage.TYPE_INT_ARGB);
	  return circle;
  }
  
  public void update(){
	  this.repaint();
  }
  
  public void drawSearchPath(){
	  searching = true;
	  searchPath = new BufferedImage(size, size,
	            BufferedImage.TYPE_INT_ARGB);
	  new Thread() {
          public void run() {
        	  paintFace(root.getTriangle(), Color.LIGHT_GRAY, searchPath);
        	  drawSearchPathRecurse(root);
        	  searching = false;
        	  searchPath = new BufferedImage(size, size,
        	            BufferedImage.TYPE_INT_ARGB);
        	  update();
          }
      }.start();
	 
	  update();
  }
  
  public TriangleNode drawSearchPathRecurse(TriangleNode currentNode){
	  try{
		 if (currentNode.isEmpty()){
			 paintFace(currentNode.getTriangle(), Color.GREEN, searchPath);
			 drawPoint(query, Color.blue, searchPath, 12);
			 Thread.sleep(500);
			 return currentNode;
		  }
		  else{
			  for (TriangleNode child : currentNode.getChildren()){
				  if (child.getTriangle().isInside(query)){
					 paintFace(child.getTriangle(), Color.LIGHT_GRAY, searchPath);
					 drawPoint(query, Color.blue, searchPath, 12);
					 Thread.sleep(250);
					 return drawSearchPathRecurse(child);
				  }
			  }
		  }
		
	  }catch(InterruptedException e){}
	  return null;
	}
  
  public Color getBackColor(){return panelColor;}

}
