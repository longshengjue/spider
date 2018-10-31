## tomato-tv
##### 目前存在的问题
- 爬虫去重问题(采用BloomFilter解决)
- 爬虫爬取更新数据的问题(没有好的方法，目前最好的方法是存储md5,然后对网页内容进行md5比对，然后指定更新频率)
- 爬虫中url的抓取顺序
- 爬取完的网页url也需要进行记录，同时记录这个网页的更新频率


##### 爬虫爬取流程
- 启动时加载数据库中的数据到redis中和内存队列中
- 每处理完一个链接，从队列和redis中分别删除链接
- 每爬取一个链接都需要到队列中去判断是否在队列中存在
- 如果不存在则分别放入队列和redis，目的是为了记录出爬虫异常中断时重启爬虫能够接着上次的执行
- 对需要解析的链接分别放入对应等级的队列中，开启对应的线程进行处理

##### 注意事项
- 每个链接和对应的文件都取md5值用来判断当前链接是否更新
- 队列中的数据处理完成后即可结束爬取
- 支持多种解析规则(适应多个网站)


