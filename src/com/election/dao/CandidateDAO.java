package com.election.dao;

import com.election.model.Candidate;
import com.election.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO {

    // Get all candidates
    public List<Candidate> getAllCandidates() {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates ORDER BY vote_count DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get candidate by ID
    public Candidate getCandidateById(int id) {
        String sql = "SELECT * FROM candidates WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add a new candidate
    public boolean addCandidate(Candidate c) {
        String sql = "INSERT INTO candidates (name, party, description) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getParty());
            ps.setString(3, c.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update candidate info
    public boolean updateCandidate(Candidate c) {
        String sql = "UPDATE candidates SET name=?, party=?, description=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getParty());
            ps.setString(3, c.getDescription());
            ps.setInt(4, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a candidate
    public boolean deleteCandidate(int id) {
        String sql = "DELETE FROM candidates WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Increment vote count (called during voting)
    public boolean incrementVote(int candidateId) {
        String sql = "UPDATE candidates SET vote_count = vote_count + 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get total votes cast
    public int getTotalVotes() {
        String sql = "SELECT SUM(vote_count) FROM candidates";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Reset all votes (admin function)
    public boolean resetAllVotes() {
        String[] sqls = {
            "UPDATE candidates SET vote_count = 0",
            "DELETE FROM votes",
            "UPDATE users SET has_voted = FALSE WHERE role = 'VOTER'"
        };
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {
            for (String sql : sqls) st.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Candidate mapRow(ResultSet rs) throws SQLException {
        return new Candidate(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("party"),
            rs.getString("description"),
            rs.getInt("vote_count")
        );
    }
}
