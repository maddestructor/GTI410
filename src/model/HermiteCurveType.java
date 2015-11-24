package model;

import java.awt.*;
import java.util.List;

public class HermiteCurveType extends CurveType {

    //Matrice de Hermite
    private List hermiteMatrix =
            Matrix.buildMatrix4(2, -2, 1, 1,
                    -3, 3, -2, -1,
                    0, 0, 1, 0,
                    1, 0, 0, 0);
    private List matrix = hermiteMatrix;

    public HermiteCurveType(String name) {
        super(name);
    }

    @Override
    public int getNumberOfSegments(int numberOfControlPoints) {
        //Si au moins un segment
        if (numberOfControlPoints >= 4) {
            //retourne le nombre de segments
            return (numberOfControlPoints - 1) / 3;
        } else {
            return 0;
        }
    }

    @Override
    public int getNumberOfControlPointsPerSegment() {
        return 4;
    }

    @Override
    public ControlPoint getControlPoint(List controlPoints, int segmentNumber, int controlPointNumber) {
        int controlPointIndex = segmentNumber * 3 + controlPointNumber;
        return (ControlPoint) controlPoints.get(controlPointIndex);
    }

    @Override
    public Point evalCurveAt(List controlPoints, double t) {
        //on crée la matrice de fonction
        List tVector = Matrix.buildRowVector4(t * t * t, t * t, t, 1);

        //On va chercher nos points
        Point p1 = ((ControlPoint) controlPoints.get(0)).getCenter();
        Point p2 = ((ControlPoint) controlPoints.get(1)).getCenter();
        Point p3 = ((ControlPoint) controlPoints.get(2)).getCenter();
        Point p4 = ((ControlPoint) controlPoints.get(3)).getCenter();

        //On détermine les vecteurs à partir de nos points
        Point r1 = new Point(p2.x - p1.x, p2.y - p1.y);
        Point r4 = new Point(p4.x - p3.x, p4.y - p3.y);

        //On crée notre matrice d'évaluation avec nos points d'extrémités et nos vecteurs
        List gVector = Matrix.buildColumnVector4(p1, p4, r1, r4);

        //On évalue le tout
        Point p = Matrix.eval(tVector, matrix, gVector);
        return p;
    }

}
