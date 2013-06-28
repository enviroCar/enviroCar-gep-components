/**
 * Copyright (C) 2004
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.cario.gep.inbound;

import java.util.List;
import java.util.Map;

import com.esri.ges.spatial.GeometryType;
import com.esri.ges.spatial.Point;
import com.esri.ges.spatial.SpatialReference;

public class PointImpl implements Point {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static Point fromList(List<?> list) {
		PointImpl result = new PointImpl();
		result.setX((Double) list.get(0));
		result.setY((Double) list.get(1));
		if (list.size() == 3) {
			result.setZ((Double) list.get(2));
		}
		return result;
	}
	public static Point fromMap(Map<?, ?> map) {
		PointImpl result = new PointImpl();
		result.setX((Double) map.get("x"));
		result.setY((Double) map.get("y"));
		result.setZ((Double) map.get("z"));
		return result;
	}
	
	private GeometryType type = GeometryType.Point;
	private double x;
	private double y;
	private double z;

	@Override
	public boolean equals(Point point) {
		if (point == null) return false;
		
		return this.x == point.getX() && this.y == point.getY()
				&& this.z == point.getZ();
	}

	@Override
	public SpatialReference getSpatialReference() {
		return null;
	}

	@Override
	public GeometryType getType() {
		return type;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getZ() {
		return z;
	}

	@Override
	public void setSpatialReference(SpatialReference sr) {

	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public String toJson() {
		return String.format("{ \"x\" : %s, \"y\" : %s, \"z\" : %s }", x, y, z);
	}
}