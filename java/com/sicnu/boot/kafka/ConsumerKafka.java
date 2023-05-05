package com.sicnu.boot.kafka;

import com.sicnu.boot.service.impl.HomeServiceImpl;
import com.sicnu.boot.utils.RedisUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * description:  Kafka消费类
 *
 * @author :  胡建华
 * Data:    2023/01/09 9:19
 */
@Configuration
public class ConsumerKafka {

    @Resource
    private RedisUtils redisUtils;

    @KafkaListener(topics = KafkaTopic.KAFKA_TOPIC)
    public void consumerKafkaTopic(String msg){
        if (msg.startsWith(KafkaTopic.VIDEO_VIEW_STRING)){
            System.out.println("<============>执行Kafka消费中:" +
                    msg.substring(KafkaTopic.VIDEO_VIEW_STRING.length()));
            //去除标头
            String str = msg.substring(KafkaTopic.VIDEO_VIEW_STRING.length());
            //以空格分割数据
            String[] split = str.split(" ");
            int dataLength = 2;
            //数据长度符合规范
            if (split.length == dataLength){
                String userValue = split[0];
                String typeValue = split[1];
                //判断输入的是否为数字
                if (isNumeric(userValue) && isNumeric(typeValue)){
                    int userId = Integer.parseInt(split[0]);
                    int typeId = Integer.parseInt(split[1]);
                    //添加用户历史数据
                    Integer cacheMapValue = redisUtils.getCacheMapValue(HomeServiceImpl.VIDEO_VIEW_NUM + userId,
                            Integer.toString(typeId));
                    if (Objects.isNull(cacheMapValue)){
                        cacheMapValue = 0;
                    }
                    redisUtils.setCacheMapValue(HomeServiceImpl.VIDEO_VIEW_NUM + userId, Integer.toString(typeId),
                            cacheMapValue+1);
                }
            }
        }else if (msg.startsWith(KafkaTopic.VIDEO_COMMENT_STRING)){
            System.out.println("<============>执行Kafka消费中:" +
                    msg.substring(KafkaTopic.VIDEO_COMMENT_STRING.length()));
            //去除标头
            String str = msg.substring(KafkaTopic.VIDEO_COMMENT_STRING.length());
            //以空格分割数据
            String[] split = str.split(" ");
            int dataLength = 2;
            //数据长度符合规范
            if (split.length == dataLength){
                String userValue = split[0];
                String typeValue = split[1];
                //判断输入的是否为数字
                if (isNumeric(userValue) && isNumeric(typeValue)){
                    int userId = Integer.parseInt(split[0]);
                    int typeId = Integer.parseInt(split[1]);
                    //添加用户历史数据
                    Integer cacheMapValue = redisUtils.getCacheMapValue(HomeServiceImpl.VIDEO_COMMENT_NUM + userId,
                            Integer.toString(typeId));
                    if (Objects.isNull(cacheMapValue)){
                        cacheMapValue = 0;
                    }
                    redisUtils.setCacheMapValue(HomeServiceImpl.VIDEO_COMMENT_NUM + userId, Integer.toString(typeId),
                            cacheMapValue+1);
                }
            }
        }else if (msg.startsWith(KafkaTopic.VIDEO_COLLECT_STRING)){
            System.out.println("<============>执行Kafka消费中:" +
                    msg.substring(KafkaTopic.VIDEO_COLLECT_STRING.length()));
            //去除标头
            String str = msg.substring(KafkaTopic.VIDEO_COLLECT_STRING.length());
            //以空格分割数据
            String[] split = str.split(" ");
            int dataLength = 2;
            //数据长度符合规范
            if (split.length == dataLength){
                String userValue = split[0];
                String typeValue = split[1];
                //判断输入的是否为数字
                if (isNumeric(userValue) && isNumeric(typeValue)){
                    int userId = Integer.parseInt(split[0]);
                    int typeId = Integer.parseInt(split[1]);
                    //添加用户历史数据
                    Integer cacheMapValue = redisUtils.getCacheMapValue(HomeServiceImpl.VIDEO_COLLECT_NUM + userId,
                            Integer.toString(typeId));
                    if (Objects.isNull(cacheMapValue)){
                        cacheMapValue = 0;
                    }
                    redisUtils.setCacheMapValue(HomeServiceImpl.VIDEO_COLLECT_NUM + userId, Integer.toString(typeId),
                            cacheMapValue+1);
                }
            }
        }else if (msg.startsWith(KafkaTopic.VIDEO_DIS_COLLECT_STRING)){
            System.out.println("<============>执行Kafka消费中:" +
                    msg.substring(KafkaTopic.VIDEO_DIS_COLLECT_STRING.length()));
            //去除标头
            String str = msg.substring(KafkaTopic.VIDEO_DIS_COLLECT_STRING.length());
            //以空格分割数据
            String[] split = str.split(" ");
            int dataLength = 2;
            //数据长度符合规范
            if (split.length == dataLength){
                String userValue = split[0];
                String typeValue = split[1];
                //判断输入的是否为数字
                if (isNumeric(userValue) && isNumeric(typeValue)){
                    int userId = Integer.parseInt(split[0]);
                    int typeId = Integer.parseInt(split[1]);
                    //添加用户历史数据
                    Integer cacheMapValue = redisUtils.getCacheMapValue(HomeServiceImpl.VIDEO_COLLECT_NUM + userId,
                            Integer.toString(typeId));
                    if (Objects.isNull(cacheMapValue)){
                        cacheMapValue = 0;
                    }
                    redisUtils.setCacheMapValue(HomeServiceImpl.VIDEO_VIEW_NUM + userId, Integer.toString(typeId),
                            cacheMapValue-1);
                }
            }
        }
    }

    public static boolean isNumeric(String str) {
        try {
            new BigDecimal(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
