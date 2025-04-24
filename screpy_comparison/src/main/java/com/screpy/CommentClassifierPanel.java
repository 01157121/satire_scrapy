package com.screpy;

import javax.swing.*;
import java.awt.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommentClassifierPanel extends JPanel {
    private JSONArray comments;
    private int idx = 0;
    private JLabel userLabel;
    private JTextPane contentPane;
    private JButton prevBtn;
    private JButton nextBtn;

    public CommentClassifierPanel(JSONArray comments) {
        this.comments = comments;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(480, 200));

        userLabel = new JLabel();
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane = new JTextPane();
        contentPane.setContentType("text/html");
        contentPane.setEditable(false);
        contentPane.setFont(new Font("SansSerif", Font.PLAIN, 15));
        contentPane.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(contentPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel navPanel = new JPanel(new BorderLayout());
        prevBtn = new JButton("←");
        nextBtn = new JButton("→");
        navPanel.add(prevBtn, BorderLayout.WEST);
        navPanel.add(nextBtn, BorderLayout.EAST);

        add(userLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);

        prevBtn.addActionListener(e -> {
            if (idx > 0) {
                idx--;
                updateComment();
            }
        });
        nextBtn.addActionListener(e -> {
            if (idx < comments.length() - 1) {
                idx++;
                updateComment();
            }
        });
        updateComment();
    }

    private void updateComment() {
        JSONObject cmt = comments.getJSONObject(idx);
        String user = cmt.optString("user", "");
        String content = cmt.optString("content", "");
        userLabel.setText(user);
        contentPane.setText("<html><body style='font-family:sans-serif;font-size:15px;padding:10px;'>" + content.replace("\n", "<br>") + "</body></html>");
        prevBtn.setEnabled(idx > 0);
        nextBtn.setEnabled(idx < comments.length() - 1);
    }
}
