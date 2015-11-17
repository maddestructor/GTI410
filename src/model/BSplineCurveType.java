package model;

import java.awt.*;
import java.util.List;

public class BSplineCurveType extends CurveType {


    private List bSplineMatrix =
            Matrix.buildMatrix4(-1.0 / 6.0, 3.0 / 6.0, -3.0 / 6.0, 1.0 / 6.0,
                    3.0 / 6.0, -6.0 / 6.0, 3.0 / 6.0, 0.0,
                    -3.0 / 6.0, 0.0, 3.0 / 6.0, 0.0,
                    1.0 / 6.0, 4.0 / 6.0, 1.0 / 6.0, 0.0);
    private List matrix = bSplineMatrix;

	public BSplineCurveType(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getNumberOfSegments(int numberOfControlPoints) {
		if (numberOfControlPoints >= 4) {
            return numberOfControlPoints - 3;
        } else {
			return 0;
		}
	}

	@Override
	public int getNumberOfControlPointsPerSegment() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public ControlPoint getControlPoint(List controlPoints, int segmentNumber, int controlPointNumber) {
		int controlPointIndex = segmentNumber + controlPointNumber;
		return (ControlPoint)controlPoints.get(controlPointIndex);
	}

    @Override
	public Point evalCurveAt(List controlPoints, double t) {
		List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);

        Point pi3 = ((ControlPoint) controlPoints.get(0)).getCenter();
        Point pi2 = ((ControlPoint) controlPoints.get(1)).getCenter();
        Point pi1 = ((ControlPoint) controlPoints.get(2)).getCenter();
        Point pi = ((ControlPoint) controlPoints.get(3)).getCenter();

        List gVector = Matrix.buildColumnVector4(pi, pi1, pi2, pi3);
        Point p = Matrix.eval(tVector, matrix, gVector);
		return p;
	}

}
