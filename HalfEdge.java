import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;


public class HalfEdge {
private HalfEdge prev;
private HalfEdge next;
private HalfEdge twin;
private Point2D pOne;
private Point2D pTwo;
private TriangleNode face;


	public HalfEdge (Point2D newPone, Point2D newPtwo){
		pOne = newPone;
		pTwo = newPtwo;
		prev = null;
		next = null;
		twin = null;
	}
	public HalfEdge getPrev() {
		return prev;
	}
	private void copy(HalfEdge c, HalfEdge e){
		//if (c!)
		c.setpOne(e.getpOne());
		c.setpTwo(e.getpTwo());
		c.setPrev(e.getPrev());
		c.setNext(e.getNext());
		c.setFace(e.getFace());
		c.setTwin(e.getTwin());
	}
	public HalfEdge copy(){
		HalfEdge copy = new HalfEdge(pOne, pTwo);
		return copy;
	}
	public void setPrev(HalfEdge prev) {
		this.prev = prev;
	}
	public HalfEdge getNext() {
		return next;
	}
	public void setNext(HalfEdge next) {
		this.next = next;
	}
	public HalfEdge getTwin() {
		return twin;
	}
	public void setTwin(HalfEdge twin) {
		this.twin = twin;
	}
	public Point2D getpOne() {
		return pOne;
	}
	public void setpOne(Point2D pOne) {
		this.pOne = pOne;
	}
	public Point2D getpTwo() {
		return pTwo;
	}
	public void setpTwo(Point2D pTwo) {
		this.pTwo = pTwo;
	}
	public String toString(){
		return ("H.E. ("+pOne.getX()+","+pOne.getY()+")->("+pTwo.getX()+","+pTwo.getY()+")");
	}
	
	public TriangleNode getFace() {
		return face;
	}
	public void setFace(TriangleNode face) {
		this.face = face;
	}
	public Point2D circleThroughPoints(Point2D p1, Point2D p2, Point2D p3)
	{		
		final double offset = Math.pow(p2.getX(),2) + Math.pow(p2.getY(),2);
	    final double bc =   ( Math.pow(p1.getX(),2) + Math.pow(p1.getY(),2) - offset )/2.0;
	    final double cd =   (offset - Math.pow(p3.getX(), 2) - Math.pow(p3.getY(), 2))/2.0;
	    final double det =  (p1.getX() - p2.getX()) * (p2.getY() - p3.getY()) - (p2.getX() - p3.getX())* (p1.getY() - p2.getY()); 
	    final double TOL = 0.0000001;

	    if (Math.abs(det) < TOL) { return null; }

	    final double idet = 1/det;

	    final double center_x =  (bc * (p2.getY() - p3.getY()) - cd * (p1.getY() - p2.getY())) * idet;
	    final double center_y =  (cd * (p1.getX() - p2.getX()) - bc * (p2.getX() - p3.getX())) * idet;
	    return new Point2D.Double(center_x, center_y);
	}
	
