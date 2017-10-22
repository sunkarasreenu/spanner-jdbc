package nl.topicus.jdbc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import nl.topicus.jdbc.test.category.UnitTest;

@Category(UnitTest.class)
public class CloudSpannerPooledConnectionTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final class SimpleConnectionEventListener implements ConnectionEventListener
	{
		private boolean error = false;

		private boolean closed = false;

		@Override
		public void connectionErrorOccurred(ConnectionEvent event)
		{
			error = true;
		}

		@Override
		public void connectionClosed(ConnectionEvent event)
		{
			closed = true;
		}
	};

	private static CloudSpannerPooledConnection createConnection() throws SQLException
	{
		CloudSpannerConnectionPoolDataSource ds = new CloudSpannerConnectionPoolDataSource();
		ds.setProjectId("helpful-adroit-123456");
		ds.setInstanceId("test-instance");
		ds.setDatabase("test");
		ds.setPvtKeyPath("C:\\Users\\MyUserName\\Documents\\CloudSpannerKeys\\cloudspanner-key.json");
		ds.setPvtKeyPath(null);
		ds.setOauthAccessToken("TEST");
		ds.setSimulateProductName("PostgreSQL");
		ds.setAllowExtendedMode(true);
		ds.setLoginTimeout(10);
		ds.setLogWriter(new PrintWriter(System.out));
		CloudSpannerPooledConnection res = ds.getPooledConnection();

		SimpleConnectionEventListener listener = new SimpleConnectionEventListener();
		res.addConnectionEventListener(listener);

		return res;
	}

	@Test
	public void testPooledAutoCommitConnection() throws SQLException
	{
		CloudSpannerPooledConnection subject = createConnection();
		Connection con = subject.getConnection();
		Assert.assertNotNull(con);
		Assert.assertFalse(con.isClosed());

		subject.close();
		Assert.assertTrue(con.isClosed());
	}

	@Test
	public void testPooledConnection() throws SQLException
	{
		CloudSpannerPooledConnection subject = createConnection();
		Connection con = subject.getConnection();
		con.setAutoCommit(false);
		Assert.assertNotNull(con);
		Assert.assertFalse(con.isClosed());

		subject.close();
		Assert.assertTrue(con.isClosed());
	}

	@Test
	public void testClosedConnection() throws SQLException
	{
		CloudSpannerPooledConnection subject = createConnection();
		subject.close();
		thrown.expect(SQLException.class);
		thrown.expectMessage("This PooledConnection has already been closed.");
		subject.getConnection();
	}

	@Test
	public void testEventListeners() throws SQLException
	{
		CloudSpannerPooledConnection subject = createConnection();
		ConnectionEventListener listener = Mockito.mock(ConnectionEventListener.class);
		subject.addConnectionEventListener(listener);
		subject.removeConnectionEventListener(listener);
		StatementEventListener statementListener = Mockito.mock(StatementEventListener.class);
		subject.addStatementEventListener(statementListener);
		subject.removeStatementEventListener(statementListener);
		subject.close();
	}

	@Test
	public void testTwoConnections() throws SQLException
	{
		testTwoConnections(true);
	}

	@Test
	public void testTwoConnectionsWithoutAutocommit() throws SQLException
	{
		testTwoConnections(false);
	}

	private void testTwoConnections(boolean autocommit) throws SQLException
	{
		CloudSpannerPooledConnection subject = createConnection();
		Connection con1 = subject.getConnection();
		con1.setAutoCommit(autocommit);
		Connection con2 = subject.getConnection();
		// Only one connection can be opened at any one time from the pooled
		// connection
		Assert.assertTrue(con1.isClosed());
		con2.setAutoCommit(autocommit);
		subject.close();
		Assert.assertTrue(con1.isClosed());
		Assert.assertTrue(con2.isClosed());
	}

	@Test
	public void testCloseConnection() throws SQLException
	{
		SimpleConnectionEventListener listener = new SimpleConnectionEventListener();
		CloudSpannerPooledConnection subject = createConnection();
		subject.addConnectionEventListener(listener);
		Connection connection = subject.getConnection();
		assertFalse(connection.isClosed());
		assertFalse(listener.closed);
		assertFalse(listener.error);
		connection.close();
		assertTrue(connection.isClosed());
		assertTrue(listener.closed);
		assertFalse(listener.error);
		subject.close();
	}

}