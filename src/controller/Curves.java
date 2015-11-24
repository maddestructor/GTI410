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
     * Cette méthode permet de changer le modèle de courbe utilisé
     * en fonction de l'élément choisi dans la combo box de l'interface.
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

    /**
     * Cette méthode est appelé lorsqu'on clique sur le bouton Aligned de l'interface graphique des courbes.
     * Elle inspiré du matériel des notes du cours GTI410, mais aussi des notes des cours équivalents à
     * l'université Simon Fraser et à L'université UMass Lowell. Le but de cette méthode est de créer une
     * continuité G1 entre les deux vecteurs de direction qui relie deux courbes. Pour se faire, la méthode
     * vérifie qu'un point de continuité est sélectionné et applique ensuite les correctifs afin de s'assurer
     * que les deux vecteurs soient des équivalences sans pour autant être parfaitement égaux. Il est
     * également important de noter que ce type de cotinuité (G1) peut devenir symmétrique (C1) si le
     * cooéficient "k" est égale à 1. (P4 - P3) = k(P5 - P4)
     *
     * @source http://goo.gl/xsK3GQ
     * @source http://goo.gl/uF6PsD
     * @source UMass Lowell and Simon Fraser University's Introduction to Computer Graphics course notes
     */
    public void alignControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects();
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);

                    if(controlPointIndex >= 3 && curve.getShapes().size() >= 7)
                    {
                        //On prend les points necessaire
                        Point p5 = ((ControlPoint) (Shape) curve.getShapes().get(controlPointIndex + 1)).getCenter();
                        Point p4 = ((ControlPoint) (Shape) curve.getShapes().get(controlPointIndex)).getCenter();
                        Point p3 = ((ControlPoint) (Shape) curve.getShapes().get(controlPointIndex - 1)).getCenter();

                        //On calcule nos vecteurs R1 et R4
                        int distance1X = p4.x - p3.x;
                        int distance1Y = p4.y - p3.y;
                        int distance2X = p5.x - p4.x;
                        int distance2Y = p5.y - p4.y;

                        //La norme de nos vecteur nous donne la grandeur de ceux-ci
                        double normeDistance1 = Math.sqrt((distance1X * distance1X) + (distance1Y * distance1Y));
                        double normeDistance2 = Math.sqrt((distance2X * distance2X) + (distance2Y * distance2Y));

                        //On peut ensuite calculer nos coefficients K à utiliser
                        double coefficientKX1 = distance1X / normeDistance1;
                        double coefficientKY1 = distance1Y / normeDistance1;
                        double coefficientKX2 = distance2X / normeDistance2;
                        double coefficientKY2 = distance2Y / normeDistance2;

                        //Pour bouger le point P5, on utilise le coefficient K1 et pour P3 ce serait K2
                        int newP5X = p4.x + (int)Math.round(coefficientKX1 * normeDistance2);
                        int newP5Y = p4.y + (int)Math.round(coefficientKY1 * normeDistance2);

                        //Le nouveau point
                        Point newP5 = new Point(newP5X, newP5Y);

                        ((Shape)curve.getShapes().get(controlPointIndex + 1)).setCenter(newP5);

                        curve.update();
                    }
                    else
                    {
                        System.out.println("This point is not valid");
                    }

                }
			}

		}

	}

    /**
     * Cette méthode est appelé lorsqu'on clique sur le bouton Symmetric de l'interface graphique des courbes.
     * Elle inspiré du matériel des notes du cours GTI410, mais aussi des notes des cours équivalents à
     * l'université Simon Fraser et à L'université UMass Lowell. Le but de cette méthode est de créer une
     * continuité C1 entre les deux vecteurs de direction qui relie deux courbes. Pour se faire, la méthode
     * vérifie qu'un point de continuité est sélectionné et applique ensuite les correctifs afin de s'assurer
     * que les deux vecteurs soient parfaitement égaux. (P4 - P3) = (P5 - P4)
     *
     * @source http://goo.gl/xsK3GQ
     * @source http://goo.gl/uF6PsD
     * @source UMass Lowell and Simon Fraser University's Introduction to Computer Graphics course notes
     */
    public void symetricControlPoint() {
		
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects();
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);

				if ((curve.getShapes()).contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);

                    if(controlPointIndex >= 3 && curve.getShapes().size() >= 7)
                    {
                        //On prend les points necessaire
                        Point p5 = ((ControlPoint) (Shape) curve.getShapes().get(controlPointIndex + 1)).getCenter();
                        Point p4 = ((ControlPoint) (Shape) curve.getShapes().get(controlPointIndex)).getCenter();
                        Point p3 = ((ControlPoint) (Shape) curve.getShapes().get(controlPointIndex - 1)).getCenter();

                        //Pour bouger le point P5, on utilise le coefficient K1 et pour P3 ce serait K2
                        int newP5X = 2 * p4.x - p3.x;
                        int newP5Y = 2 * p4.y - p3.y;

                        //Le nouveau point
                        Point newP5 = new Point(newP5X, newP5Y);

                        ((Shape) curve.getShapes().get(controlPointIndex + 1)).setCenter(newP5);

                        curve.update();

                        System.out.println("Try to apply C1 continuity on control point [" + controlPointIndex + "]");
                    }
                    else
                    {
                        System.out.println("This point is not valid");
                    }

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