	private boolean inCircle(Point2D center, double radius, Point2D q){
		double square_dist = Math.pow(center.getX()-q.getX(), 2) + Math.pow(center.getY()-q.getY(), 2);
		double square_rad = Math.pow(radius, 2);
		return (square_dist <= square_rad);
	}
    public double radius(Point2D center, Point2D q){
        double radius;
        double x=Math.pow((center.getX()-q.getX()), 2);
        double y=Math.pow((center.getY()-q.getY()), 2);
        radius = Math.sqrt(x+y);
        return radius;
    }
	private boolean collinear(Point2D p1, Point2D p2, Point2D p3) {
		
		double result = (p1.getX() * (p2.getY()-p3.getY()) 
				+p2.getX()*(p3.getY()-p1.getY())
				+p3.getX()*(p1.getY()-p2.getY()))/2;
		System.out.println ("Collinear delta halfEdge: (" + Math.abs(result) +") "+p3.getX()+","+p3.getY());
		return Math.abs(result)/1000 <= 0.1;
		}
	 public void drawPoint(Point2D q, Color c, DelaunayGuiPanel panel){
		 	int width = 12;
			Graphics2D g2d = panel.getImage().createGraphics();
		  	g2d.setColor(c);
		  	g2d.fillOval((int)q.getX()-width/2, (int)q.getY()-width/2, width, width);
		  	panel.update();
	}
	public boolean isLegal(Point2D p1, Point2D p2, DelaunayGuiPanel panel){
		return isLegal(p1, p2, panel, null, null);
	}
	public boolean isLegal(Point2D p1, Point2D p2, DelaunayGuiPanel panel, Color kColor, Color rColor){
		try {
			if (twin == null) {Thread.sleep(300); return true;}
			int iRank = 0;
			int jRank = 0;
			int kRank = 0;
			
			Point2D k = twin.getNext().getpTwo();
			if (kColor != null){
				drawPoint(k, kColor, panel);
			}
			Point2D r = next.getpTwo();
			if (rColor!= null){
				drawPoint(r, rColor, panel);
			}
			
			//if(k.getX()==p1.getX() && k.getY()==p1.getY()){ Thread.sleep(300); return true;}
			//if(k.getX()==p2.getX() && k.getY()==p2.getY()){ Thread.sleep(300); return true;}
			if(k.getX()==p1.getX() && k.getY()==p1.getY()){ kRank = -1;}
			if(k.getX()==p2.getX() && k.getY()==p2.getY()){ kRank=-2;}

			
			if (pOne.getX()==p1.getX() && pOne.getY()==p1.getY()) {iRank = -1;}
			if (pOne.getX()==p2.getX() && pOne.getY()==p2.getY()) {iRank = -2;}
			if (pTwo.getX()==p1.getX() && pTwo.getY()==p1.getY()) {jRank = -1;}
			if (pTwo.getX()==p2.getX() && pTwo.getY()==p2.getY()) {jRank = -2;}
			//if (iRank < 0 && collinear(r, k, pTwo)) return true;
			//if (jRank < 0 && collinear(r, k, pOne)) return true;
			if ((iRank < 0) || (jRank < 0 || (kRank < 0))) {
				if (Math.min(iRank, jRank) >= kRank){ 
					Thread.sleep(300);
					return true;
				}
			}
			
			r = next.getpTwo();
			Point2D center = circleThroughPoints(pOne, pTwo, r);
			
			if (center==null) return true;
			double radius = radius(center, r);
			boolean inside = inCircle(center, radius, k);
			if(inside) {
				drawCircle(panel, Color.red, center, 2*radius);
			}
			else{
				drawCircle(panel, Color.green, center, 2*radius);
			}
			Thread.sleep(300);
			return !(inside);
			
			
			/**/
			//Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public void drawEdge(DelaunayGuiPanel panel, Color c){
		Graphics2D g2d = panel.getImage().createGraphics();
		Line2D line = new Line2D.Double();
		int d=12;
		g2d.setColor(c);
		line.setLine(getpOne().getX(), getpOne().getY(), getpTwo().getX(), getpTwo().getY());
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(line);
		g2d.setColor(Color.black);
		g2d.fillOval((int)getpOne().getX()-d/2, (int)getpOne().getY()-d/2, d, d);
		g2d.fillOval((int)getpTwo().getX()-d/2, (int)getpTwo().getY()-d/2, d, d);
		panel.update();
	}
	
	public void drawCircle(DelaunayGuiPanel panel, Color c, Point2D center, double diameter){
		Graphics2D g2d = panel.updateCircleImage().createGraphics();
		g2d.setComposite(AlphaComposite.Clear);
		g2d.fillRect(0, 0, 700, 700);
		g2d.setComposite(AlphaComposite.Src);
		g2d.setColor(c);
		g2d.setStroke(new BasicStroke(3));
	 	g2d.drawOval((int)Math.round(center.getX()-diameter/2), (int)Math.round(center.getY()-diameter/2), (int)Math.round(diameter), (int)Math.round(diameter));
	 	g2d.dispose();
	 	panel.update();
	 	
	}
	public HalfEdge flip(DelaunayGuiPanel panel){
		HalfEdge e = null;
		if (twin!=null){
			try {
				
				Thread.sleep(300);
				drawEdge(panel, panel.getBackColor());
				panel.drawPoints();
				TimeUnit.SECONDS.sleep(1);
			
				System.out.println("e not null");
				Point2D k = new Point2D.Double(twin.getNext().getpTwo().getX(), twin.getNext().getpTwo().getY());
				Point2D r = new Point2D.Double(next.getpTwo().getX(), next.getpTwo().getY());
				
				
				face.copy();
				twin.getFace().copy();
				//TriangleFace f1 = new TriangleFace(k, r, new Point2D.Double(pOne.getX(), pOne.getY()));
			
				e= new HalfEdge(k, r);
				e.setNext(prev);
				prev.setNext(twin.getNext());
				e.setPrev(twin.getNext());
				prev.setPrev(e);
				twin.getNext().setNext(e);
				twin.getNext().setPrev(prev);
				
				HalfEdge flipTwin = new HalfEdge(r,k);
				flipTwin.setNext(twin.getPrev());
				flipTwin.setPrev(next);
				next.setNext(flipTwin);
				next.setPrev(twin.getPrev());
				twin.getPrev().setNext(next);
				twin.getPrev().setPrev(flipTwin);
				e.setTwin(flipTwin);
				flipTwin.setTwin(e);
				
				System.out.println ("FLIP CRAP"+k+" "+r+" "+pOne+" "+pTwo);
				
		
				e.setFace(new TriangleNode(new TriangleFace(e, e.getNext(), e.getPrev()), panel));
				
				e.getNext().setFace(e.getFace());
				e.getPrev().setFace(e.getFace());
				flipTwin.setFace(new TriangleNode(new TriangleFace(flipTwin, flipTwin.getNext(), flipTwin.getPrev()), panel));
				flipTwin.getNext().setFace(flipTwin.getFace());
				flipTwin.getPrev().setFace(flipTwin.getFace());
				
				System.err.println("flip Internal to "+e+ "crossing: \n !!"+ face.getTriangle() +"\n !!"+ twin.getFace().getTriangle());
		
				
				face.addChild(e.getFace());
				face.addChild(flipTwin.getFace());
				twin.getFace().addChild(e.getFace());
				twin.getFace().addChild(flipTwin.getFace());
				e.getFace().getTriangle().drawFace(panel, Color.black, panel.getImage());
				flipTwin.getFace().getTriangle().drawFace(panel, Color.black, panel.getImage());
				
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
		return e;
	}
	public static void main(String[]args){
		/*Point2D a = new Point2D.Double(4.26, 5.52);
		Point2D b = new Point2D.Double(3, 3);
		Point2D c = new Point2D.Double(6.92, 4.32);
		Point2D d = new Point2D.Double(6.26, 1.82);
		
		TriangleFace f1 = new TriangleFace(a, b, c);
		TriangleFace f2 = new TriangleFace(b, d, c);
		f1.getEdge(1).setTwin(f2.getEdge(2));
		f1.getEdge(1).setFace(new TriangleNode(f1));
		f2.getEdge(2).setFace(new TriangleNode(f2));
		f2.getEdge(2).setTwin(f1.getEdge(1));
		/*System.out.println(f1.getEdge(1));
		HalfEdge flipped = f1.getEdge(1).flip();
		Point2D center = flipped.circleThroughPoints(b, d, c);
		System.out.println(center.getX()+","+ center.getY());
		System.out.println(flipped);
		System.out.println("After flip pointer check"+f1.getEdge(1).getTwin().getNext());
		//System.out.println("First face:"+ flipped.getFace().getTriangle());
		//System.out.println("Second face:"+ flipped.getTwin().getFace().getTriangle());
		System.out.println("first child f1"+f1.getEdge(1).getFace().getChild(0).getTriangle());
		System.out.println("second child f1"+f1.getEdge(1).getFace().getChild(1).getTriangle());
		System.out.println("first child f2"+f2.getEdge(2).getFace().getChild(0).getTriangle());
		System.out.println("second child f2"+f2.getEdge(2).getFace().getChild(1).getTriangle());
	
		Point2D i = new Point2D.Double(200, 200);
		Point2D f = new Point2D.Double(250, 160);
		Point2D k = new Point2D.Double(1000, 0);
		Point2D j = new Point2D.Double(300, 200);
		
		TriangleFace f3 = new TriangleFace(i, k, j);
		TriangleFace f4 = new TriangleFace(k, i, f);
		
		f3.getEdge(0).setTwin(f4.getEdge(0));
		f4.getEdge(0).setTwin(f3.getEdge(0));
		
		//System.out.println("illegal edge legal"+ f3.getEdge(0).isLegal());
		
		
		f3.getEdge(0).setFace(new TriangleNode(f3));
		f4.getEdge(0).setFace(new TriangleNode(f4));
		//HalfEdge e = f4.getEdge(0).flip();
		System.out.println("first child f3"+f3.getEdge(1).getFace().getChild(0).getTriangle());
		System.out.println("second child f3"+f3.getEdge(1).getFace().getChild(1).getTriangle());
		System.out.println("first child f4"+f4.getEdge(2).getFace().getChild(0).getTriangle());
		System.out.println("second child f4"+f4.getEdge(2).getFace().getChild(1).getTriangle());
		*/

	}
}
