import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;


public class TriangleNode {
	private TriangleFace triangle;
	private List <TriangleNode> children;
	private DelaunayGuiPanel panel;	
	public TriangleNode(TriangleFace newT, DelaunayGuiPanel panel){
		this.panel = panel;
		children = new ArrayList<TriangleNode>();
		triangle = newT;
		triangle.getEdge(0).setFace(this);
		triangle.getEdge(1).setFace(this);
		triangle.getEdge(2).setFace(this);
	}
	
	public void addChild(TriangleFace nChild){
		TriangleNode newChild = new TriangleNode(nChild, panel);
		children.add(newChild);
	}
	
	public void addChild(TriangleNode newNode){
		children.add(newNode);
	}
	public void copy(){
		triangle = new TriangleFace(
				   new Point2D.Double(triangle.getPoint(0).getX(),triangle.getPoint(0).getY()),
				   new Point2D.Double(triangle.getPoint(1).getX(), triangle.getPoint(1).getY()),
				   new Point2D.Double(triangle.getPoint(2).getX(), triangle.getPoint(2).getY()));	
	}
	
	public TriangleFace getTriangle(){
		return triangle;
	}
	public TriangleNode getChild(int i){
		return children.get(i);
	}
	
	public List<TriangleNode> getChildren(){
		return children;
	}
	
