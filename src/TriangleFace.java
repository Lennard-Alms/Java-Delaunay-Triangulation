import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/*import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;
*/

public class TriangleFace {
	private HalfEdge[] edges;
	final double eps = 0.000000000001;
	
	public TriangleFace(Point2D a, Point2D b, Point2D c){
		edges = new HalfEdge[3];
		edges[0] = new HalfEdge (a, b);
		edges[1] = new HalfEdge (b, c);
		edges[2] = new HalfEdge (c, a);
		edges[0].setNext(edges[1]);
		edges[1].setNext(edges[2]);
		edges[2].setNext(edges[0]);
		edges[0].setPrev(edges[2]);
		edges[1].setPrev(edges[0]);
		edges[2].setPrev(edges[1]);
	}
	
	public TriangleFace(HalfEdge e0, HalfEdge e1, HalfEdge e2){
		edges = new HalfEdge[3];
		edges[0] = e0;
		edges[1] = e1;
		edges[2] = e2;
		/*edges[0].setNext(edges[1]);
		edges[1].setNext(edges[2]);
		edges[2].setNext(edges[0]);
		edges[0].setPrev(edges[2]);
		edges[1].setPrev(edges[0]);
		edges[2].setPrev(edges[1]);*/
	}
	
	public HalfEdge getEdge(int i){
		if (i <= 2) return edges[i];
		return null;
	}
	
	public void setEdge (int i, HalfEdge e){
		edges[i] = e;
	}
	
	public Point2D getPoint(int i){
	        switch (i) {
	            case 0:  return edges[0].getpOne();
	            case 1:  return edges[0].getpTwo();
	            case 2:  return edges[1].getpTwo();
	            default: break;
	        }
		return null;
	}
	
	private boolean boundingBox(Point2D q, HalfEdge e){
		if (e==null){
			return false;
		}
		double maxX = Math.max(e.getpOne().getX(), e.getpTwo().getX());
		double minX = Math.min(e.getpOne().getX(), e.getpTwo().getX());
		double maxY = Math.max(e.getpOne().getY(), e.getpTwo().getY());
		double minY = Math.min(e.getpOne().getY(), e.getpTwo().getY());
		return ( minX <= q.getX() && q.getX() <= maxX && minY <= q.getY() && q.getY() <= maxY );
	}
	public Double area(Point2D a, Point2D b, Point2D c){
		return Math.abs(a.getX()*b.getY()+b.getX()*c.getY()+c.getX()*a.getY()
				-a.getX()*c.getY()-c.getX()*b.getY()-b.getX()*a.getY())/2;
		//x1*y2+x2*y3+x3*y1-x1*y3-x3*y2-x2*y1)/2 

	}
	public boolean isInside(Point2D p)
    {
		//Point2D a = new Point2D.Double(edges[0].getpOne().getX(), edges[0].getpOne().getY());
		//Point2D b = new Point2D.Double(edges[0].getpTwo().getX(), edges[0].getpTwo().getY());
		//Point2D c = new Point2D.Double(edges[1].getpTwo().getX(), edges[1].getpTwo().getY());
		//Area PAB+ Area PBC + Area PAC= Area ABC 
		//if(getPoint(0).equals(p)|| getPoint(1).equals(p) || getPoint(2).equals(p)) return false; 
		if (isCollinear(p)!=null){
			return true;
		}
		double areaABC = area(edges[0].getpOne(), edges[0].getpTwo(), edges[1].getpTwo());
		double areaPAB = area(p, edges[0].getpOne(), edges[0].getpTwo());
		double areaPBC = area(p, edges[0].getpTwo(), edges[1].getpTwo());
		double areaPAC = area(p, edges[0].getpOne(), edges[1].getpTwo());
		double delta = Math.abs(areaABC - (areaPAB + areaPBC + areaPAC));
		//System.out.println("Inside delta:"+ delta);
		return delta <= eps;
		
    }

