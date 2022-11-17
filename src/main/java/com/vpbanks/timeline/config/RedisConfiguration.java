package com.vpbanks.timeline.config;

import com.vpbanks.timeline.config.properties.RedisHostPortProperties;
import com.vpbanks.timeline.config.properties.RedisJedisPoolProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

	private final RedisHostPortProperties redisHostPortPrefixDto;
	private final RedisJedisPoolProperties redisJedisPoolPrefixDto;
	StringRedisSerializer stringRedisSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);

	@Bean
	public RedisConnectionFactory connectionFactory() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(redisJedisPoolPrefixDto.getMaxIdle());
		jedisPoolConfig.setMinIdle(redisJedisPoolPrefixDto.getMinIdle());
		jedisPoolConfig.setMaxTotal(redisJedisPoolPrefixDto.getMaxActive());
		jedisPoolConfig.setMaxWait(Duration.ofMillis(redisJedisPoolPrefixDto.getMaxWait()));
		jedisPoolConfig.setBlockWhenExhausted(redisJedisPoolPrefixDto.getBlockWhenExhausted());

		JedisClientConfiguration jedisClientConfiguration =
				JedisClientConfiguration.builder().usePooling().poolConfig(jedisPoolConfig).and().build();

		log.info("[connectionFactory] redis host: {}, port: {}", redisHostPortPrefixDto.getHost(), redisHostPortPrefixDto.getPort());
		RedisStandaloneConfiguration redisStandaloneConfiguration =
				new RedisStandaloneConfiguration(redisHostPortPrefixDto.getHost(), Integer.valueOf(redisHostPortPrefixDto.getPort()));

		if (StringUtils.isNotBlank(redisHostPortPrefixDto.getPassword())) {
			redisStandaloneConfiguration.setPassword(redisHostPortPrefixDto.getPassword());
		}
		return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory());
		redisTemplate.setDefaultSerializer(this.stringRedisSerializer);
		redisTemplate.setHashKeySerializer(this.stringRedisSerializer);
		redisTemplate.setKeySerializer(this.stringRedisSerializer);

		return redisTemplate;
	}
}