	public boolean isEmpty(){
		return (children.isEmpty());
	}
	public void addPointInside(Point2D p1, Point2D p2, Point2D q){
		System.out.println("points be all inside "+ q.getX()+","+q.getY());
		TriangleFace f = triangle;
		copy();
		
		HalfEdge aq = new HalfEdge(f.getPoint(0), q);
		HalfEdge cq = new HalfEdge(f.getPoint(2), q);
		HalfEdge qc = new HalfEdge(q, f.getPoint(2));
		HalfEdge qa = new HalfEdge(q, f.getPoint(0));
		HalfEdge bq = new HalfEdge(f.getPoint(1), q);
		HalfEdge qb = new HalfEdge(q, f.getPoint(1));
		
		aq.setPrev(f.getEdge(2));
		aq.setTwin(qa);
		aq.setNext(qc);
		
		qa.setNext(f.getEdge(0));
		qa.setPrev(bq);
		qa.setTwin(aq);
		
		bq.setTwin(qb);
		bq.setNext(qa);
		bq.setPrev(f.getEdge(0));
		
		qb.setTwin(bq);
		qb.setNext(f.getEdge(1));
		qb.setPrev(cq);
		
		cq.setTwin(qc);
		cq.setNext(qb);
		cq.setPrev(f.getEdge(1));
		
		qc.setTwin(cq);
		qc.setNext(f.getEdge(2));
		qc.setPrev(aq); 
		
		f.getEdge(0).setNext(bq);
		f.getEdge(0).setPrev(qa);
		f.getEdge(1).setNext(cq);
		f.getEdge(1).setPrev(qb);
		f.getEdge(2).setNext(aq);
		f.getEdge(2).setPrev(qc);
		
		TriangleFace f1 = new TriangleFace(f.getEdge(0), bq, qa);
		TriangleFace f2 = new TriangleFace(f.getEdge(1), cq, qb);
		TriangleFace f3 = new TriangleFace(f.getEdge(2), aq, qc);
		try {
			//TimeUnit.SECONDS.sleep(1)
		Thread.sleep(500);
		f1.drawFace(panel, Color.black, panel.getImage());
		//Thread.sleep(500);
		f2.drawFace(panel, Color.black, panel.getImage());
		//Thread.sleep(500);
		f3.drawFace(panel, Color.black, panel.getImage());
		Thread.sleep(500);
		
		addChild(f1);
		addChild(f2);
		addChild(f3);
		
		f.getEdge(0).setFace(children.get(0));
		f.getEdge(1).setFace(children.get(1));
		f.getEdge(2).setFace(children.get(2));
		
		aq.setFace(children.get(2));
		qa.setFace(children.get(0));
		bq.setFace(children.get(0));
		qb.setFace(children.get(1));
		cq.setFace(children.get(1));
		qc.setFace(children.get(2));
		
		legalizeEdge(qa.getNext(), p1, p2);
		legalizeEdge(qb.getNext(), p1, p2);
		legalizeEdge(qc.getNext(), p1, p2);
		
				
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private TriangleNode splitFace(TriangleNode currentNode, Point2D q, HalfEdge e){
		return splitFace(currentNode, q, e, Color.black, Color.black);
	}
	
	private TriangleNode splitFace(TriangleNode currentNode, Point2D q, HalfEdge e, Color a, Color b){
		//TriangleFace f = currentNode.getTriangle();
		currentNode.copy();
		System.err.println("point searched "+ q+ " on edge "+ e);
		System.err.println("next to "+ e+ " is "+ e.getNext() +" and prev is "+e.getPrev());
		HalfEdge aq = new HalfEdge(e.getpOne(), q);
			 HalfEdge qb = new HalfEdge(q, e.getpTwo());
			 HalfEdge qd = new HalfEdge(q, e.getNext().getpTwo());
			 HalfEdge dq = new HalfEdge(e.getNext().getpTwo(), q);
			 aq.setNext(qd);
			 aq.setPrev(e.getPrev());
			 dq.setNext(qb);
			 dq.setPrev(e.getNext());
			 dq.setTwin(qd);
			 qd.setNext(e.getPrev());
			 qd.setPrev(aq);
			 qd.setTwin(dq);
			 qb.setPrev(dq);
			 qb.setNext(e.getNext());
			 e.getNext().setNext(dq);
			 e.getNext().setPrev(qb);
			 e.getPrev().setNext(aq);
			 e.getPrev().setPrev(qd);
			 TriangleFace f1 = new TriangleFace(aq, qd, e.getPrev());
			 System.out.println("Collinear 1 inside "+a+f1);
			 TriangleFace f2 = new TriangleFace(qb, e.getNext(), dq);
			 System.out.println("Collinear 2 inside "+b+f2);
			 try {
				Thread.sleep(500);
			
				 f1.drawFace(panel, a, panel.getImage());
				 f2.drawFace(panel, b, panel.getImage());
				 Thread.sleep(500);
			 } catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			 }
			 //System.out.println("Collinear 2 inside "+f2);
			 currentNode.addChild(f1);
			 currentNode.addChild(f2);
			 aq.setFace(currentNode.getChild(0));
			 qd.setFace(currentNode.getChild(0));
			 e.getPrev().setFace(currentNode.getChild(0));
			 qb.setFace(currentNode.getChild(1));
			 dq.setFace(currentNode.getChild(1));
			 e.getNext().setFace(currentNode.getChild(1));
			 return currentNode;
	}
	
	private void addPointCollinear(Point2D p1, Point2D p2, HalfEdge e, Point2D q){
		
		if(e.getTwin()==null){
		System.out.println("printing point on bounding triangle edge" +e+ "point "+q.getX()+","+q.getY());
			splitFace(this, q, e);
			System.out.println(e);
			System.out.println("Border"+ e.getFace().getChild(0).getTriangle());
			System.out.println("Border"+e.getFace().getChild(1).getTriangle());
			legalizeEdge(e.getPrev(), p1, p2);
			legalizeEdge(e.getNext(), p1, p2);
		}
		else{
			System.err.println("creating 4 faces:"+ e + "\n" +e.getTwin());
			TriangleNode adjNode = e.getTwin().getFace();
			System.out.println("Face 1");
			splitFace(this, q, e);
			System.out.println("Face 2");
			splitFace(adjNode, q, e.getTwin());
			
			HalfEdge edge1 = children.get(0).getTriangle().getEdge(0);
			HalfEdge edgeAdj1 = adjNode.getChild(1).getTriangle().getEdge(0);
		
			//System.out.println(children.get(0).getTriangle());
			//System.out.println(children.get(1).getTriangle());
			//System.out.println(adjNode.getChild(0).getTriangle());
			//System.out.println(adjNode.getChild(1).getTriangle());
			//System.out.println ("This parent face" + getTriangle());
			//System.out.println ("Adjacent parent Faces" + adjNode.getTriangle());
			//System.out.println("creating 4 faces:"+ e + "\n" +e.getTwin());
			System.out.println("edges should be equal: "+edge1+","+edgeAdj1);
			
			HalfEdge edge2 = children.get(1).getTriangle().getEdge(0);
			HalfEdge edgeAdj2 = adjNode.getChild(0).getTriangle().getEdge(0);
			
			System.out.println("edges should be equal: "+edge2+","+edgeAdj2);
			
			edge2.setTwin(edgeAdj2);
			edgeAdj2.setTwin(edge2);
			edge1.setTwin(edgeAdj1);
			edgeAdj1.setTwin(edge1);

			
			legalizeEdge(edge1.getPrev(), p1, p2);
			legalizeEdge(edgeAdj1.getNext(), p1, p2);
			legalizeEdge(edge2.getNext(), p1, p2);
			legalizeEdge(edgeAdj2.getPrev(), p1, p2);
			
			
		}
	}
	public TriangleNode findPoint(Point2D q, Point2D p1, Point2D p2, boolean addPoint){
		return findPointRecurse(p1, p2, q, this, addPoint);
	}
	public TriangleNode findPointRecurse(Point2D p1, Point2D p2, Point2D q, TriangleNode currentNode, boolean addPoint){
		  
		 if (currentNode.isEmpty()){
			 if (addPoint){
				 HalfEdge e = currentNode.getTriangle().isCollinear(q);  
				 if (e!=null){
					 currentNode.addPointCollinear(p1, p2, e, q);
				 }
				 else{
					 currentNode.addPointInside(p1, p2, q);
				 }
			 }
			 return currentNode;
		  }
		  else{
			  for (TriangleNode child : currentNode.getChildren()){
				  if (child.getTriangle().isInside(q)){
					 return findPointRecurse(p1, p2, q, child, addPoint);
				  }
			  }
		  }
		 return null;
	}
	public void legalizeEdge(HalfEdge e, Point2D p1, Point2D p2){
			e.drawEdge(panel, Color.yellow);
			//Thread.sleep(1000);
		
			if(!(e.isLegal(p1, p2, panel))){
				e = e.flip(panel);
				panel.updateCircleImage();
				legalizeEdge(e.getPrev(), p1, p2);
				legalizeEdge(e.getTwin().getNext(), p1, p2);
	
			}
			
			e.drawEdge(panel, Color.black);
		//System.out.println (e + "is legal");
	}

}