	boolean collinear(Point2D p1, Point2D p2, Point2D p3) {
	
		double result = (p1.getX() * (p2.getY()-p3.getY()) 
				+p2.getX()*(p3.getY()-p1.getY())
				+p3.getX()*(p1.getY()-p2.getY()))/2;
		//System.out.println ("Collinear delta: (" + Math.abs(result) +") "+p3.getX()+","+p3.getY());
		return Math.abs(result)/1000 <= 0.1;
		}
	
	public HalfEdge isCollinear(Point2D q){
		HalfEdge e = null;
		if (q.equals(edges[0].getpOne()) ||  q.equals(edges[0].getpTwo()) || q.equals(edges[1].getpTwo())) return null;
		if (collinear(edges[0].getpOne(), edges[0].getpTwo(), q)){
			e= edges[0];
		}
		else if (collinear(edges[1].getpOne(), edges[1].getpTwo(), q)){
			e= edges[1];
		}
		else if (collinear(edges[2].getpOne(), edges[2].getpTwo(), q)){
			e= edges[2];
		}
		if (!boundingBox(q, e)){
			System.out.println("Not bounded"+e+ " "+q.getX()+" "+q.getY());
			return null;
		}
		return e;
	}
	boolean edgeIntersect(HalfEdge e1, HalfEdge e2) 
	{ 
		double x1 = e1.getpOne().getX();
		double y1 = e1.getpOne().getY();
		double x2 = e1.getpTwo().getX();
		double y2 = e1.getpTwo().getY();
		
		double x3 = e2.getpOne().getX();
		double y3 = e2.getpOne().getY();
		double x4 = e2.getpTwo().getX();
		double y4 = e2.getpTwo().getY();
		
		if (e1.getpOne().equals(e2.getpOne()) || e1.getpOne().equals(e2.getpTwo()) 
				|| e1.getpTwo().equals(e2.getpOne()) || e1.getpTwo().equals(e2.getpTwo())) {
			return false;
			}
		if(collinear(e1.getpOne(), e1.getpTwo(), e2.getpOne()) ||
				collinear(e1.getpOne(), e1.getpTwo(), e2.getpTwo()) ||
				collinear(e2.getpOne(), e2.getpTwo(), e1.getpOne()) ||
				collinear(e2.getpOne(), e2.getpTwo(), e1.getpTwo())) return false;
		
		double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4); 
		if (d == 0) return false; 
		double xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d; 
		double yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d; 
		if(x3==x4) { if ( yi < Math.min(y1,y2) || yi > Math.max(y1,y2) )return false; } 
		Point2D.Double p = new Point2D.Double(xi,yi); 
		if (xi < Math.min(x1,x2) || xi > Math.max(x1,x2)) return false; 
		if (xi < Math.min(x3,x4) || xi > Math.max(x3,x4)) return false; 
		return true; 
	}
	
	public boolean intersects (TriangleFace f){
		Line2D tline = new Line2D.Double(); 
		//check that edges are crossing

		tline.setLine(getPoint(0).getX(), getPoint(0).getY(),
					  getPoint(1).getX(), getPoint(1).getY());
		if(tline.intersects(f.getPoint(0).getX(), f.getPoint(0).getY(), //1
				  		 f.getPoint(1).getX(),	f.getPoint(1).getY())){ 
			return true;
		}
		if(tline.intersects(f.getPoint(0).getX(), f.getPoint(0).getY(), //2
		  		 		 f.getPoint(2).getX(),	f.getPoint(2).getY())) return true;
		if(tline.intersects(f.getPoint(1).getX(), f.getPoint(1).getY(), //3
 		 		 		 f.getPoint(2).getX(),	f.getPoint(2).getY())) return true; 
		
		
		tline.setLine(getPoint(0).getX(), getPoint(0).getY(),
				      getPoint(2).getX(), getPoint(2).getY());
		if(tline.intersects(f.getPoint(0).getX(), f.getPoint(0).getY(), //4
		  		 	     f.getPoint(1).getX(),	f.getPoint(1).getY())) return true;
		if(tline.intersects(f.getPoint(0).getX(), f.getPoint(0).getY(), //5
 		 		         f.getPoint(2).getX(),	f.getPoint(2).getY())) return true; 
		if(tline.intersects(f.getPoint(1).getX(), f.getPoint(1).getY(), //6
 		 		 		 f.getPoint(2).getX(),	f.getPoint(2).getY())) return true; 

		tline.setLine(getPoint(1).getX(), getPoint(1).getY(),
			          getPoint(2).getX(), getPoint(2).getY());
		if(tline.intersects(f.getPoint(0).getX(), f.getPoint(0).getY(), //7
						 f.getPoint(1).getX(),	f.getPoint(1).getY())) return true;
		if(tline.intersects(f.getPoint(0).getX(), f.getPoint(0).getY(), //8
						 f.getPoint(2).getX(),	f.getPoint(2).getY())) return true;
		if(tline.intersects(f.getPoint(1).getX(), f.getPoint(1).getY(), //9
 		 		 		 f.getPoint(2).getX(),	f.getPoint(2).getY())) return true; 

		
		if (isInside(f.getPoint(0)) && 
			isInside(f.getPoint(1)) && 
			isInside(f.getPoint(2))) return true;
		if (f.isInside(getPoint(0)) && 
			f.isInside(getPoint(1)) && 
			f.isInside(getPoint(2))) return true;
		return false;
	}
	
	public boolean intersectsFace (TriangleFace f){
		Line2D tline = new Line2D.Double(); 
		//check that edges are crossing

		if(edgeIntersect(getEdge(0), f.getEdge(0))) return true;
		if(edgeIntersect(getEdge(0), f.getEdge(2))) return true;
		if(edgeIntersect(getEdge(0), f.getEdge(1))) return true;
	
		if(edgeIntersect(getEdge(2), f.getEdge(0))) return true;
		if(edgeIntersect(getEdge(2), f.getEdge(2))) return true;
		if(edgeIntersect(getEdge(2), f.getEdge(1))) return true;
		
		if(edgeIntersect(getEdge(1), f.getEdge(0))) return true;
		if(edgeIntersect(getEdge(1), f.getEdge(2))) return true;
		if(edgeIntersect(getEdge(1), f.getEdge(1))) return true;
		 		
		if (isInside(f.getPoint(0)) && 
			isInside(f.getPoint(1)) && 
			isInside(f.getPoint(2))) return true;
		if (f.isInside(getPoint(0)) && 
			f.isInside(getPoint(1)) && 
			f.isInside(getPoint(2))) return true;
		return false;
	}
	public void drawFace(DelaunayGuiPanel panel, Color c, BufferedImage img){
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(c);
		  int d = 12;
		  HalfEdge walk = getEdge(0);
		  //System.out.println("original "+f.getEdge(0));
		  int i =0;
		  Line2D line = new Line2D.Double();
		  if (walk!=null){
			  do {
				  line.setLine(walk.getpOne().getX(), walk.getpOne().getY(), walk.getpTwo().getX(), walk.getpTwo().getY());
				  g2d.setStroke(new BasicStroke(2));
				  g2d.draw(line);
				  g2d.fillOval((int)walk.getpOne().getX()-d/2, (int)walk.getpOne().getY()-d/2, d, d);
				  g2d.fillOval((int)walk.getpTwo().getX()-d/2, (int)walk.getpTwo().getY()-d/2, d, d);
				  //i++;
				 //System.out.println("walking "+ walk);
				 
				 walk = walk.getNext();
			   
			  } while (walk != getEdge(0));
		  }
		  panel.update();
	}
	public String toString(){
		return "Face("+edges[0]+")" +
				"("+edges[1]+")"+
				"("+edges[2]+")";
	}
	public static void main (String[]args){
		Point2D a = new Point2D.Double (20, 43);
		Point2D b = new Point2D.Double (8, 21);
		Point2D c = new Point2D.Double (37, 17);
		Point2D d = new Point2D.Double (20, 27);
		Point2D e = new Point2D.Double (28.34, 30.25);
		Point2D g = new Point2D.Double (28.7, 29.46);
		Point2D f = new Point2D.Double (18, 14);
		Point2D h = new Point2D.Double (30.3, 27.25);
		Point2D i = new Point2D.Double (27.72, 31.5);
		Point2D j = new Point2D.Double (12.73, 11.84);
		Point2D k = new Point2D.Double (21.69, 19.11);
		TriangleFace f0 = new TriangleFace(a, b, c);
		System.out.println("D inside ABC expected true: "+ f0.isInside(d));
		System.out.println("F inside ABC expected false: "+ f0.isInside(f));
		System.out.println("E collinear ABC expected true: "+ f0.isCollinear(e));
		System.out.println("E inside ABC expected true: "+ f0.isInside(e));
		System.out.println("G inside ABC expected true: "+ f0.isInside(g));
		System.out.println("G collinear ABC expected false: "+ f0.isCollinear(g));
		System.out.println("H inside ABC expected true: "+ f0.isInside(h));
		System.out.println("H collinear ABC expected true: "+ f0.isCollinear(h));
		System.out.println("I inside ABC expected false: "+ f0.isInside(i));
		System.out.println("I collinear expected false: "+ f0.isCollinear(i));
		
		TriangleFace t1 = new TriangleFace(new Point2D.Double(2.31,4.25), 
				 						   new Point2D.Double(5.42,4.25),
				 						   new Point2D.Double(3.94,1.83));
		TriangleFace t2 = new TriangleFace(new Point2D.Double(6.92,4.35), 
				 						   new Point2D.Double(10.39,4.35),
				 						   new Point2D.Double(8.68,1.94));
		TriangleFace t3 = new TriangleFace(new Point2D.Double(2.58,2.4), 
				 						   new Point2D.Double(5.77,2.4),
				 						   new Point2D.Double(3.88,4.81));
		TriangleFace t4 = new TriangleFace(new Point2D.Double(11.13,3.51), 
   				 						   new Point2D.Double(8.38,3.61),
   				 						   new Point2D.Double(10.25,3.05));
		TriangleFace f1 = new TriangleFace(b, f, c);
		TriangleFace f2 = new TriangleFace(b, j, k);
		TriangleFace f3 = new TriangleFace(k, j, c);
		TriangleFace f4 = new TriangleFace(d, b, c);
		TriangleFace f5 = new TriangleFace(b, j, f);
		TriangleFace f6 = new TriangleFace(h, b, c);
		System.out.println("expected false:"+ t1.intersectsFace(t2));
		System.out.println("expected true:"+ t1.intersectsFace(t3));
		System.out.println("expected true:"+ t2.intersectsFace(t4));
		System.out.println("expected false:"+ f0.intersectsFace(f1));
		System.out.println("expected false:"+ f0.isInside(a));
		System.out.println("ABC intersects BJK expected false:"+ f0.intersectsFace(f2));
		System.out.println("ABC intersects KJC expected false:"+ f0.intersectsFace(f3));
		System.out.println("BFC intersects KJC expected true:"+ f1.intersectsFace(f3));
		System.out.println("BJK intersects BFC expected true:"+ f2.intersectsFace(f1));
		System.out.println("ABC intersects DBC expected true:"+ f0.intersectsFace(f4));
		System.out.println("ABC intersects ABC expected true:"+ f0.intersectsFace(f0));
		System.out.println("BFC intersects BJF expected false:"+ f1.intersectsFace(f5));
		System.out.println("ABC intersects HBC expected true:"+ f0.intersectsFace(f6));
		System.out.println("b-c, j-k intersect expected false:"
		+ f0.edgeIntersect(f0.getEdge(1), f2.getEdge(1)));
		
		System.out.println("k-b, j-k intersect expected false:"
		+ f0.edgeIntersect(f2.getEdge(2), f2.getEdge(1)));
		
		System.out.println("j-k, b-f intersect expected true:"
		+ f0.edgeIntersect(f1.getEdge(0), f2.getEdge(1)));
		
	}
}
