package gov.usgs.owi.nldi.typehandler;

import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Geometry;
import mil.nga.sf.geojson.Point;
import mil.nga.sf.geojson.Position;
import org.apache.ibatis.type.*;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes({Geometry.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class GeometryTypeHandler extends BaseTypeHandler<Geometry> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Geometry geometry, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public Geometry getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String geoJson = resultSet.getString(s);
        Geometry geometry = FeatureConverter.toGeometry(geoJson);
        return geometry;
    }

    @Override
    public Geometry getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String geoJson = resultSet.getString(i);
        Geometry geometry = FeatureConverter.toGeometry(geoJson);
        return geometry;
    }

    @Override
    public Geometry getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String geoJson = callableStatement.getString(i);
        Geometry geometry = FeatureConverter.toGeometry(geoJson);
        return geometry;
    }
}
