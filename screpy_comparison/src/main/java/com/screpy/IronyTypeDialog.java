package com.screpy;

import javax.swing.*;
import java.awt.*;

public class IronyTypeDialog {
    public static void showDialog(Component parent) {
        JTabbedPane tabbedPane = new JTabbedPane();

        // A. 反話（Verbal Irony）
        JTextPane aPane = new JTextPane();
        aPane.setContentType("text/html");
        aPane.setText("<html><body style='font-family:sans-serif;font-size:13px;'><b>A. 反話（Verbal Irony）</b><br><br>"
            + "<b>定義：</b><br>使用與實際意思相反的語言來表達諷刺、嘲笑或批評，使話語具有雙重含義。<br><br>"
            + "<b>語言特徵：</b><ul><li>表面上是稱讚或中立語言，但實際上是批評或嘲諷。</li><li>語境與語氣決定真正的含義，常帶有戲謔、諷刺、挖苦意味。</li></ul>"
            + "<b>功能：</b><ul><li>強調矛盾，讓對方察覺錯誤。</li><li>製造幽默感，緩和嚴肅氣氛。</li></ul>"
            + "<b>使用情境：</b><ul><li>「你真是個天才！」（當對方犯了低級錯誤時）。</li><li>「這真是最棒的天氣了！」（當外面狂風暴雨時）。</li></ul></body></html>");
        aPane.setEditable(false);
        tabbedPane.addTab("A. 反話", new JScrollPane(aPane));

        // B. 挪用（Satirical Juxtaposition / Contextual Incongruity）
        JTextPane bPane = new JTextPane();
        bPane.setContentType("text/html");
        bPane.setText("<html><body style='font-family:sans-serif;font-size:13px;'><b>B. 挪用（Satirical Juxtaposition / Contextual Incongruity）</b><br><br>"
            + "<b>定義：</b><br>用與某事件無關但類似的事件來影射該事件，透過比較來揭露荒謬性。<br><br>"
            + "<b>語言特徵：</b><ul><li>使用兩件事情的對比來達到批評效果。</li><li>讓聽者自行察覺隱含的諷刺意味。</li></ul>"
            + "<b>功能：</b><ul><li>透過對比顯示矛盾或雙重標準。</li><li>讓諷刺更隱晦，不直接攻擊。</li></ul>"
            + "<b>使用情境：</b><ul><li>「政府說這次的政策會帶來經濟繁榮，就像上次他們說疫情不會影響經濟一樣。」</li><li>「這次比賽結果公平得就像我小時候媽媽讓我和哥哥公平分蛋糕一樣，他切，我選。」</li></ul></body></html>");
        bPane.setEditable(false);
        tabbedPane.addTab("B. 挪用", new JScrollPane(bPane));

        // C. 誇張或縮小（Hyperbole & Understatement）
        JTextPane cPane = new JTextPane();
        cPane.setContentType("text/html");
        cPane.setText("<html><body style='font-family:sans-serif;font-size:13px;'><b>C. 誇張或縮小（Hyperbole & Understatement）</b><br><br>"
            + "<b>定義：</b><br>使用極端誇大的語言或刻意淡化事實來達到諷刺效果。<br><br>"
            + "<b>誇張（Hyperbole）語言特徵：</b><ul><li>使用極端詞語來放大問題的嚴重性。</li><li>通常帶有幽默成分。</li></ul>"
            + "<b>誇張功能：</b><ul><li>強調問題的荒謬性。</li><li>吸引注意，讓聽者思考事件的真實情況。</li></ul>"
            + "<b>誇張使用情境：</b><ul><li>「這是我人生中見過最愚蠢的決定！」</li><li>「這條馬路的紅燈應該是全世界最長的！」</li></ul>"
            + "<b>縮小（Understatement）語言特徵：</b><ul><li>用冷靜或輕描淡寫的語氣來弱化事件的重要性。</li><li>使聽者自己察覺反差，進而感受到諷刺。</li></ul>"
            + "<b>縮小功能：</b><ul><li>反向強調事件的嚴重性或荒謬性。</li><li>讓批評更為隱晦，避免直接攻擊。</li></ul>"
            + "<b>縮小使用情境：</b><ul><li>「哦，這只是個小擦傷而已。」（當實際上傷勢嚴重時）</li><li>「嗯，這次的災難只影響了幾百萬人，沒什麼大不了的。」（當事件其實極具破壞性時）</li></ul></body></html>");
        cPane.setEditable(false);
        tabbedPane.addTab("C. 誇張/縮小", new JScrollPane(cPane));

        // D. 提問（Rhetorical Question）
        JTextPane dPane = new JTextPane();
        dPane.setContentType("text/html");
        dPane.setText("<html><body style='font-family:sans-serif;font-size:13px;'><b>D. 提問（Rhetorical Question）</b><br><br>"
            + "<b>定義：</b><br>提出一個明知答案的問題，讓聽者自行體會其中的諷刺意味。<br><br>"
            + "<b>語言特徵：</b><ul><li>語氣帶有反諷，並非真的尋求回答。</li><li>問題的答案通常是顯而易見的。</li></ul>"
            + "<b>功能：</b><ul><li>強調矛盾或愚蠢的行為。</li><li>讓聽者自行思考，增加說服力。</li></ul>"
            + "<b>使用情境：</b><ul><li>「你真的覺得這是一個好主意嗎？」（當對方做了一件明顯愚蠢的事）</li><li>「我們的交通這麼順暢，為什麼還要抱怨塞車呢？」（當實際上塞車嚴重時）</li></ul></body></html>");
        dPane.setEditable(false);
        tabbedPane.addTab("D. 提問", new JScrollPane(dPane));

        // E. 正諷（Logical Sarcasm / 順邏輯式諷刺）
        JTextPane ePane = new JTextPane();
        ePane.setContentType("text/html");
        ePane.setText("<html><body style='font-family:sans-serif;font-size:13px;'><b>E. 正諷（Logical Sarcasm / 順邏輯式諷刺）</b><br><br>"
            + "<b>定義：</b><br>順著對方的邏輯或說法繼續推論到底，表面看似支持，實則透過邏輯的極端或矛盾讓荒謬性自我顯現，達到諷刺目的。<br><br>"
            + "<b>語言特徵：</b><ul>"
            + "<li>話語表面『合理』，但實際在推論出荒謬結論。</li>"
            + "<li>多用『照你這樣說…』、『乾脆…』這類語氣詞。</li>"
            + "<li>假裝支持，實際用極端推論打臉。</li>"
            + "</ul>"
            + "<b>功能：</b><ul>"
            + "<li>讓對方的論點『自己崩壞』。</li>"
            + "<li>增加語言幽默與批判力度而不顯攻擊性。</li>"
            + "</ul>"
            + "<b>使用情境：</b><ul>"
            + "<li>『既然你說多開會能提升效率，那我們每天都開一整天好了！』</li>"
            + "<li>『你說聲音最重要，那乾脆影片只放聲音就好，畫面都拿掉。』</li>"
            + "</ul></body></html>");
        ePane.setEditable(false);
        tabbedPane.addTab("E. 正諷", new JScrollPane(ePane));

        // F. 第三者諷刺（Third-Party Sarcasm）
        JTextPane fPane = new JTextPane();
        fPane.setContentType("text/html");
        fPane.setText("<html><body style='font-family:sans-serif;font-size:13px;'><b>F. 第三者諷刺（Third-Party Sarcasm）</b><br><br>"
            + "<b>定義：</b><br>將諷刺對象指向他人、群體或機構，透過各種修辭手法對外界現象進行嘲諷或批判，常見於社群評論、新聞討論等語境中。<br><br>"
            + "<b>語言特徵：</b><ul>"
            + "<li>批評對象明確是『他人』或『體制』。</li>"
            + "<li>帶有不屑、嘲弄或強烈情緒色彩。</li>"
            + "<li>常伴隨誇張、反話、模仿、提問等修辭。</li>"
            + "</ul>"
            + "<b>功能：</b><ul>"
            + "<li>揭露第三方的矛盾、無能或雙標。</li>"
            + "<li>借批評他人來引發共鳴或群體幽默。</li>"
            + "</ul>"
            + "<b>使用情境：</b><ul>"
            + "<li>『政府的防詐機制做得真好，騙子都快感動了。』</li>"
            + "<li>『這公司效率高到，拖三個月才開會解決上週的事。』</li>"
            + "</ul></body></html>");
        fPane.setEditable(false);
        tabbedPane.addTab("F. 第三者諷刺", new JScrollPane(fPane));

        // G. 比喻諷刺（Personified Metaphorical Satire）
        JTextPane mPane = new JTextPane();
mPane.setContentType("text/html");
mPane.setText("<html><body style='font-family:sans-serif;font-size:13px;'>"
    + "<b>G. 比喻諷刺（Metaphorical Satire）</b><br><br>"
    + "<b>定義：</b><br>透過將人物、事件或現象比喻為其他角色、物品、動作或場景，以象徵或隱喻方式表達對其荒謬、不合理或矛盾特質的批評。這類諷刺通常不直接點出批評，而是以形象化語言激發讀者自行聯想與思考。<br><br>"
    + "<b>語言特徵：</b><ul>"
    + "<li>運用『像』、『彷彿是』、『技能』、『模式』、『職業』、『動物』等比喻性語彙。</li>"
    + "<li>常見動漫、遊戲、商品、職場、自然現象等文化化參照。</li>"
    + "<li>文字具畫面感，語氣偏輕鬆、戲謔、甚至荒謬。</li>"
    + "</ul>"
    + "<b>功能：</b><ul>"
    + "<li>提升諷刺的趣味性與記憶度，使批評具象化。</li>"
    + "<li>透過間接的描述強化語言穿透力，避免直接攻擊卻更具影響力。</li>"
    + "</ul>"
    + "<b>使用情境：</b><ul>"
    + "<li>『法院發動技能：躲閃飄，所有指控完美 Miss。』</li>"
    + "<li>『整間公司像台 Windows 98，一點就當機。』</li>"
    + "<li>『這場會議根本大逃殺，活下來的都不是人。』</li>"
    + "<li>『他的發言像老式印表機，一卡紙就整段錯字。』</li>"
    + "</ul></body></html>");
mPane.setEditable(false);
tabbedPane.addTab("G. 比喻諷刺", new JScrollPane(mPane));


        tabbedPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(parent, tabbedPane, "諷刺類別說明", JOptionPane.PLAIN_MESSAGE);
    }
}
