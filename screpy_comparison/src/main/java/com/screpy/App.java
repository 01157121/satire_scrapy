package com.screpy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;
import com.screpy.IronyTypeDialog;

public class App {
    private static JSONArray articles = null;
    private static String loadedFileName = "";
    private static String comparingFilePath = null;

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("諷刺類別分類");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 650); // 增加初始視窗大小
        frame.setLayout(new BorderLayout());

        // 上方控制面板
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JLabel fileLabel = new JLabel("尚未載入檔案");
        controlPanel.add(fileLabel);

        JButton loadJsonButton = new JButton("載入 JSON");
        controlPanel.add(loadJsonButton);

        JButton continueJsonButton = new JButton("繼續做JSON");
        controlPanel.add(continueJsonButton);

        JButton showArticleButton = new JButton("顯示文章");
        showArticleButton.setEnabled(false);
        controlPanel.add(showArticleButton);

        JButton ironyTypeButton = new JButton("諷刺類別");
        controlPanel.add(ironyTypeButton);

        // 添加控制面板到框架頂部
        frame.add(controlPanel, BorderLayout.NORTH);

        // 中央留言顯示區初始化
        JPanel commentPanelContainer = new JPanel(new GridBagLayout());
        commentPanelContainer.setPreferredSize(new Dimension(1000, 600));
        
        JPanel commentPanel = new JPanel(new BorderLayout());
        commentPanel.setPreferredSize(new Dimension(900, 500));
        commentPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // 留言顯示區
        JLabel progressLabel = new JLabel("未載入留言");
        progressLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // 留言顯示區（改為多留言一覽）
        // 移除 prevBtn/nextBtn，改為留言列表
        JPanel commentsListPanel = new JPanel();
        commentsListPanel.setLayout(new BoxLayout(commentsListPanel, BoxLayout.Y_AXIS));
        JScrollPane commentsScrollPane = new JScrollPane(commentsListPanel);
        commentsScrollPane.setPreferredSize(new Dimension(880, 350));
        commentsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        // 讓scrollbar更好拉
        commentsScrollPane.getVerticalScrollBar().setUnitIncrement(24);
        commentsScrollPane.getVerticalScrollBar().setBlockIncrement(120);
        
        // 組裝留言面板
        commentPanel.removeAll();
        commentPanel.add(progressLabel, BorderLayout.NORTH);
        commentPanel.add(commentsScrollPane, BorderLayout.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        commentPanelContainer.add(commentPanel, gbc);
        
        // 將留言區添加到框架中央
        frame.add(commentPanelContainer, BorderLayout.CENTER);

        // 新增底部 panel 放置「下一篇文章」與「匯出」按鈕
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        frame.add(bottomPanel, BorderLayout.SOUTH);
        JButton nextArticleBtn = new JButton("下一篇文章");
        nextArticleBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        nextArticleBtn.setVisible(true);
        JButton exportBtn = new JButton("匯出JSON");
        exportBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        exportBtn.setVisible(false);
        JButton prevArticleBtn = new JButton("上一篇文章");
        prevArticleBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottomPanel.add(prevArticleBtn);
        bottomPanel.add(exportBtn);
        bottomPanel.add(nextArticleBtn);



        // 文章與留言切換流程
        final int[] articleIdx = {0};
        final int[] cmtIdx = {0};

        // 載入 JSON 按鈕動作
        loadJsonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        String content = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())), java.nio.charset.StandardCharsets.UTF_8);
                        
                        // 處理 JSON 內容，修正格式問題
                        content = content.replaceAll("(?m)^//.*$", "");
                        content = content.replaceAll("\\{\\s*\"time\"\\s*:\\s*\"([^\"]*)\"\\s*\\}", "{\"type\":\"\",\"user\":\"\",\"content\":\"\",\"time\":\"$1\"}");
                        content = content.replaceAll("}(\\s*)\\{\\s*\"type\"", "},$1{\"type\"");
                        content = content.replaceAll(",\\s*([\\]}])", "$1");
                        
                        JSONArray rawArticles = new JSONArray(content);
                        // 新建一份 comparingArticles，將所有留言 Satire 設為 0
                        JSONArray comparingArticles = new JSONArray();
                        for (int i = 0; i < rawArticles.length(); i++) {
                            JSONObject article = rawArticles.getJSONObject(i);
                            JSONObject newArticle = new JSONObject(article.toString()); // 深拷貝
                            // 第一篇文章加 now 標籤
                            if (i == 0) {
                                newArticle.put("now", true);
                            } else {
                                newArticle.remove("now");
                            }
                            if (newArticle.has("comments")) {
                                JSONArray comments = newArticle.getJSONArray("comments");
                                for (int j = 0; j < comments.length(); j++) {
                                    JSONObject cmt = comments.getJSONObject(j);
                                    cmt.put("Satire", 0);
                                    // 移除留言的 now 標籤
                                    cmt.remove("now");
                                }
                            }
                            comparingArticles.put(newArticle);
                        }
                        // 儲存 comparingArticles 為 _comparing.json
                        String comparingFileName = selectedFile.getAbsolutePath();
                        if (comparingFileName.toLowerCase().endsWith(".json")) {
                            comparingFileName = comparingFileName.substring(0, comparingFileName.length() - 5) + "_comparing.json";
                        } else {
                            comparingFileName = comparingFileName + "_comparing.json";
                        }
                        Files.write(Paths.get(comparingFileName), comparingArticles.toString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                        comparingFilePath = comparingFileName; // 記住 comparing 的完整路徑
                        // 之後都用 comparingArticles 作為主資料
                        articles = comparingArticles;
                        loadedFileName = new File(comparingFileName).getName();
                        fileLabel.setText(loadedFileName);
                        showArticleButton.setEnabled(true);
                        
                        // 顯示第一篇文章的留言
                        articleIdx[0] = 0;
                        cmtIdx[0] = 0;
                        App.showArticle(frame, commentPanelContainer, commentPanel, progressLabel, commentsListPanel, commentsScrollPane, nextArticleBtn, exportBtn, articles, articleIdx, cmtIdx);
                        
                        JOptionPane.showMessageDialog(frame, "JSON 載入成功");
                        commentPanelContainer.revalidate();
                        commentPanelContainer.repaint();
                        frame.revalidate();
                        frame.repaint();
                    } catch (Exception ex) {
                        // 錯誤處理
                        int errorPos = -1;
                        String errorContent = "";
                        try {
                            errorContent = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
                            String msg = ex.getMessage();
                            java.util.regex.Matcher m = java.util.regex.Pattern.compile("at (\\d+)").matcher(msg);
                            if (m.find()) {
                                errorPos = Integer.parseInt(m.group(1));
                                int start = Math.max(0, errorPos - 100);
                                int end = Math.min(errorContent.length(), errorPos + 100);
                                String snippet = errorContent.substring(start, end);
                                JOptionPane.showMessageDialog(frame, "錯誤位置附近內容：\n..." + snippet + "...", 
                                                            "錯誤行內容提示", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ignore) {}
                        
                        JOptionPane.showMessageDialog(frame, "讀取 JSON 文件時出錯: " + ex.getMessage(), 
                                                    "錯誤", JOptionPane.ERROR_MESSAGE);
                        showArticleButton.setEnabled(false);
                        System.out.println("Error reading JSON file: " + ex.getMessage());
                    }
                }
            }
        });

        // "繼續做JSON" 按鈕動作
        continueJsonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        String content = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())), java.nio.charset.StandardCharsets.UTF_8);
                        JSONArray loadedArticles = new JSONArray(content);
                        articles = loadedArticles;

                        // 檢查是不是繼續做的JSON（檢查文章有沒有 now 標籤）
                        boolean hasNow = false;
                        boolean hasSatire = false;
                        for (int i = 0; i < articles.length(); i++) {
                            JSONObject art = articles.getJSONObject(i);
                            if (art.has("now")) hasNow = true;
                            JSONArray cmts = art.optJSONArray("comments");
                            if (cmts != null) {
                                for (int j = 0; j < cmts.length(); j++) {
                                    JSONObject cmt = cmts.getJSONObject(j);
                                    if (cmt.has("Satire")) hasSatire = true;
                                }
                            }
                        }
                        if (!hasNow || !hasSatire) {
                            JOptionPane.showMessageDialog(frame, "這不是可繼續標註的JSON檔案（缺少 now 或 Satire 標籤）", "錯誤", JOptionPane.ERROR_MESSAGE);
                            showArticleButton.setEnabled(false);
                            return;
                        }

                        loadedFileName = selectedFile.getName();
                        fileLabel.setText(loadedFileName);
                        showArticleButton.setEnabled(true);

                        comparingFilePath = selectedFile.getAbsolutePath();

                        // 尋找 now 標籤所在的留言
                        int foundArticleIdx = 0;
                        int foundCmtIdx = 0;
                        outer:
                        for (int i = 0; i < articles.length(); i++) {
                            JSONObject art = articles.getJSONObject(i);
                            JSONArray cmts = art.optJSONArray("comments");
                            if (cmts != null) {
                                for (int j = 0; j < cmts.length(); j++) {
                                    JSONObject cmt = cmts.getJSONObject(j);
                                    if (cmt.has("now") && cmt.optBoolean("now", false)) {
                                        foundArticleIdx = i;
                                        foundCmtIdx = j;
                                        break outer;
                                    }
                                }
                            }
                        }
                        articleIdx[0] = foundArticleIdx;
                        cmtIdx[0] = foundCmtIdx;
                        App.showArticle(frame, commentPanelContainer, commentPanel, progressLabel, commentsListPanel, commentsScrollPane, nextArticleBtn, exportBtn, articles, articleIdx, cmtIdx);

                        JOptionPane.showMessageDialog(frame, "JSON 載入成功");
                        commentPanelContainer.revalidate();
                        commentPanelContainer.repaint();
                        frame.revalidate();
                        frame.repaint();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "讀取 JSON 文件時出錯: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                        showArticleButton.setEnabled(false);
                        System.out.println("Error reading JSON file: " + ex.getMessage());
                    }
                }
            }
        });

        // 顯示文章按鈕動作
        showArticleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (articles == null || articles.length() == 0) {
                    JOptionPane.showMessageDialog(frame, "尚未載入文章");
                    return;
                }
                // 依照目前選擇的文章索引顯示內容
                JSONObject article = articles.getJSONObject(articleIdx[0]);
                String author = article.optString("author", "");
                String title = article.optString("title", "");
                String date = article.optString("date", "");
                String content = article.optString("content", "");

                JTextPane contentPane = new JTextPane();
                contentPane.setContentType("text/html");
                contentPane.setText("<html><body style='font-family:sans-serif;font-size:12px;'>" + content.replace("\n", "<br>") + "</body></html>");
                contentPane.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(contentPane);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JPanel articlePanel = new JPanel();
                articlePanel.setLayout(new BorderLayout());
                JPanel infoPanel = new JPanel(new GridLayout(3, 1));
                infoPanel.add(new JLabel("作者: " + author));
                infoPanel.add(new JLabel("標題: " + title));
                infoPanel.add(new JLabel("日期: " + date));
                articlePanel.add(infoPanel, BorderLayout.NORTH);
                articlePanel.add(scrollPane, BorderLayout.CENTER);

                JOptionPane.showMessageDialog(frame, articlePanel, "文章內容", JOptionPane.PLAIN_MESSAGE);
            }
        });

        // 諷刺類別按鈕動作
        ironyTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IronyTypeDialog.showDialog(frame);
            }
        });

        // 上一篇文章按鈕事件
        prevArticleBtn.addActionListener(ev -> {
            if (articleIdx[0] == 0) {
                JOptionPane.showMessageDialog(frame, "已經是第一篇文章");
                return;
            }
            articleIdx[0]--;
            cmtIdx[0] = 0;
            showArticle(frame, commentPanelContainer, commentPanel, progressLabel, commentsListPanel, commentsScrollPane, nextArticleBtn, exportBtn, articles, articleIdx, cmtIdx);
        });

        // 顯示框架
        frame.setVisible(true);
    }

    // --- 靜態 showArticle 方法 ---
    public static void showArticle(JFrame frame, JPanel commentPanelContainer, JPanel commentPanel, JLabel progressLabel, JPanel commentsListPanel, JScrollPane commentsScrollPane, JButton nextArticleBtn, JButton exportBtn, JSONArray articles, int[] articleIdx, int[] cmtIdx) {
        // 記錄scrollbar位置
        int scrollValue = commentsScrollPane.getVerticalScrollBar().getValue();
        commentPanelContainer.removeAll();
        if (articles == null || articleIdx[0] >= articles.length()) return;
        JSONObject article = articles.getJSONObject(articleIdx[0]);
        
        // 顯示當前文章的資訊和內容
        String author = article.optString("author", "未知作者");
        String title = article.optString("title", "無標題");
        String date = article.optString("date", "");
        String articleContent = article.optString("content", "無內容");
        
        // 創建文章顯示面板
        JPanel articleDisplayPanel = new JPanel(new BorderLayout());
        articleDisplayPanel.setPreferredSize(new Dimension(900, 200));
        articleDisplayPanel.setBorder(BorderFactory.createTitledBorder("文章內容"));
        
        // 文章標題和作者信息
        JPanel articleInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("<html><b>標題:</b> " + title + " | <b>作者:</b> " + author + " | <b>日期:</b> " + date + "</html>");
        articleInfoPanel.add(titleLabel);
        articleDisplayPanel.add(articleInfoPanel, BorderLayout.NORTH);
        
        // 文章內容
        JTextPane articleContentPane = new JTextPane();
        articleContentPane.setContentType("text/html");
        articleContentPane.setText("<html><body style='font-family:sans-serif;font-size:14px;padding:10px;'>" 
                                  + articleContent.replace("\n", "<br>") + "</body></html>");
        articleContentPane.setEditable(false);
        articleContentPane.setBackground(new Color(250, 250, 250));
        
        JScrollPane articleScrollPane = new JScrollPane(articleContentPane);
        articleScrollPane.setBorder(BorderFactory.createEmptyBorder());
        articleScrollPane.setPreferredSize(new Dimension(880, 150));
        articleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        articleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        articleDisplayPanel.add(articleScrollPane, BorderLayout.CENTER);
        
        // 文章相關信息顯示
        JLabel articleIndexLabel = new JLabel("文章 " + (articleIdx[0] + 1) + " / " + articles.length());
        articleIndexLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        articleDisplayPanel.add(articleIndexLabel, BorderLayout.SOUTH);
        
        // 添加文章面板到容器
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        commentPanelContainer.add(articleDisplayPanel, gbc);
        
        // 留言相關代碼
        JSONArray comments = article.optJSONArray("comments");
        java.util.List<JSONObject> filtered = new java.util.ArrayList<>();
        if (comments != null) {
            for (int i = 0; i < comments.length(); i++) {
                JSONObject cmt = comments.getJSONObject(i);
                if ((cmt.has("user") && !cmt.optString("user", "").isEmpty()) ||
                    (cmt.has("content") && !cmt.optString("content", "").isEmpty())) {
                    if (!cmt.has("Satire")) cmt.put("Satire", 0);
                    filtered.add(cmt);
                }
            }
        }
        if (filtered.size() == 0) {
            JPanel noCommentsPanel = new JPanel(new BorderLayout());
            noCommentsPanel.setPreferredSize(new Dimension(900, 300));
            JLabel noCommentsLabel = new JLabel("此文章沒有可顯示的留言");
            noCommentsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noCommentsLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            noCommentsPanel.add(noCommentsLabel, BorderLayout.CENTER);
            
            gbc.gridy = 1;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            commentPanelContainer.add(noCommentsPanel, gbc);
            
            nextArticleBtn.setVisible(true);
            nextArticleBtn.setEnabled(true);
            exportBtn.setVisible(false);
            
            commentPanelContainer.revalidate();
            commentPanelContainer.repaint();
            return;
        }
        
        // 計算已標記留言數/總留言數，並統計有效諷刺句數
        int total = filtered.size();
        int done = 0;
        int satireCount = 0; // 有效諷刺句數
        for (JSONObject cmt : filtered) {
            String satireStr = cmt.has("Satire") ? cmt.get("Satire").toString() : "";
            if (satireStr != null && !satireStr.isEmpty() && !satireStr.equals("0") && !satireStr.equals(0)) done++;
            // 有效諷刺句：已標記且不含 x（不是諷刺）
            if (satireStr != null && !satireStr.isEmpty() && !satireStr.equals("0") && !satireStr.equals(0) && !satireStr.contains("x")) {
                satireCount++;
            }
        }
        progressLabel.setText("已標記 " + done + "/" + total + "，有效諷刺句：" + satireCount);
        // 若有效諷刺句達 20，彈窗提醒（每篇文章只提醒一次）
        if (satireCount == 20 && !article.has("satire20Notified")) {
            JOptionPane.showMessageDialog(frame, "本篇文章已標記 20 個有效諷刺句子！");
            article.put("satire20Notified", true);
            try {
                String savePath = comparingFilePath != null ? comparingFilePath : loadedFileName;
                Files.write(Paths.get(savePath), articles.toString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception ex) {
                // 可忽略自動儲存失敗
            }
        }

        // 設置留言面板位置
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        commentPanelContainer.add(commentPanel, gbc);
        
        commentsListPanel.removeAll();
        // 調整分類顏色與名稱，新增三個分類，"不是諷刺"為最後一個
        Color[] satireColors = {
            new Color(255,200,200), // 反話
            new Color(200,255,200), // 挪用
            new Color(200,200,255), // 誇張/縮小
            new Color(255,255,200), // 提問
            new Color(255,180,255), // 正諷
            new Color(180,255,255), // 第三者諷刺
            new Color(255,220,180), // 比喻Metaphor
            new Color(220, 200, 255), // 模仿
            new Color(255,150,200), // 揶揄
            new Color(180,180,180)  // 不是諷刺（最後一個）
        };
        String[] satireNames = {
            "A.反話", "B.挪用", "C.誇張/縮小", "D.提問",
            "E.正諷", "F.第三者", "G.比喻", "H.模仿", "I.揶揄", "不是諷刺"
        };
        int satireBtnCount = satireNames.length;
        for (int idx = 0; idx < filtered.size(); idx++) {
            JSONObject cmt = filtered.get(idx);
            JPanel commentItemPanel = new JPanel(new BorderLayout());
            // 將留言框高度縮短一點點（原本預設padding 5,5,5,5，改為 2,5,2,5）
            commentItemPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            // 問號按鈕
            JButton questionBtn = new JButton("?");
            questionBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            questionBtn.setMargin(new Insets(2, 6, 2, 6));
            boolean isQuestion = cmt.has("question") && cmt.optBoolean("question", false);
            questionBtn.setBackground(isQuestion ? Color.YELLOW : Color.LIGHT_GRAY);
            questionBtn.addActionListener(e -> {
                boolean nowQ = cmt.has("question") && cmt.optBoolean("question", false);
                cmt.put("question", !nowQ);
                questionBtn.setBackground(!nowQ ? Color.YELLOW : Color.LIGHT_GRAY);
                try {
                    String savePath = comparingFilePath != null ? comparingFilePath : loadedFileName;
                    Files.write(Paths.get(savePath), articles.toString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "自動儲存失敗: " + ex.getMessage());
                }
            });
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            leftPanel.add(questionBtn);
            JLabel userLabelCmt = new JLabel(cmt.optString("user", "未知用戶"));
            userLabelCmt.setFont(new Font("SansSerif", Font.BOLD, 14));
            leftPanel.add(userLabelCmt);
            JTextPane commentContentPane = new JTextPane();
            commentContentPane.setContentType("text/html");
            commentContentPane.setText("<html><body style='font-family:sans-serif;font-size:14px;'>"
                                      + cmt.optString("content", "").replace("\n", "<br>") + "</body></html>");
            commentContentPane.setEditable(false);
            commentContentPane.setBackground(new Color(245, 245, 245));
            // 判斷留言長度，若超過閾值則只推該行按鈕
            boolean isLong = cmt.optString("content", "").length() > 60;
            // 留言內容區域（可自動換行，超長自帶scrollbar）
            JScrollPane commentScrollPane = null;
            if (isLong) {
                commentContentPane.setPreferredSize(new Dimension(500, 48)); // 原本 60，縮短一點
                commentContentPane.setMinimumSize(new Dimension(200, 32));   // 原本 40，縮短一點
                commentContentPane.setMaximumSize(new Dimension(800, 90));   // 原本 120，縮短一點
                commentContentPane.setEditable(false);
                commentScrollPane = new JScrollPane(commentContentPane);
                commentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                commentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                commentScrollPane.setPreferredSize(new Dimension(500, 48)); // 原本 60，縮短一點
            } else {
                // 短留言也縮短高度
                commentContentPane.setPreferredSize(new Dimension(500, 32));
                commentContentPane.setMinimumSize(new Dimension(200, 24));
                commentContentPane.setMaximumSize(new Dimension(800, 48));
            }
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            // 揣測按鈕
            JButton thinkingBtn = new JButton("揣測");
            thinkingBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
            thinkingBtn.setMargin(new Insets(2, 8, 2, 8));
            boolean isThinking = cmt.has("thinking") && cmt.optBoolean("thinking", false);
            thinkingBtn.setBackground(isThinking ? Color.ORANGE : Color.LIGHT_GRAY);
            thinkingBtn.addActionListener(e -> {
                boolean nowT = cmt.has("thinking") && cmt.optBoolean("thinking", false);
                cmt.put("thinking", !nowT);
                thinkingBtn.setBackground(!nowT ? Color.ORANGE : Color.LIGHT_GRAY);
                try {
                    String savePath = comparingFilePath != null ? comparingFilePath : loadedFileName;
                    Files.write(Paths.get(savePath), articles.toString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "自動儲存失敗: " + ex.getMessage());
                }
            });
            btnPanel.add(thinkingBtn);
            // 分類checkbox按鈕
            JCheckBox[] satireChecks = new JCheckBox[satireBtnCount];
            // 解析現有分類
            String satireStr = cmt.has("Satire") ? cmt.get("Satire").toString() : "";
            java.util.Set<Integer> satireSet = new java.util.HashSet<>();
            boolean isNotSatire = false;
            for (char ch : satireStr.toCharArray()) {
                if (ch == 'x') {
                    isNotSatire = true;
                } else {
                    int v = ch - '0';
                    if (v > 0 && v <= satireBtnCount) satireSet.add(v);
                }
            }
            for (int i = 0; i < satireBtnCount; i++) {
                final int satireValue = i + 1;
                JCheckBox check = new JCheckBox(satireNames[i]);
                check.setFont(new Font("SansSerif", Font.BOLD, 13));
                check.setBackground(satireColors[i]);
                check.setForeground(Color.BLACK);
                if (i == satireBtnCount - 1) {
                    check.setSelected(isNotSatire);
                } else {
                    check.setSelected(satireSet.contains(satireValue));
                }
                satireChecks[i] = check;
                check.addActionListener(ev -> {
                    // 處理不是諷刺（只能單選）
                    if (satireValue == satireBtnCount) {
                        if (check.isSelected()) {
                            for (int j = 0; j < satireBtnCount - 1; j++) satireChecks[j].setSelected(false);
                        }
                    } else {
                        if (check.isSelected()) {
                            satireChecks[satireBtnCount-1].setSelected(false);
                        }
                    }
                    // 更新 Satire 欄位
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < satireBtnCount; j++) {
                        if (satireChecks[j].isSelected()) {
                            if (j == satireBtnCount - 1) {
                                sb.append("x"); // 不是諷刺用 x
                            } else {
                                sb.append(j+1);
                            }
                        }
                    }
                    cmt.put("Satire", sb.toString());
                    try {
                        String savePath = comparingFilePath != null ? comparingFilePath : loadedFileName;
                        Files.write(Paths.get(savePath), articles.toString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "自動儲存失敗: " + ex.getMessage());
                    }
                    showArticle(frame, commentPanelContainer, commentPanel, progressLabel, commentsListPanel, commentsScrollPane, nextArticleBtn, exportBtn, articles, articleIdx, cmtIdx);
                });
                btnPanel.add(check);
            }
            // 若留言過長，btnPanel加大左邊間距
            if (isLong) btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 0)); // 原本 80，縮短一點
            // 標記顏色（多選時以條紋顯示多色）
            java.util.List<Color> selectedColors = new java.util.ArrayList<>();
            for (int i = 0; i < satireBtnCount; i++) {
                if (i == satireBtnCount - 1) {
                    if (isNotSatire) selectedColors.add(satireColors[i]);
                } else {
                    if (satireSet.contains(i+1)) selectedColors.add(satireColors[i]);
                }
            }
            JPanel coloredPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (!selectedColors.isEmpty()) {
                        int w = getWidth();
                        int h = getHeight();
                        int n = selectedColors.size();
                        int segW = w / n;
                        for (int i = 0; i < n; i++) {
                            g.setColor(selectedColors.get(i));
                            g.fillRect(i * segW, 0, (i == n-1 ? w - i*segW : segW), h);
                        }
                    }
                }
            };
            coloredPanel.setLayout(new BorderLayout());
            coloredPanel.add(leftPanel, BorderLayout.WEST);
            if (isLong && commentScrollPane != null) {
                coloredPanel.add(commentScrollPane, BorderLayout.CENTER);
            } else {
                coloredPanel.add(commentContentPane, BorderLayout.CENTER);
            }
            coloredPanel.add(btnPanel, BorderLayout.EAST);
            coloredPanel.setBorder(commentItemPanel.getBorder());
            // 讓每則留言 panel 高度再短一點
            coloredPanel.setPreferredSize(new Dimension(860, isLong ? 54 : 38)); // 原本約 60/40，縮短一點
            commentsListPanel.add(coloredPanel);
        }
        
        commentPanelContainer.revalidate();
        commentPanelContainer.repaint();
        // 恢復scrollbar位置
        SwingUtilities.invokeLater(() -> commentsScrollPane.getVerticalScrollBar().setValue(scrollValue));
        // 下一篇文章按鈕事件
        nextArticleBtn.setVisible(true);
        for (ActionListener al : nextArticleBtn.getActionListeners()) nextArticleBtn.removeActionListener(al);
        nextArticleBtn.addActionListener(ev -> {
            // 將所有文章的 now 標籤移除，並將 now 標籤加到下一篇
            for (int i = 0; i < articles.length(); i++) {
                JSONObject art = articles.getJSONObject(i);
                JSONArray cmts = art.optJSONArray("comments");
                if (cmts != null) {
                    for (int j = 0; j < cmts.length(); j++) {
                        cmts.getJSONObject(j).remove("now");
                    }
                }
            }
            if (articleIdx[0] + 1 < articles.length()) {
                JSONObject nextArt = articles.getJSONObject(articleIdx[0] + 1);
                JSONArray nextCmts = nextArt.optJSONArray("comments");
                if (nextCmts != null && nextCmts.length() > 0) {
                    nextCmts.getJSONObject(0).put("now", true);
                }
            }
            // 存檔
            try {
                String savePath = comparingFilePath != null ? comparingFilePath : loadedFileName;
                Files.write(Paths.get(savePath), articles.toString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "自動儲存失敗: " + ex.getMessage());
            }
            articleIdx[0]++;
            cmtIdx[0] = 0;
            if (articleIdx[0] < articles.length()) {
                showArticle(frame, commentPanelContainer, commentPanel, progressLabel, commentsListPanel, commentsScrollPane, nextArticleBtn, exportBtn, articles, articleIdx, cmtIdx);
            } else {
                nextArticleBtn.setVisible(false);
                exportBtn.setVisible(true);
            }
        });
        // 匯出按鈕事件
        exportBtn.setVisible(false);
        for (ActionListener al : exportBtn.getActionListeners()) exportBtn.removeActionListener(al);
        exportBtn.addActionListener(ev -> {
            // 直接存成 compared，存到 comparingFilePath 的同一個資料夾
            String exportName = loadedFileName;
            String exportPath = comparingFilePath != null ? comparingFilePath : loadedFileName;
            if (exportName != null && !exportName.isEmpty()) {
                if (exportName.toLowerCase().endsWith("_comparing.json")) {
                    exportName = exportName.substring(0, exportName.length() - 13) + "_compared.json";
                } else if (exportName.toLowerCase().endsWith(".json")) {
                    exportName = exportName.substring(0, exportName.length() - 5) + "_compared.json";
                } else {
                    exportName = exportName + "_compared.json";
                }
            }
            // 取 comparingFilePath 的資料夾
            String exportFullPath = exportPath;
            if (exportPath != null && exportPath.contains(File.separator)) {
                String dir = exportPath.substring(0, exportPath.lastIndexOf(File.separator) + 1);
                exportFullPath = dir + exportName;
            } else {
                exportFullPath = exportName;
            }
            File out = new File(exportFullPath);
            try {
                Files.write(out.toPath(), articles.toString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                JOptionPane.showMessageDialog(frame, "已匯出成 " + exportFullPath + "，請確認檔案位置");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "匯出失敗: " + ex.getMessage());
            }
        });
    }
}
