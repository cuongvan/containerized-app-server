package externalapi.appcall;

import externalapi.appcall.models.AppCallResult;
import externalapi.appcall.models.BatchAppCallResult;
import externalapi.DBConnectionPool;
import externalapi.appcall.models.CallDetail;
import externalapi.appcall.models.CallParam;
import externalapi.appcall.models.CallStatus;
import externalapi.appinfo.models.ParamType;
import helpers.DBHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DBAppCallDAO implements AppCallDAO {
    private DBConnectionPool dbPool;
    
    public static final String ANONYMOUS_USER = null;
    
    @Inject
    public DBAppCallDAO(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }

    @Override
    public void createNewCall(String callId, String appId, String userId, List<CallParam> callParams) {
        try (Connection conn = dbPool.getNonAutoCommitConnection()) {
            
            insertAppCallRow(conn, callId, appId, userId);
            
            try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO call_param (call_id, name, type, value) VALUES (?, ?, ?, ?)")) {

                for (CallParam p : callParams) {
                    stmt.setString(1, callId);
                    stmt.setString(2, p.name);
                    stmt.setString(3, p.type.name());
                    stmt.setString(4, p.value);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            
            conn.commit();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void insertAppCallRow(Connection conn, String callId, String appId, String userId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement (
            "INSERT INTO app_call (call_id, app_id, user_id) VALUES (?, ?, ?)")) {
            stmt.setString(1, callId);
            stmt.setString(2, appId);
            stmt.setString(3, userId);
            int nrows = stmt.executeUpdate();
        }
    }
    
    private void insertParam(Connection conn, String callId, String name, ParamType type, String value) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO call_param (call_id, name, type, value) VALUES (?, ?, ?)")) {
            
            stmt.setString(1, callId);
            stmt.setString(2, name);
            stmt.setString(3, type.name());
            stmt.setString(4, value);
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateFinishedAppCall(AppCallResult callResult) {
        //String query = "SELECT call_id, elapsed_seconds, call_status, output FROM app_call WHERE call_id = ? FOR UPDATE";
        String query = "UPDATE app_call SET elapsed_seconds = ?, call_status = ?, output = ? WHERE call_id = ?";
        try (Connection conn = dbPool.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query))
            {
                stmt.setLong(1, callResult.elapsedSeconds);
                stmt.setString(2, callResult.callStatus.name());
                stmt.setString(3, callResult.output);
                stmt.setString(4, callResult.appCallId);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    //@Override
    public BatchAppCallResult getCallInfoByCallId(String callId) {
        String query = "SELECT image, json_input, binary_input FROM app_info, app_call "
                    + "WHERE call_id = ? AND app_info.app_id = app_call.app_id";
        try (
            Connection conn = dbPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ){
            stmt.setString(1, callId);
            try (ResultSet r = stmt.executeQuery()) {
                if (!r.next()) {
                    throw new IllegalStateException("call_id not found in app_info");
                }
//                return new BatchAppCallInfo(
//                    callId,
//                    r.getString("image"),
//                    r.getBoolean("json_input"),
//                    r.getBoolean("binary_input")
//                );
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateStartedAppCall(String callId, String containerId) {
        String query = "UPDATE app_call SET status = ?, container_id = ? WHERE call_id = ?";
        // update call status
        try (
            Connection conn = dbPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setString(1, "Started");
            stmt.setString(2, containerId);
            stmt.setString(3, callId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } 
    }

    @Override
    public List<String> getAllCallIds() {
        String query = "SELECT call_id FROM app_call";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
        ) {
            List<String> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getString("call_id"));
            }
            
            return ids;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public CallDetail getById(String callId) {
        Connection connection = null;
        CallDetail callDetail = new CallDetail();
        try {
            connection = dbPool.getNonAutoCommitConnection();
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT call_id, app_id, user_id, elapsed_seconds, call_status, output "
                + "FROM app_call WHERE call_id = ?")) {
                stmt.setString(1, callId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next())
                        return null;
                    callDetail
                        .withCallId(callId)
                        .withAppId(rs.getString("app_id"))
                        .withUserId(rs.getString("user_id"))
                        .withElapsedSeconds(rs.getLong("elapsed_seconds"))
                        .withCallStatus(CallStatus.valueOf(rs.getString("call_status")))
                        .withOutput(rs.getString("output"));
                }
            }
            
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT name, type, value FROM call_param WHERE call_id = ?")) {
                stmt.setString(1, callId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        ParamType type = ParamType.valueOf(rs.getString("type"));
                        String value = rs.getString("value");
                        CallParam param = new CallParam(type, name, value);
                        callDetail.addCallParam(param);
                    }
                }
            }
            
            connection.commit();
            return callDetail;
        } catch (SQLException ex) {
            DBHelper.rollback(connection);
            throw new RuntimeException(ex);
        } finally {
            DBHelper.close(connection);
        }
    }
}
