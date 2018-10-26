package cn.info;

import cn.info.bean.Episode;
import cn.info.bean.Video;
import cn.info.utils.DataTools;
import cn.info.utils.HtmlTools;
import cn.info.utils.HttpConnectionTools;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 用于爬取数据的任务
 * Created by Gao.WenLong on 2018/10/25.
 */
public class SpiderTask implements Runnable {

    /**
     * 在爬取数据时用来存储获取的链接(非重复的)
     */
    private Set<String> urls;

    /**
     * 用于存放所有的url，同时在处理时从队列中弹出
     */
    private LinkedBlockingQueue<String> queue;

    /**
     * 用于向线程池中添加任务
     */
    private ThreadPoolExecutor threadPool ;

    /**
     * @param urls 总的url集合
     * @param queue 需要处理的queue
     * @param threadPool
     */
    public SpiderTask(Set<String> urls,ThreadPoolExecutor threadPool,LinkedBlockingQueue<String> queue) {
       this.urls = urls;
       this.threadPool = threadPool;
       this.queue = queue;
    }

    /**
     * 处理爬虫爬取数据的逻辑
     */
    @Override
    public void run() {
        if(!queue.isEmpty()) {
            String url = queue.poll();
            Document document = HttpConnectionTools.document(url);
            if(null == document) { //如果获取连接失败则url返回队列等待下次执行
               queue.offer(url);
               return;
            }
            //提取页面上所有的url
            HtmlTools.urls(document,urls,queue);
            //提取页面上的视频信息,此处需要存到数据库
            if(DataTools.urlLevel(url) == 2) {
                Video video = HtmlTools.videoInfo(document);
                //TODO 此处需要存储
            }
            //提取页面上所有的视频源信息，此处需要更新到数据库
            if(DataTools.urlLevel(url) == 3) {
                List<Episode> list = HtmlTools.videoUrls(document);
                //TODO 此处需要存储
            }
            //TODO 判断队列中是否还要url，如果有继续向线程池添加任务
            if(!queue.isEmpty()) {
                threadPool.execute(new SpiderTask(urls,threadPool,queue));
            }
        }
    }
}
