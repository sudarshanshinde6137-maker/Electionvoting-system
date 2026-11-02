package com.election.ui;

import com.election.dao.CandidateDAO;
import com.election.dao.VoteDAO;
import com.election.model.Candidate;
import com.election.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VoterDashboard extends JFrame {

    private User currentUser;
    private CandidateDAO candidateDAO = new CandidateDAO();
    private VoteDAO voteDAO = new VoteDAO();
    private JTable candidateTable;
    private DefaultTableModel tableModel;

    public VoterDashboard(User user) {
        this.currentUser = user;
        setTitle("Voter Dashboard - Welcome, " + user.getFullName());
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(240, 244, 255));

        // ── Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 42, 86));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblWelcome = new JLabel("🗳  Welcome, " + currentUser.getFullName());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel(currentUser.isHasVoted()
            ? "✅ You have already voted"
            : "⚠  You have not voted yet", SwingConstants.RIGHT);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setForeground(currentUser.isHasVoted() ? new Color(80, 220, 100) : new Color(255, 210, 80));

        header.add(lblWelcome, BorderLayout.WEST);
        header.add(lblStatus, BorderLayout.EAST);

        // ── Table ──
        String[] cols = {"#", "Candidate Name", "Party", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        candidateTable = new JTable(tableModel);
        candidateTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        candidateTable.setRowHeight(30);
        candidateTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        candidateTable.getTableHeader().setBackground(new Color(52, 73, 140));
        candidateTable.getTableHeader().setForeground(Color.WHITE);
        candidateTable.setSelectionBackground(new Color(180, 210, 255));
        candidateTable.setGridColor(new Color(210, 220, 240));
        candidateTable.getColumnModel().getColumn(0).setMaxWidth(40);
        candidateTable.getColumnModel().getColumn(3).setPreferredWidth(300);

        loadCandidates();

        JScrollPane scroll = new JScrollPane(candidateTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230)));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        tablePanel.setBackground(new Color(240, 244, 255));
        JLabel lblTitle = new JLabel("  Select a Candidate to Vote For:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(25, 42, 86));
        tablePanel.add(lblTitle, BorderLayout.NORTH);
        tablePanel.add(scroll, BorderLayout.CENTER);

        // ── Buttons ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(240, 244, 255));

        JButton btnVote   = createButton("🗳  CAST MY VOTE",     new Color(39, 174, 96));
        JButton btnResult = createButton("📊  VIEW RESULTS",     new Color(52, 152, 219));
        JButton btnLogout = createButton("🚪  LOGOUT",           new Color(192, 57, 43));

        btnPanel.add(btnVote);
        btnPanel.add(btnResult);
        btnPanel.add(btnLogout);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // ── Actions ──
        btnVote.addActionListener(e -> handleVote());
        btnResult.addActionListener(e -> new ResultsFrame().setVisible(true));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        if (currentUser.isHasVoted()) btnVote.setEnabled(false);
    }

    private void loadCandidates() {
        tableModel.setRowCount(0);
        List<Candidate> candidates = candidateDAO.getAllCandidates();
        int i = 1;
        for (Candidate c : candidates) {
            tableModel.addRow(new Object[]{i++, c.getName(), c.getParty(), c.getDescription()});
        }
    }

    private void handleVote() {
        int row = candidateTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a candidate from the table first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String candidateName = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "You are about to vote for:\n\n  " + candidateName + "\n\nThis action cannot be undone. Proceed?",
            "Confirm Vote", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Find candidate id matching the selected row
        List<Candidate> candidates = candidateDAO.getAllCandidates();
        Candidate selected = candidates.get(row);

        boolean success = voteDAO.castVote(currentUser.getId(), selected.getId());
        if (success) {
            currentUser.setHasVoted(true);
            JOptionPane.showMessageDialog(this,
                "✅ Your vote has been cast successfully for " + candidateName + "!",
                "Vote Cast", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new VoterDashboard(currentUser).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Voting failed. You may have already voted.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
