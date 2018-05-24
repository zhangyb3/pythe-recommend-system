//package com.pythe.controller;
//
//import java.io.File;
//import java.io.FileInputStream;
//
//import org.junit.Test;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import com.pythe.common.utils.FtpUtil;
//
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//
//public class JedisTest {
//
//			/**
//			 * test redis连接
//			 * @throws Exception
//			 */
//			@Test
//			public void testJedis() throws Exception {
//				//创建一个jedis的对象。
//				Jedis jedis = new Jedis("139.199.212.174", 6379);
//				//调用jedis对象的方法，方法名称和redis的命令一致。
//				//jedis.set("key1", "jedis test");
////				jedis.hset("key2", "jedis 热啊test","bb");
//				
//				//String string = jedis.get("key1");
//				String string = jedis.get("USER_SESSION"+":"+"oU6Xr0Iddiof1I7YFsioFTkwmJiU");
//				System.out.println(string);
//				//关闭jedis。
//				jedis.close();
//			}
//			
//			/**
//			 * 使用连接池
//			 */
//			@Test
//			public void testJedisPool() {
//				//创建jedis连接池
//				JedisPool pool = new JedisPool("192.168.1.13", 6379);
//				//从连接池中获得Jedis对象
//				Jedis jedis = pool.getResource();
//				String string = jedis.get("key1");
//				System.out.println(string);
//				//关闭jedis对象
//				jedis.close();
//				pool.close();
//			}
//			
//			/**
//			 * 集群版测试
//			 * <p>Title: testJedisCluster</p>
//			 * <p>Description: </p>
//			 */
//			//@Test
////			public void testJedisCluster() {
////				LOGGER.debug("调用redisCluster开始");
////				HashSet<HostAndPort> nodes = new HashSet<>();
////				nodes.add(new HostAndPort("192.168.25.153", 7001));
////				nodes.add(new HostAndPort("192.168.25.153", 7002));
////				nodes.add(new HostAndPort("192.168.25.153", 7003));
////				nodes.add(new HostAndPort("192.168.25.153", 7004));
////				nodes.add(new HostAndPort("192.168.25.153", 7005));
////				nodes.add(new HostAndPort("192.168.25.153", 7006));
////				LOGGER.info("创建一个JedisCluster对象");
////				JedisCluster cluster = new JedisCluster(nodes);
////				LOGGER.debug("设置key1的值为1000");
////				cluster.set("key1", "1000");
////				
////				LOGGER.debug("从Redis中取key1的值");
////				String string = cluster.get("key1");
////				System.out.println(string);
////				cluster.close();
////				try {
////					int a = 1/0;
////				} catch (Exception e) {
////					LOGGER.error("系统发送异常", e);
////				}
////			}
//			
//			/**
//			 * 单机版测试
//			 * <p>Title: testSpringJedisSingle</p>
//			 * <p>Description: </p>
//			 */
//			@Test
//			public void testSpringJedisSingle() {
//				ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
//				JedisPool pool = (JedisPool) applicationContext.getBean("redisClient");
//				Jedis jedis = pool.getResource();
//				//String string = jedis.get("key1");
//				String string = jedis.get("REGISTER_SUBJECTLIST_KEY");
//				System.out.println(string);
//				jedis.close();
//				pool.close();
//			}
//			
////			@Test
////			public void testSpringJedisCluster() {
////				ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
////				JedisCluster jedisCluster =  (JedisCluster) applicationContext.getBean("redisClient");
////				String string = jedisCluster.get("key1");
////				System.out.println(string);
////				jedisCluster.close();
////			}
//			
//			
//			/**
//			 * 单机版测试:删除
//			 * <p>Title: testSpringJedisSingle</p>
//			 * <p>Description: </p>
//			 */
//			@Test
//			public void testHdelJedisSingle() {
//				ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
//				JedisPool pool = (JedisPool) applicationContext.getBean("redisClient");
//				Jedis jedis = pool.getResource();
////				jedis.hdel("INDEX_CONTENT_REDIS_KEY", "yang");
////				String string = jedis.get("key1");
////				System.out.println(string);
//				jedis.close();
//				pool.close();
//			}
//			
//			
//			
//}
