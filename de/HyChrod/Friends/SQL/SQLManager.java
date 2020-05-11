package de.HyChrod.Friends.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.UUID;

import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Hashing.Options;
import de.HyChrod.Friends.Hashing.Request;

public class SQLManager {
	
	private String host, database, username, password, port;
	
	private Connection con;
	
	public SQLManager(String[] sqldata) {
		this.host = sqldata[0];
		this.database = sqldata[2];
		this.port = sqldata[1];
		this.username = sqldata[3];
		this.password = sqldata[4];
	}
	
	public boolean connect() {
		try {
			if(con != null)
				con.close();
			
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":"+ this.port + "/" + this.database + "?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", this.username, this.password);
			
			createTables();
		} catch (Exception ex) {}
		return con != null;
	}
	
	public boolean isConnected() {
		return con != null;
	}
	
	public Connection getCon() throws SQLException {
		if(con.isClosed()) connect();
		return con;
	}
	
	private void createTables() throws SQLException {
		getCon().prepareStatement("create table if not exists friends_playerdata(uuid VARCHAR(50) NOT NULL PRIMARY KEY, name VARCHAR(50), online int, lastOnline long, server VARCHAR(50));").executeUpdate();
		getCon().prepareStatement("create table if not exists friends_frienddata(uuid VARCHAR(50), uuid2 VARCHAR(50), favorite int, timestamp long, nickname TEXT, canSendMessages int);").executeUpdate();
		getCon().prepareStatement("create table if not exists friends_requests(uuid VARCHAR(50), uuid2 VARCHAR(50), message TEXT, timestamp long);").executeUpdate();
		getCon().prepareStatement("create table if not exists friends_blocked(uuid VARCHAR(50), uuid2 VARCHAR(50), message TEXT, timestamp long);").executeUpdate();
		getCon().prepareStatement("create table if not exists friends_options(uuid VARCHAR(50) NOT NULL PRIMARY KEY, offline int, receiveMsg int, receiveRequests int, sorting int, status TEXT, jumping int);").executeUpdate();
		try {
			perform("alter table friends_playerdata add server varchar(50) after lastOnline");
			perform("alter table friends_options add jumping int after status");
			perform("alter table friends_playerdata add online int after name");
		} catch (Exception ex) {}
	}
	
	public String getServer(UUID uuid) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getCon().prepareStatement("select server from friends_playerdata where uuid='" + uuid.toString() + "'");
			rs = ps.executeQuery();
			if(rs.next() && rs.getString("server") != null)
				return rs.getString("server");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			close(rs, ps);
		}
		return null;
	}
	
	public boolean isOnline(UUID uuid) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getCon().prepareStatement("select p.online,o.offline from friends_playerdata p join friends_options o using (uuid) where uuid='" + uuid.toString() + "';");
			rs = ps.executeQuery();
			if(rs.next()) {
				boolean online = rs.getString("p.online") == null ? false : rs.getInt("p.online") == 1;
				boolean offMode = rs.getInt("o.offline") == 1;
				return online && !offMode;
			}
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(rs, ps);
		}
		return false;
	}
	
	public long getLastOnline(UUID uuid) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getCon().prepareStatement("select lastOnline from friends_playerdata where uuid='" + uuid.toString() + "';");
			rs = ps.executeQuery();
			if(rs.next() && rs.getString("lastOnline") != null) return rs.getLong("lastOnline");
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(rs, ps);
		}
		return -1;
	}
	
	public void insertIntoFriends(LinkedList<Friendship> newFrienships) {
		if(!isConnected()) return;
		PreparedStatement ps = null;
		try {
			String statement = "insert into friends_frienddata(uuid, uuid2, favorite, timestamp, nickname, cansendmessages) values";
			for(Friendship fs : newFrienships) {
				statement = statement + "('" + fs.getPlayer().toString() + "','" + fs.getFriend().toString() + "','" + (fs.getFavorite() ? 1 : 0) + "','" + fs.getTimestamp() + "','" + fs.getNickname() + "','" + (fs.getCanSendMessages() ? 1 : 0) + "')" + ",";
			}
			statement = statement.substring(0, statement.length()-1);
			if(!newFrienships.isEmpty()) {
				ps = getCon().prepareStatement(statement);
				ps.executeUpdate();
			}
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(null, ps);
		}
	}
	
	public void insertIntoRequests(LinkedList<Request> newRequest) {
		if(!isConnected()) return;
		PreparedStatement ps = null;
		try {
			String statement = "insert into friends_requests(uuid, uuid2, message, timestamp) values";
			for(Request fs : newRequest) {
				statement = statement + "('" + fs.getPlayer().toString() + "','" + fs.getPlayerToAdd().toString() + "','" +  fs.getMessage() + "','"  + fs.getTimestamp() + "'),";
			}
			statement = statement.substring(0, statement.length()-1);
			if(!newRequest.isEmpty()) {
				ps = getCon().prepareStatement(statement);
				ps.executeUpdate();
			}
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(null, ps);
		}
	}
	
	public void insertIntoBlocked(LinkedList<Blockplayer> newBlocked) {
		if(!isConnected()) return;
		PreparedStatement ps = null;
		try {
			String statement = "insert into friends_blocked(uuid, uuid2, message, timestamp) values";
			for(Blockplayer fs : newBlocked) {
				statement = statement + "('" + fs.getPlayer().toString() + "','" + fs.getBlocked().toString() + "','" +  fs.getMessage() + "','"  + fs.getTimestamp() + "'),";
			}
			statement = statement.substring(0, statement.length()-1);
			if(!newBlocked.isEmpty()) {
				ps = getCon().prepareStatement(statement);
				ps.executeUpdate();
			}
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(null, ps);
		}
	}
	
	public void updateFriends(LinkedList<Friendship> toUpdate) {
		if(!isConnected()) return;
		PreparedStatement ps = null;
		try {
			for(Friendship fs : toUpdate) {
				ps = getCon().prepareStatement("update friends_frienddata set favorite ='" + (fs.getFavorite() ? 1 : 0) + "', nickname = '" + fs.getNickname() 
				+ "', cansendmessages = '" + (fs.getCanSendMessages() ? 1 : 0) + "' where uuid = '" + fs.getPlayer().toString() + "' and uuid2 = '" + fs.getFriend().toString() + "'");
				ps.executeUpdate();
				close(null, ps);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			close(null, ps);
		}
	}
	
	public void deleteFromFriends(LinkedList<Friendship> toDelete) {
		if(!isConnected()) return;
		PreparedStatement ps = null;
		try {
			for(Friendship fs : toDelete) {
				ps = getCon().prepareStatement("delete from friends_frienddata where uuid = '" + fs.getPlayer().toString() + "' and uuid2 = '" + fs.getFriend().toString() + "';");
				ps.executeUpdate();
				close(null, ps);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			close(null, ps);
		}
	}
	
	public void deleteFromRequests(LinkedList<Request> toDelete) {
		if(!isConnected()) return;
		PreparedStatement ps = null;
		try {
			for(Request fs : toDelete) {
				ps = getCon().prepareStatement("delete from friends_requests where uuid = '" + fs.getPlayer().toString() + "' and uuid2 = '" + fs.getPlayerToAdd().toString() + "';");
				ps.executeUpdate();
				close(null, ps);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			close(null, ps);
		}
	}
	
	public void deleteFromBlocked(LinkedList<Blockplayer> toDelete) {
		if(!isConnected()) return;
		PreparedStatement ps = null;
		try {
			for(Blockplayer fs : toDelete) {
				ps = getCon().prepareStatement("delete from friends_blocked where uuid = '" + fs.getPlayer().toString() + "' and uuid2 = '" + fs.getBlocked().toString() + "';");
				ps.executeUpdate();
				close(null, ps);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			close(null, ps);
		}
	}
	
	public void updateOptions(Options options) {
		if(!isConnected()) return;
		PreparedStatement ps = null;
		try {
			ps = getCon().prepareStatement("insert into friends_options(uuid, offline, receiveMsg, receiveRequests, sorting, status, jumping) "
					+ "values ('" + options.getUuid().toString() + "','" + (options.isOffline() ? 1 : 0) + "','" + (options.getMessages() ? 1 : options.getFavMessages() ? 2 : 0) 
					+ "','" + (options.getRequests() ? 1 : 0) + "','" + options.getSorting() + "','" + (options.getStatus() == null || options.getStatus().length() < 1 ? "" : options.getStatus()) 
					+ "', '" + (options.getJumping() ? 1 : 0) + "') on duplicate key " + "update uuid=values(uuid), offline=values(offline), receiveMsg=values(receiveMsg), "
							+ "receiveRequests=values(receiveRequests), sorting=values(sorting), status=values(status), jumping=values(jumping);");
			ps.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			close(null, ps);
		}
	}
	
	public LinkedList<Friendship> getFriendships(UUID uuid) {
		LinkedList<Friendship> friendships = new LinkedList<Friendship>();
		if(!isConnected()) return friendships;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = getCon().prepareStatement("select distinct f.uuid2,f.favorite,f.timestamp,f.cansendmessages,o.status,p.lastOnline from friends_frienddata f, friends_options o, friends_playerdata p where o.uuid = f.uuid2 and f.uuid = '" + uuid.toString() + "' and p.uuid=f.uuid2;");
			rs = ps.executeQuery();
			while(rs.next()) {
				friendships.add(new Friendship(uuid, UUID.fromString(rs.getString("f.uuid2")), rs.getLong("f.timestamp"), rs.getInt("f.favorite") == 0 ? false : true, 
						rs.getInt("f.cansendmessages") == 0 ? false : true, rs.getString("o.status"), rs.getLong("p.lastOnline")));
			}
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(rs, ps);
		}
		return friendships;
	}
	
	public LinkedList<Request> getRequests(UUID uuid) {
		LinkedList<Request> requests = new LinkedList<Request>();
		if(!isConnected()) return requests;
		ResultSet rs  = null;
		PreparedStatement ps = null;
		try {
			ps = getCon().prepareStatement("select distinct uuid2,message,timestamp from friends_requests where uuid = '" + uuid.toString() + "';");
			rs = ps.executeQuery();
			while(rs.next())
				requests.add(new Request(uuid, UUID.fromString(rs.getString("uuid2")), rs.getString("message"), rs.getLong("timestamp")));
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(rs, ps);
		}
		return requests;
	}
	
	public LinkedList<Blockplayer> getBlocked(UUID uuid) {
		LinkedList<Blockplayer> blocked = new LinkedList<Blockplayer>();
		if(!isConnected()) return blocked;
		ResultSet rs  = null;
		PreparedStatement ps = null;
		try {
			ps = getCon().prepareStatement("select distinct uuid2,message,timestamp from friends_blocked where uuid = '" + uuid.toString() + "';");
			rs = ps.executeQuery();
			while(rs.next())
				blocked.add(new Blockplayer(uuid, UUID.fromString(rs.getString("uuid2")), rs.getLong("timestamp"), rs.getString("message")));
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(rs, ps);
		}
		return blocked;
	}
	
	public Options getOptions(UUID uuid) {
		Options opt = new Options(uuid, false, true, 1, null, 0, false);
		if(!isConnected()) return opt;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getCon().prepareStatement("select offline,receivemsg,receiverequests,sorting,status,jumping from friends_options where uuid = '" + uuid.toString() + "'");
			rs = ps.executeQuery();
			if(rs.next()) {
				opt.setOffline(rs.getInt("offline") == 0 ? false : true);
				opt.setReceive_messages(rs.getInt("receivemsg"));
				opt.setReceive_requests(rs.getInt("receiverequests") == 0 ? false : true);
				opt.setSorting(rs.getInt("sorting"));
				opt.setStatus(rs.getString("status"));
				opt.setJumping(rs.getString("jumping") != null ? (rs.getInt("jumping") == 0 ? false : true) : true);
			}
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(rs, ps);
		}
		return opt;
	}
	
	public UUID getUUIDByName(String name) {
		if(!isConnected()) return null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		UUID uuid = null;
		try {
			ps = getCon().prepareStatement("select uuid from friends_playerdata where name = '" + name + "'");
			rs = ps.executeQuery();
			if(rs.next())
				uuid = UUID.fromString(rs.getString("uuid"));
		} catch (Exception ex) {ex.printStackTrace();} finally {
			close(rs, ps);
		}
		return uuid;
	}
	
	public String getNameByUUID(UUID uuid) {
		if(!isConnected()) return null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String name = null;
		try {
			ps = getCon().prepareStatement("select name from friends_playerdata where uuid = '" + uuid.toString() + "';");
			rs = ps.executeQuery();
			if(rs.next())
				name = rs.getString("name");
		} catch (Exception ex) {
		} finally {
			close(rs, ps);
		}
		return name;
	}
	
	public void perform(String update) {
		if(!isConnected()) return;
		PreparedStatement ps = null;
		try {
			ps = getCon().prepareStatement(update);
			ps.executeUpdate();
		} catch (Exception ex) {} finally {
			close(null, ps);
		}
	}
	
	private void close(ResultSet rs, PreparedStatement ps) {
		try {
			if(rs != null)
				rs.close();
			if(ps != null)
				ps.close();
		} catch (Exception ex) {}
	}
	
	public void closeConnection() {
		if(con != null)
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

}
