from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import json
import time
import os
from selenium.webdriver.chrome.service import Service 

class LineTodayScraper:
    def __init__(self):
        base_dir = os.path.dirname(os.path.abspath(__file__))
        driver_path = os.path.join(base_dir, "chromedriver.exe")
        
        self.comments_dir = os.path.join(base_dir, "comments")
        if not os.path.exists(self.comments_dir):
            os.makedirs(self.comments_dir)
            
        service = Service(driver_path)
        self.driver = webdriver.Chrome(service=service)
        
    def start(self):
        self.driver.get("https://today.line.me/tw/v2/")
        print("已開啟 LINE TODAY")
        
    def get_comments(self, article_url):
        comment_url = article_url.replace('/article/', '/comment/article/')
        self.driver.get(comment_url)
        comments = []
        
        try:
            time.sleep(1)
            
            try:
                article_title = self.driver.find_element(By.CSS_SELECTOR, ".articleCard-content .header").text.strip()
            except:
                article_title = "無法獲取標題"
            
            try:
                title_element = WebDriverWait(self.driver, 10).until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, "h2.titlebar-text"))
                )
                comment_count = int(''.join(filter(str.isdigit, title_element.text)))
                print(f"總留言數: {comment_count}")
                
                if comment_count < 50:
                    print(f"留言數量不足50則，跳過抓取")
                    return None
            except Exception as e:
                print(f"無法獲取留言數量: {str(e)}")
                return None

            last_count = 0
            scroll_attempts = 0
            max_attempts = 10
            
            while scroll_attempts < max_attempts:
                self.driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
                time.sleep(3)
                
                current_count = len(self.driver.find_elements(By.CLASS_NAME, "commentItem"))
                if current_count == last_count:
                    scroll_attempts += 1
                else:
                    scroll_attempts = 0
                    last_count = current_count

            comment_elements = self.driver.find_elements(By.CLASS_NAME, "commentItem")
            
            for comment in comment_elements:
                try:
                    user = comment.find_element(By.CLASS_NAME, "commentItem-user").text.strip()
                    
                    content_spans = comment.find_elements(By.CSS_SELECTOR, ".commentItem-content > span")
                    if content_spans:
                        content = content_spans[0].text.strip()
                    else:
                        content = comment.find_elements(By.CSS_SELECTOR, ".commentItem-content span")[-1].text.strip()
                    
                    time_text = comment.find_element(By.CLASS_NAME, "commentItem-time").text.split("\n")[0].strip()
                    
                    likes_element = comment.find_elements(By.CSS_SELECTOR, ".commentItem-btnUp span")
                    likes = int(likes_element[-1].text or "0") if likes_element else 0
                    
                    dislikes_element = comment.find_elements(By.CSS_SELECTOR, ".commentItem-btnDown span")
                    dislikes = int(dislikes_element[-1].text or "0") if dislikes_element else 0
                    
                    comment_data = {
                        "user": user,
                        "content": content,
                        "time": time_text,
                        "likes": likes,
                        "dislikes": dislikes,
                        "replies": []
                    }
                    
                    reply_buttons = comment.find_elements(By.CSS_SELECTOR, ".commentItem-reply .commentItem-btn")
                    if reply_buttons:
                        try:
                            reply_count = int(''.join(filter(str.isdigit, reply_buttons[0].text)))
                            comment_data["reply_count"] = reply_count
                            
                            if reply_count > 0:
                                reply_buttons[0].click()
                                time.sleep(2)
                                
                                try:
                                    reply_items = WebDriverWait(comment, 5).until(
                                        EC.presence_of_all_elements_located((By.CSS_SELECTOR, ".replyItem"))
                                    )
                                    
                                    for reply in reply_items:
                                        try:
                                            reply_user = reply.find_element(By.CLASS_NAME, "replyItem-user").text.strip()
                                            reply_content = reply.find_element(By.CSS_SELECTOR, ".replyItem-content span").text.strip()
                                            reply_time = reply.find_element(By.CLASS_NAME, "replyItem-time").text.split("\n")[0].strip()

                                            reply_data = {
                                                "user": reply_user,
                                                "content": reply_content,
                                                "time": reply_time
                                            }
                                            comment_data["replies"].append(reply_data)
                                        except Exception as e:
                                            print(f"解析回覆內容時發生錯誤: {str(e)}")
                                            continue
                                except Exception as e:
                                    print(f"找不到回覆區塊或等待超時: {str(e)}")
                        except Exception as e:
                            print(f"處理回覆時發生錯誤: {str(e)}")
                    
                    comments.append(comment_data)
                except Exception as e:
                    print(f"解析留言時發生錯誤: {str(e)}")
                    continue

            if comments:
                result = {
                    "article_title": article_title,
                    "article_url": article_url,
                    "comment_url": comment_url,
                    "total_comments": comment_count,
                    "comments": comments
                }
                print(f"成功抓取 {len(comments)} 則留言")
                return result
            else:
                print("未能成功抓取任何留言")
                return None
            
        except Exception as e:
            print(f"取得留言時發生錯誤: {str(e)}")
            return None

    def monitor_url_changes(self):
        last_url = ''
        while True:
            try:
                try:
                    _ = self.driver.current_url
                except:
                    print("瀏覽器已關閉，程式結束")
                    return
                
                current_url = self.driver.current_url
                if (current_url != last_url and 
                    current_url.startswith('https://today.line.me/tw/v2/article') and
                    'comment' not in current_url):
                    print(f"檢測到新的LINE TODAY文章: {current_url}")
                    comments = self.get_comments(current_url)
                    if comments:
                        output_file = f"comments_{int(time.time())}.json"
                        self.save_to_json(comments, output_file)
                    last_url = current_url
                time.sleep(2)
            except Exception as e:
                print(f"監視URL時發生錯誤: {str(e)}")
                time.sleep(2)
                continue
    
    def save_to_json(self, comments, output_file):
        try:
            file_path = os.path.join(self.comments_dir, output_file)
            with open(file_path, 'w', encoding='utf-8') as f:
                json.dump(comments, f, ensure_ascii=False, indent=2)
            print(f"已將留言保存至 {file_path}")
        except Exception as e:
            print(f"保存檔案時發生錯誤: {str(e)}")
    
    def close(self):
        self.driver.quit()

def main():
    scraper = LineTodayScraper()
    try:
        scraper.start()
        print("開始監視瀏覽器中的LINE TODAY文章...")
        scraper.monitor_url_changes()
    except KeyboardInterrupt:
        print("\n程式結束")
    except Exception as e:
        print(f"發生錯誤: {str(e)}")
    finally:
        try:
            scraper.close()
        except:
            pass

if __name__ == "__main__":
    main()
