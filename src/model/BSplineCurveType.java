package model;

import java.awt.Point;
import java.util.List;

public class BSplineCurveType extends CurveType {

	public BSplineCurveType(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getNumberOfSegments(int numberOfControlPoints) {
		if (numberOfControlPoints >= 4) {
			return (numberOfControlPoints - 1) / 3;
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
		int controlPointIndex = segmentNumber * 3 + controlPointNumber;
		return (ControlPoint)controlPoints.get(controlPointIndex);
	}

	@Override
	public Point evalCurveAt(List controlPoints, double t) {
		List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);
		List gVector = Matrix.buildColumnVector4(((ControlPoint)controlPoints.get(0)).getCenter(), 
			((ControlPoint)controlPoints.get(1)).getCenter(), 
			((ControlPoint)controlPoints.get(2)).getCenter(),
			((ControlPoint)controlPoints.get(3)).getCenter());
		Point p = Matrix.eval(tVector, matrix, gVector);
		return p;
	}

	private List bezierMatrix = 
		Matrix.buildMatrix4(-1*1.0/6.0,  3*1.0/6.0, -3*1.0/6.0, 1*1.0/6.0, 
							 3*1.0/6.0, -6*1.0/6.0,  3*1.0/6.0, 0*1.0/6.0, 
							-3*1.0/6.0,  3*1.0/6.0,  0*1.0/6.0, 0*1.0/6.0, 
							 1*1.0/6.0,  0*1.0/6.0,  0*1.0/6.0, 0*1.0/6.0);
							 
	private List matrix = bezierMatrix;

}
