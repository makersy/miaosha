# 秒杀项目

## 介绍

实现了电商网站秒杀商品功能，采用 RabbitMQ 消息队列尝试支持高并发访问。采用多级缓存技术加快响应速度，降低资源消耗。

## 工具

使用IDEA作为开发工具。

## 项目使用技术

### 框架搭建

1. Spring Boot环境搭建
2. 集成了`Thymeleaf`模板框架，进行`Result`结果封装
3. 集成 Mybatis + Druid。mybatis有两种方式写mapper，一种是xml，一种直接用Java代码和注解，此项目使用了后者
4. 集成 Jedis + Redis 安装，自己实现了通用缓存Key封装

### 登录功能

1. 明文密码两次MD5处理
2. JSR303参数检验，全局异常处理器（仅处理了接口层面。没有页面情况）
3. 分布式session

### jmeter压测


### 页面优化技术

1. 页面缓存+URL缓存+对象缓存
2. 页面静态化，前后端分离
3. 静态资源优化
4. CDN优化

### 秒杀接口性能优化

1. Redis预减库存减少数据库访问

   思路：减少数据库访问

   1. 系统初始化，把商品库存数量加载到Redis
   2. 收到请求，Redis预减库存，库存不足，直接返回，否则进行3
   3. 异步下单。请求入队，立即返回：排队中
   4. 请求出队，生成订单，减少库存
   5. 客户端轮询，是否秒杀成功

2. 内存标记减少redis访问

3. RabbitMQ队列缓冲，异步下单，增强用户体验

4. RabbitMQ安装与SpringBoot集成

5. Nginx访问水平扩展

6. 压测

### 安全优化

1. 秒杀接口地址隐藏

   目的：秒杀地址动态获取。这样一来，秒杀开始前是不知道秒杀地址的。
   
   思路：秒杀开始之前，先去请求接口获取秒杀地址。
   
   1. 接口改造，带上`PathVariable`参数；
   2. 添加生成地址的接口；
   3. 秒杀收到请求，先验证`PathVariable`。

2. 数学公式验证码

   目的：设置图片验证码，防止机器人、刷票软件刷。同时避免请求太过于集中，分散之前可能非常集中的请求。
   
   思路：点击秒杀之前，先输入验证码，分散用户的请求。
   
   1. 添加生成验证码的接口
   2. 在获取秒杀路径的时候，验证验证码
   3. `ScriptEngine`使用

3. 接口限流防刷

   目的：防止恶意用户刷接口，在规定时间内只能访问此接口若干次。
   
   思路：对接口做限流。计时操作可以使用缓存实现。
   
   初始想法：
   
   将用户访问接口的uri联合用户id设置为一个key，放入redis中进行计数，设定有效时间为5s，然后每当用户访问接口时，就查询当前5s内已访问次数：如果第1次访问，那么set进redis；如果没有到5次，那么计数+1，继续执行下面的业务；如果超过5次，直接返回错误。
   
   进阶：
   
   这样做显然有一个缺点，就是这种写法只能用在当前接口，其他接口如果也需要做有限时间内的防刷处理，还需要各自不同的实现。为此，我们可以使用**注解**来处理，注解内传入参数如：有效时间，最大访问次数等等，可以灵活地处理需求。注解通过拦截器来处理。


### bug记录

1. 秒杀失败后，仍然生成了订单

   **出错代码：**

   地点在`MiaoshaService`中。

   ```java
   	@Transactional
   	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
   		//减库存
   		int res = goodsService.reduceStock(goods);
   		//生成并写入订单，返回生成的订单信息
   		return orderService.createOrder(user, goods);	
   	}
   ```

   **出错原因：**

   没有检查减库存是否成功就进行生成订单操作，因此每一次秒杀都生成了订单...这还秒杀个鬼，总共就1个东西，结果10万个人都秒杀成功了，那不是亏死。

   **改正：**

   ```java
   	@Transactional
   	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
   		//减库存
   		int res = goodsService.reduceStock(goods);
   		//生成并写入订单，返回生成的订单信息
   
   		if (res > 0) {
   			return orderService.createOrder(user, goods);
   		} else {
   			return null;
   		}
   	}
   ```

   加了一个判断，如果update语句影响了字段，即大于0，那么说明库存可减，订单生成；否则失败。

2. 生成订单的订单号均为1

   **出错代码：**

   错误地点在`OrderService`里面。

   ```java
   @Transactional
       public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
           //生成订单
           OrderInfo orderInfo = new OrderInfo();
           orderInfo.setCreateDate(new Date());
           orderInfo.setDeliveryAddrId(0L);
           orderInfo.setGoodsCount(1);
           orderInfo.setGoodsId(goods.getId());
           orderInfo.setGoodsName(goods.getGoodsName());
           orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
           orderInfo.setOrderChannel(1);
           orderInfo.setStatus(0);  //todo 最好用枚举，新建未支付
           orderInfo.setUserId(user.getId());
   
           //写入订单
           long id = orderDao.insert(orderInfo);
   
           //写入秒杀订单进缓存
           MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
           miaoshaOrder.setGoodsId(goods.getId());
           miaoshaOrder.setOrderId(id);  //这里出错了
           miaoshaOrder.setUserId(user.getId());
           orderDao.insertMiaoshaOrder(miaoshaOrder);
           redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);
   
           return orderInfo;
       }
   ```

   **出错原因：**

   这里insert返回的是插入成功的字段条数，而不是插入的订单的id号。因此生成的订单号只有1。我想当时可能是因为`Mybatis`的`@SelectKey`注解将我误导了。因为我的insert方法是这样写的：

   ```java
       @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
               + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
       @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before = false, statement="select last_insert_id()")  //将插入id赋给传入的model
       public long insert(OrderInfo orderInfo);
   ```

   用`SelectKey`是为了在插入订单数据成功之后，将插入成功的id赋给传入的`orderInfo`对象，结果误以为是将其作为返回值返回，因此出错。

   **改正：**

   ```java
       @Transactional
       public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
           //生成订单
           OrderInfo orderInfo = new OrderInfo();
           orderInfo.setCreateDate(new Date());
           orderInfo.setDeliveryAddrId(0L);
           orderInfo.setGoodsCount(1);
           orderInfo.setGoodsId(goods.getId());
           orderInfo.setGoodsName(goods.getGoodsName());
           orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
           orderInfo.setOrderChannel(1);
           orderInfo.setStatus(0);  //todo 最好用枚举，新建未支付
           orderInfo.setUserId(user.getId());
   
           //写入订单
           orderDao.insert(orderInfo);
   
           //写入秒杀订单进缓存
           MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
           miaoshaOrder.setGoodsId(goods.getId());
           miaoshaOrder.setOrderId(orderInfo.getId());  //从orderInfo中获取而不是用insert返回值获取
           miaoshaOrder.setUserId(user.getId());
           orderDao.insertMiaoshaOrder(miaoshaOrder);
           redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);
   
           return orderInfo;
       }
   
   ```

   3. `rabbitmq DeclarationException: Failed to declare queue`
   
      **出错原因：**
   
      `miaosha.queue`队列找不到，排查发现没有在`@Configuration`文件中配置该队列，
   
      **改正：**
   
      在配置类`com/makersy/rabbitmq/MQConfig`中添加上相关队列即可
   
      ```java
      @Bean
      public Queue miaoshaQueue() {
          return new Queue(MIAOSHA_QUEUE, true);
      }
      ```