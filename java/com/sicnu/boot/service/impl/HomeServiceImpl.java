package com.sicnu.boot.service.impl;

import com.sicnu.boot.dto.CollegeDTO;
import com.sicnu.boot.dto.QuestionDTO;
import com.sicnu.boot.dto.VideoDTO;
import com.sicnu.boot.dto.VideoTypeCount;
import com.sicnu.boot.pojo.Video;
import com.sicnu.boot.service.HomeService;
import com.sicnu.boot.utils.RedisUtils;
import com.sicnu.boot.utils.ResponseCode;
import com.sicnu.boot.utils.ServerResponse;
import com.sicnu.boot.vo.LoginUser;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * description:
 *
 * @author :  胡建华
 * Data:    2022/12/13 19:22
 */
@Service
public class HomeServiceImpl implements HomeService {

    @Resource
    private AsyncService asyncService;

    @Resource
    private RedisUtils redisUtils;

    public static final String HOT_SPOT = "video_spot:";

    public static final String VIDEO_COLLECT_NUM = "video-collect-num:";

    public static final String VIDEO_VIEW_NUM = "video-view-num:";

    public static final String VIDEO_COMMENT_NUM = "video-comment-num:";

    public static final String VIDEO_RECOMMEND = "video-recommend:";

    @Resource
    private TimedTaskService timedTaskService;

    @SneakyThrows
    @Override
    public ServerResponse<Map<String,Object>> search(String name) {
        CompletableFuture<List<CollegeDTO>> searchCollege = asyncService.searchCollege(name);
        CompletableFuture<List<QuestionDTO>> searchQuestion = asyncService.searchQuestion(name);
        CompletableFuture<List<VideoDTO>> searchVideo = asyncService.searchVideo(name);
        //等待所有任务都执行完
        CompletableFuture.allOf(searchCollege,searchVideo,searchQuestion);
        // 获取每个任务的返回结果
        List<CollegeDTO> collegeList;
        List<QuestionDTO> questionList;
        List<VideoDTO> videoList;
        Map<String,Object> map = new HashMap<>(5);
        AtomicReference<Integer> nums = new AtomicReference<>(1);
        if (!Objects.isNull(searchCollege)){
            collegeList = searchCollege.get();
            collegeList.forEach(item -> item.setId(nums.getAndSet(nums.get() + 1)));
            map.put("collegeList",collegeList);
        }
        if (!Objects.isNull(searchQuestion)){
            questionList = searchQuestion.get();
            questionList.forEach(item -> item.setId(nums.getAndSet(nums.get() + 1)));
            map.put("questionList",questionList);
        }
        if (!Objects.isNull(searchVideo)){
            videoList = searchVideo.get();
            videoList.forEach(item -> item.setId(nums.getAndSet(nums.get() + 1)));
            map.put("videoList",videoList);
        }
        if (map.isEmpty()){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.HAS_NO_DATA.getCode(),
                    "数据为空");
        }
        return ServerResponse.createBySuccess("获取成功",map);
    }

    @Override
    public ServerResponse<List<Video>> getVideoListByType(Integer typeId) {
        List<Video> cacheList = redisUtils.getCacheList(HOT_SPOT + typeId);
        if (Objects.isNull(cacheList) || cacheList.isEmpty()){
            //手动刷新热点数据
            timedTaskService.flushHotSpot();
            //重新获取
            cacheList = redisUtils.getCacheList(HOT_SPOT + typeId);
        }
        //没有数据
        if (cacheList.isEmpty()){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.HAS_NO_DATA.getCode()
                    , "没有数据");
        }
        return ServerResponse.createBySuccess("获取成功",cacheList);
    }

    @Override
    public ServerResponse<List<Video>> getVideoRecommend() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = ((LoginUser) authentication.getPrincipal()).getUser().getUserId();
        //从redis中获取用户推荐视频
        List<Video> list = redisUtils.getCacheList(VIDEO_RECOMMEND + userId);
        if (!list.isEmpty()){
            //获取到视频
            return ServerResponse.createBySuccess("获取成功",list);
        }
        List<VideoTypeCount> videoTypeCounts = statsVideo(userId);
        int maxCount = 2;
        //遍历权重，并取值
        for (int i = 0; i <= maxCount; i++) {
            List<Video> cacheList = redisUtils.getCacheList(HOT_SPOT + videoTypeCounts.get(i).getId());
            if (Objects.isNull(cacheList) || cacheList.isEmpty()){
                //手动刷新热点数据
                timedTaskService.flushHotSpot();
                //重新获取
                cacheList = redisUtils.getCacheList(HOT_SPOT + videoTypeCounts.get(i).getId());
            }
            if (i == 0){
                cacheList.stream().limit(4).forEach(list::add);
            }else if (i == 1){
                cacheList.stream().limit(3).forEach(list::add);
            }else {
                cacheList.stream().limit(1).forEach(list::add);
            }
        }
        if (list.isEmpty()){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.HAS_NO_DATA.getCode()
                    , "没有数据");
        }else {
            //缓存推荐视频信息
            redisUtils.setCacheList(VIDEO_RECOMMEND + userId,list);
            //设置有效时间，1个小时
            redisUtils.expire(VIDEO_RECOMMEND + userId,1, TimeUnit.HOURS);
        }
        return ServerResponse.createBySuccess("获取成功",list);
    }

    /**
     * description: 获取用户对不同种类视频的权重
     *
     * @param userId:
     * @return List<VideoTypeCount>
     * @author 胡建华
     * Date:  2023/1/7 20:15
     */
    private List<VideoTypeCount> statsVideo(Integer userId){
        List<VideoTypeCount> list = new ArrayList<>();
        Map<String, Integer> collectMap = redisUtils.getCacheMap(VIDEO_COLLECT_NUM + userId);
        Map<String, Integer> viewMap = redisUtils.getCacheMap(VIDEO_VIEW_NUM + userId);
        Map<String, Integer> commentMap = redisUtils.getCacheMap(VIDEO_COMMENT_NUM + userId);
        int typeNum = 6;
        for(int i = 0; i < typeNum; i++){
            int collectNum = collectMap.get(Integer.toString(i + 1)) == null ?
                    0 : collectMap.get(Integer.toString(i + 1));
            int viewNum = viewMap.get(Integer.toString(i + 1)) == null ?
                    0 : viewMap.get(Integer.toString(i + 1));
            int commentNum = commentMap.get(Integer.toString(i + 1)) == null ?
                    0 : commentMap.get(Integer.toString(i + 1));
            list.add(new VideoTypeCount(i + 1,viewNum * 5 + collectNum * 3 + commentNum * 2));
        }
        list.sort((o1, o2) -> o2.getCount() - o1.getCount());
        return list;
    }
}
