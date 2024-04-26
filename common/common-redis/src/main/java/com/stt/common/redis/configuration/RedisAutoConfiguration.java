package com.stt.common.redis.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: shaott
 * @create: 2024-04-26 13:45
 * @Version 1.0
 **/
@Slf4j
@Configuration
@ComponentScan("com.stt.common.redis.configuration")
@EnableAutoConfiguration
public class RedisAutoConfiguration {

    @Autowired
    private RedisClusterConfigProperties clusterProperties;

    @Bean
    public RedisClusterConfiguration getClusterConfig() {
        RedisClusterConfiguration rcc = new RedisClusterConfiguration(clusterProperties.getNodes());
        rcc.setMaxRedirects(clusterProperties.getMaxAttempts());
        rcc.setPassword(RedisPassword.of(clusterProperties.getPassword()));
        return rcc;
    }

    @Bean
    public JedisCluster getJedisCluster() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 截取集群节点
        String[] cluster = clusterProperties.getNodes().toArray(new String[0]);
        // 创建set集合
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        // 循环数组把集群节点添加到set集合中
        for (String node : cluster) {
            String[] host = node.split(":");
            //添加集群节点
            nodes.add(new HostAndPort(host[0], Integer.parseInt(host[1])));
        }
        return new JedisCluster(nodes, clusterProperties.getConnectionTimeout(), clusterProperties.getSoTimeout(), clusterProperties.getMaxAttempts(), clusterProperties.getPassword(), poolConfig);
    }


    @Bean
    public JedisConnectionFactory redisConnectionFactory(RedisClusterConfiguration cluster) {
        return new JedisConnectionFactory(cluster);
    }


    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisClusterConfiguration cluster){
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(cluster);
        return lettuceConnectionFactory;
    }

    @Bean(name = "reactiveRedisTemplate")
    public ReactiveRedisTemplate reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        RedisSerializationContext.SerializationPair<String> stringSerializationPair = RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer.UTF_8);
        RedisSerializationContext.SerializationPair<Object> objectSerializationPair = RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json());
        RedisSerializationContext.RedisSerializationContextBuilder builder =
                RedisSerializationContext.newSerializationContext();
        builder.key(stringSerializationPair);
        builder.value(objectSerializationPair);
        builder.hashKey(stringSerializationPair);
        builder.hashValue(objectSerializationPair);
        builder.string(objectSerializationPair);
        RedisSerializationContext build = builder.build();
        ReactiveRedisTemplate reactiveRedisTemplate = new ReactiveRedisTemplate(reactiveRedisConnectionFactory, build);
        return reactiveRedisTemplate;
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        setSerializer(template); //设置序列化工具，这样ReportBean不需要实现Serializable接口
        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * @param template
     */
    private void setSerializer(RedisTemplate template) {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
    }


    @Bean
    @ConditionalOnProperty(value = "spring.redis.cluster.enable", havingValue = "true")
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(
                redisProperties.getCluster().getNodes());

        // https://github.com/lettuce-io/lettuce-core/wiki/Redis-Cluster#user-content-refreshing-the-cluster-topology-view
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh().enableAllAdaptiveRefreshTriggers().refreshPeriod(Duration.ofSeconds(5))
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions).build();

        // https://github.com/lettuce-io/lettuce-core/wiki/ReadFrom-Settings
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED).clientOptions(clusterClientOptions).build();

        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }
}
