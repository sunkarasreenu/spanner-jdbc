package nl.topicus.jdbc.statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import nl.topicus.jdbc.CloudSpannerConnection;
import nl.topicus.jdbc.resultset.CloudSpannerResultSet;

import com.google.cloud.spanner.DatabaseClient;

/**
 * 
 * @author loite
 *
 */
public class CloudSpannerStatement extends AbstractCloudSpannerStatement
{
	private DatabaseClient dbClient;

	public CloudSpannerStatement(CloudSpannerConnection connection, DatabaseClient dbClient)
	{
		super(connection);
		this.dbClient = dbClient;
	}

	protected DatabaseClient getDbClient()
	{
		return dbClient;
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException
	{
		com.google.cloud.spanner.ResultSet rs = dbClient.singleUse().executeQuery(
				com.google.cloud.spanner.Statement.of(sql));

		return new CloudSpannerResultSet(rs);
	}

	@Override
	public int executeUpdate(String sql) throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean execute(String sql) throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int getUpdateCount() throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

}
