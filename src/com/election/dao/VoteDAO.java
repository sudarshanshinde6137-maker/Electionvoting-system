package com.election.dao;

import com.election.util.DBConnection;

import java.sql.*;

public class VoteDAO {

    // Cast a vote — inserts into votes table and increments candidate count atomically
    public boolean castVote(int voterId, int candidateId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert vote record
            String insertVote = "INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertVote)) {
                ps.setInt(1, voterId);
                ps.setInt(2, candidateId);
                ps.executeUpdate();
            }

            // Increment candidate's vote count
            String updateCandidate = "UPDATE candidates SET vote_count = vote_count + 1 WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateCandidate)) {
                ps.setInt(1, candidateId);
                ps.executeUpdate();
            }

            // Mark voter as voted
            String markVoted = "UPDATE users SET has_voted = TRUE WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(markVoted)) {
                ps.setInt(1, voterId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    // Check if voter has already voted
    public boolean hasVoted(int voterId) {
        String sql = "SELECT id FROM votes WHERE voter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, voterId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get total number of votes cast
    public int getTotalVotesCast() {
        String sql = "SELECT COUNT(*) FROM votes";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
