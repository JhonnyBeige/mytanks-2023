package gtanks.services;

import com.mysql.jdbc.PreparedStatement;
import gtanks.commands.Type;
import gtanks.lobby.LobbyManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendsServices {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gtanks";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void getFriends(LobbyManager lobby, String userNickname) throws JSONException {
        JSONObject data = new JSONObject();

        JSONArray incoming = new JSONArray();
        JSONObject test = new JSONObject();
        test.put("battleId", "");
        test.put("rank", 28);
        test.put("online", false);
        test.put("id", "incoming");
        incoming.put(test);
        data.put("incoming", incoming);


        JSONArray outcoming = new JSONArray();
        JSONObject test2 = new JSONObject();
        test2.put("battleId", "");
        test2.put("rank", 28);
        test2.put("online", false);
        test2.put("id", "outcoming");
        outcoming.put(test2);
        data.put("outcoming", outcoming);


        JSONArray newAccepted = new JSONArray();
        JSONObject test3 = new JSONObject();
        test3.put("battleId", "");
        test3.put("rank", 28);
        test3.put("online", false);
        test3.put("id", "new_accepted");
        newAccepted.put(test3);
        data.put("new_accepted", newAccepted);


        JSONArray newIncoming = new JSONArray();
        JSONObject test4 = new JSONObject();
        test4.put("battleId", "");
        test4.put("rank", 28);
        test4.put("online", false);
        test4.put("id", "new_incoming");
        newIncoming.put(test4);
        data.put("new_incoming", newIncoming);

        JSONArray friendsArray = new JSONArray();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT friends_list FROM friends JOIN users ON friends.user_id = users.uid WHERE users.nickname = ?";
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, userNickname);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String friendsList = rs.getString("friends_list");
                JSONArray friends = new JSONArray(friendsList);

                for (int i = 0; i < friends.length(); i++) {
                    String friendNickname = friends.getString(i);
                    JSONObject friend = new JSONObject();
                    friend.put("battleId", "");
                    friend.put("rank", getUserRank(friendNickname, conn) + 1);
                    friend.put("online", isUserOnline(friendNickname, conn));
                    friend.put("id", friendNickname);
                    friendsArray.put(friend);
                }
            }

            data.put("friends", friendsArray);
            lobby.send(Type.LOBBY, "init_friends_list", data.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getUserRank(String nickname, Connection conn) throws SQLException {
        String sql = "SELECT rank FROM users WHERE nickname = ?";
        try (PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rank");
                }
            }
        }
        return 0;
    }

    private static boolean isUserOnline(String nickname, Connection conn) throws SQLException {
        return false;
    }
}
