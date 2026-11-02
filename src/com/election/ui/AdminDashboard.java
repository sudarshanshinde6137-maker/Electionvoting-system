package com.election.ui;

import com.election.dao.CandidateDAO;
import com.election.dao.UserDAO;
import com.election.dao.VoteDAO;
import com.election.model.Candidate;
import com.election.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private User admin;
    private CandidateDAO candidateDAO = new CandidateDAO();
    private UserDAO userDAO = new UserDAO();
    private VoteDAO voteDAO = new VoteDAO();

    private JTable candidateTable, voterTable;
    private DefaultTableModel candidateModel, voterModel;
    private JLabel lblStats;

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getFullName());
        setSize(850, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 50));

        // ── Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 20, 40));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lblTitle = new JLabel("⚙  ADMIN CONTROL PANEL");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(255, 210, 60));
        lblStats = new JLabel("", SwingConstants.RIGHT);
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStats.setForeground(new Color(180, 200, 255));
        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblStats, BorderLayout.EAST);

        // ── Tabbed Pane ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(new Color(30, 30, 50));
        tabs.setForeground(Color.WHITE);

        tabs.addTab("🏅 Candidates", buildCandidatePanel());
        tabs.addTab("👥 Voters",     buildVoterPanel());

        // ── Bottom Buttons ──
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnBar.setBackground(new Color(20, 20, 40));

        JButton btnResults = createButton("📊 View Results",  new Color(52, 152, 219));
        JButton btnReset   = createButton("🔄 Reset Votes",   new Color(192, 57, 43));
        JButton btnLogout  = createButton("🚪 Logout",        new Color(80, 80, 100));

        btnBar.add(btnResults);
        btnBar.add(btnReset);
        btnBar.add(btnLogout);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(btnBar, BorderLayout.SOUTH);
        add(mainPanel);

        refreshStats();

        // Actions
        btnResults.addActionListener(e -> new ResultsFrame().setVisible(true));
        btnReset.addActionListener(e -> handleReset());
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
    }

    // ── Candidate Tab ──
    private JPanel buildCandidatePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(240, 244, 255));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Name", "Party", "Description", "Votes"};
        candidateModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        candidateTable = styledTable(candidateModel);
        candidateTable.getColumnModel().getColumn(0).setMaxWidth(40);
        candidateTable.getColumnModel().getColumn(4).setMaxWidth(70);

        loadCandidates();

        JScrollPane scroll = new JScrollPane(candidateTable);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btns.setBackground(new Color(240, 244, 255));
        JButton btnAdd = createButton("+ Add",    new Color(39, 174, 96));
        JButton btnEdit = createButton("✏ Edit",  new Color(230, 126, 34));
        JButton btnDel = createButton("🗑 Delete", new Color(192, 57, 43));
        btns.add(btnAdd); btns.add(btnEdit); btns.add(btnDel);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> showCandidateDialog(null));
        btnEdit.addActionListener(e -> {
            int row = candidateTable.getSelectedRow();
            if (row < 0) { warn("Select a candidate."); return; }
            int id = (int) candidateModel.getValueAt(row, 0);
            showCandidateDialog(candidateDAO.getCandidateById(id));
        });
        btnDel.addActionListener(e -> {
            int row = candidateTable.getSelectedRow();
            if (row < 0) { warn("Select a candidate."); return; }
            int id = (int) candidateModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete this candidate?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                candidateDAO.deleteCandidate(id);
                loadCandidates();
            }
        });
        return panel;
    }

    // ── Voter Tab ──
    private JPanel buildVoterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(240, 244, 255));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Username", "Full Name", "Email", "Voted?"};
        voterModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        voterTable = styledTable(voterModel);
        voterTable.getColumnModel().getColumn(0).setMaxWidth(40);
        voterTable.getColumnModel().getColumn(4).setMaxWidth(70);

        loadVoters();

        JScrollPane scroll = new JScrollPane(voterTable);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btns.setBackground(new Color(240, 244, 255));
        JButton btnDel = createButton("🗑 Remove Voter", new Color(192, 57, 43));
        JButton btnRefresh = createButton("🔄 Refresh",  new Color(52, 152, 219));
        btns.add(btnDel); btns.add(btnRefresh);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> { loadVoters(); refreshStats(); });
        btnDel.addActionListener(e -> {
            int row = voterTable.getSelectedRow();
            if (row < 0) { warn("Select a voter."); return; }
            int id = (int) voterModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Remove this voter account?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                userDAO.deleteVoter(id);
                loadVoters();
                refreshStats();
            }
        });
        return panel;
    }

    private void showCandidateDialog(Candidate c) {
        boolean isEdit = (c != null);
        JTextField name  = new JTextField(isEdit ? c.getName() : "");
        JTextField party = new JTextField(isEdit ? c.getParty() : "");
        JTextField desc  = new JTextField(isEdit ? c.getDescription() : "");

        JPanel form = new JPanel(new GridLayout(6, 1, 5, 5));
        form.add(new JLabel("Name:")); form.add(name);
        form.add(new JLabel("Party:")); form.add(party);
        form.add(new JLabel("Description:")); form.add(desc);

        int result = JOptionPane.showConfirmDialog(this, form,
            isEdit ? "Edit Candidate" : "Add Candidate", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (isEdit) {
                c.setName(name.getText()); c.setParty(party.getText()); c.setDescription(desc.getText());
                candidateDAO.updateCandidate(c);
            } else {
                candidateDAO.addCandidate(new Candidate(0, name.getText(), party.getText(), desc.getText(), 0));
            }
            loadCandidates();
        }
    }

    private void handleReset() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠ This will reset ALL votes and mark all voters as not voted.\nAre you sure?",
            "Reset Votes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            candidateDAO.resetAllVotes();
            loadCandidates(); loadVoters(); refreshStats();
            JOptionPane.showMessageDialog(this, "All votes have been reset.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadCandidates() {
        candidateModel.setRowCount(0);
        for (Candidate c : candidateDAO.getAllCandidates()) {
            candidateModel.addRow(new Object[]{c.getId(), c.getName(), c.getParty(), c.getDescription(), c.getVoteCount()});
        }
        refreshStats();
    }

    private void loadVoters() {
        voterModel.setRowCount(0);
        for (User u : userDAO.getAllVoters()) {
            voterModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.isHasVoted() ? "✅" : "❌"});
        }
    }

    private void refreshStats() {
        int total = voteDAO.getTotalVotesCast();
        lblStats.setText("Total Votes Cast: " + total + "  |  Admin: " + admin.getFullName() + "  ");
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(28);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setBackground(new Color(30, 50, 100));
        t.getTableHeader().setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(200, 220, 255));
        t.setGridColor(new Color(210, 220, 240));
        return t;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Selection Required", JOptionPane.WARNING_MESSAGE);
    }
}
