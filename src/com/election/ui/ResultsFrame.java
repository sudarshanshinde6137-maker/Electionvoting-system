package com.election.ui;

import com.election.dao.CandidateDAO;
import com.election.dao.VoteDAO;
import com.election.model.Candidate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ResultsFrame extends JFrame {

    private CandidateDAO candidateDAO = new CandidateDAO();
    private VoteDAO voteDAO = new VoteDAO();

    public ResultsFrame() {
        setTitle("Election Results");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(15, 20, 40));

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(10, 15, 35));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel lbl = new JLabel("📊  LIVE ELECTION RESULTS", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(255, 215, 0));
        header.add(lbl);

        // Chart Panel
        List<Candidate> candidates = candidateDAO.getAllCandidates();
        int totalVotes = voteDAO.getTotalVotesCast();

        JPanel chartPanel = new BarChartPanel(candidates, totalVotes);
        chartPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        // Stats label
        JLabel lblTotal = new JLabel("Total Votes Cast: " + totalVotes, SwingConstants.CENTER);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(new Color(180, 210, 255));
        lblTotal.setBorder(new EmptyBorder(10, 0, 15, 0));

        JButton btnClose = new JButton("  Close  ");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(52, 73, 140));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(15, 20, 40));
        bottomPanel.add(lblTotal, BorderLayout.CENTER);
        JPanel btnWrapper = new JPanel();
        btnWrapper.setBackground(new Color(15, 20, 40));
        btnWrapper.add(btnClose);
        bottomPanel.add(btnWrapper, BorderLayout.SOUTH);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    // ── Custom bar chart ──
    static class BarChartPanel extends JPanel {
        private List<Candidate> candidates;
        private int totalVotes;
        private Color[] barColors = {
            new Color(52, 152, 219),
            new Color(39, 174, 96),
            new Color(230, 126, 34),
            new Color(155, 89, 182),
            new Color(231, 76, 60)
        };

        public BarChartPanel(List<Candidate> candidates, int totalVotes) {
            this.candidates = candidates;
            this.totalVotes = totalVotes;
            setBackground(new Color(15, 20, 40));
            setPreferredSize(new Dimension(600, 350));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int padLeft = 180, padRight = 60, padTop = 20, padBottom = 40;
            int chartW = w - padLeft - padRight;
            int chartH = h - padTop - padBottom;

            int maxVotes = candidates.stream().mapToInt(Candidate::getVoteCount).max().orElse(1);
            if (maxVotes == 0) maxVotes = 1;

            int barHeight = Math.min(36, (chartH / candidates.size()) - 10);
            int spacing   = (chartH - barHeight * candidates.size()) / (candidates.size() + 1);

            // Draw gridlines
            g2.setColor(new Color(40, 50, 80));
            for (int i = 0; i <= 5; i++) {
                int x = padLeft + (int)(chartW * i / 5.0);
                g2.drawLine(x, padTop, x, h - padBottom);
            }

            for (int i = 0; i < candidates.size(); i++) {
                Candidate c = candidates.get(i);
                int barW = (int) ((double) c.getVoteCount() / maxVotes * chartW);
                int y = padTop + spacing * (i + 1) + barHeight * i;

                // Bar
                g2.setColor(barColors[i % barColors.length]);
                g2.fillRoundRect(padLeft, y, Math.max(barW, 4), barHeight, 8, 8);

                // Candidate name (left)
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                String label = c.getName();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, padLeft - fm.stringWidth(label) - 8,
                    y + barHeight / 2 + fm.getAscent() / 2 - 2);

                // Vote count + % (right of bar)
                int pct = totalVotes == 0 ? 0 : (int)((double)c.getVoteCount() / totalVotes * 100);
                String info = c.getVoteCount() + " (" + pct + "%)";
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(200, 220, 255));
                g2.drawString(info, padLeft + barW + 8, y + barHeight / 2 + 5);
            }

            // X-axis labels
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(120, 150, 200));
            for (int i = 0; i <= 5; i++) {
                int val = (int)(maxVotes * i / 5.0);
                int x = padLeft + (int)(chartW * i / 5.0);
                String s = String.valueOf(val);
                g2.drawString(s, x - g2.getFontMetrics().stringWidth(s) / 2, h - padBottom + 18);
            }
        }
    }
}
