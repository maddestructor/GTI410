/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package controller;

import model.*;
import view.Application;
import view.CurvesPanel;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * <p>Title: Curves</p>
 * <p>Description: (AbstractTransformer)</p>
 * <p>Copyright: Copyright (c) 2004 Sébastien Bois, Eric Paquette</p>
 * <p>Company: (ÉTS) - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.9 $
 */
public class Curves extends AbstractTransformer implements DocObserver {

	private boolean firstPoint = false;
	private Curve curve;
	private CurvesPanel cp;

	/**
	 * Default constructor
	 */
	public Curves() {
		Application.getInstance().getActiveDocument().addObserver(this);
	}

	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_CURVES; }
	
	public void activate() {
		firstPoint = true;
		Document doc = Application.getInstance().getActiveDocument();
		List selectedObjects = doc.getSelectedObjects();
		boolean selectionIsACurve = false;
		if (selectedObjects.size() > 0){
			Shape s = (Shape)selectedObjects.get(0);
			if (s instanceof Curve){
				curve = (Curve)s;
				firstPoint = false;
				cp.setCurveType(curve.getCurveType());
				cp.setNumberOfSections(curve.getNumberOfSections());
			}
			else if (s instanceof ControlPoint){
				curve = (Curve)s.getContainer();
				firstPoint = false;
			}
		}

		if (firstPoint) {
			// First point means that we will have the first point of a new curve.
			// That new curve has to be constructed.
			curve = new Curve(100,100);
			setCurveType(cp.getCurveType());
			setNumberOfSections(cp.getNumberOfSections());
		}
	}

	/**
	 *
	 */
	protected boolean mouseReleased(MouseEvent e){
		int mouseX = e.getX();
		int mouseY = e.getY();

		if (firstPoint) {
			firstPoint = false;
			Document doc = Application.getInstance().getActiveDocument();
			doc.addObject(curve);
		}
		ControlPoint cp = new ControlPoint(mouseX, mouseY);
		curve.addPoint(cp);

		return true;
	}

	/**
	 * @param string
	 */
	public void setCurveType(String string) {
		if (string == CurvesModel.BEZIER) {
			curve.setCurveType(new BezierCurveType(CurvesModel.BEZIER));
		} else if (string == CurvesModel.LINEAR) {
			curve.setCurveType(new PolylineCurveType(CurvesModel.LINEAR));
		} else if (string == CurvesModel.HERMITE) {
			curve.setCurveType(new HermiteCurveType(CurvesModel.HERMITE));
		} else if (string == CurvesModel.BSPLINE) {
			curve.setCurveType(new BSplineCurveType(CurvesModel.BSPLINE));
		} else {
			System.out.println("Curve type [" + string + "] is unknown.");
		}
	}
	
	public void alignControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects();
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);
					
					Point p5 = ((ControlPoint)(Shape)curve.getShapes().get(controlPointIndex+1)).getCenter();
					Point p4 = ((ControlPoint)(Shape)curve.getShapes().get(controlPointIndex)).getCenter();
					Point p3 = ((ControlPoint)(Shape)curve.getShapes().get(controlPointIndex-1)).getCenter();
					
					int x =p4.x-p3.x;
					int y =p4.y-p3.y;
	
					
					Point newP4 = new Point(p3.x + (int)Math.round((p5.x - p3.x)/2.0), p3.y + (int)Math.round((p5.y - p3.y)/2.0));

					
					((Shape)curve.getShapes().get(controlPointIndex)).setCenter(newP4); 
					((Shape)curve.getShapes().get(controlPointIndex)).notifyObservers();
				}
			}

		}
	}
	
	public void symetricControlPoint() {
		
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects();
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				
				if ((curve.getShapes()).contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);
					Point p5 = ((ControlPoint)(Shape)curve.getShapes().get(controlPointIndex+1)).getCenter();
					Point p4 = ((ControlPoint)(Shape)curve.getShapes().get(controlPointIndex)).getCenter();
					Point p3 = ((ControlPoint)(Shape)curve.getShapes().get(controlPointIndex-1)).getCenter();
					
					
					Point newP4 = new Point(p3.x + (int)Math.round((p5.x - p3.x)/2.0), p3.y + (int)Math.round((p5.y - p3.y)/2.0));

					
					((Shape)curve.getShapes().get(controlPointIndex)).setCenter(newP4); 
					((Shape)curve.getShapes().get(controlPointIndex)).notifyObservers();
					
					
					
					
					
					//System.out.println("Try to apply C1 continuity on control point [" + shape.getCenter() + "]");
				}
			}

		}
	}
	
	public int getNumberOfSections() {
		if (curve != null)
			return curve.getNumberOfSections();
		else
			return Curve.DEFAULT_NUMBER_OF_SECTIONS;
	}

	public void setNumberOfSections(int n) {
		curve.setNumberOfSections(n);
	}

	public void setCurvesPanel(CurvesPanel cp) {
		this.cp = cp;
	}

	/* (non-Javadoc)
	 * @see model.DocObserver#docChanged()
	 */
	public void docChanged() {
	}

	/* (non-Javadoc)
	 * @see model.DocObserver#docSelectionChanged()
	 */
	public void docSelectionChanged() {
		activate();
	}
}
