package com.sicnu.boot.service.impl;

import com.alibaba.fastjson.JSON;
import com.sicnu.boot.dto.CollegeDTO;
import com.sicnu.boot.dto.QuestionDTO;
import com.sicnu.boot.dto.VideoDTO;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
/**
 * description:  异步service
 *
 * @author :  胡建华
 * Data:    2022/12/13 19:30
 */
@Service
public class AsyncService {

    @Resource
    private RestHighLevelClient client;

    @Async("myExecutor")
    public CompletableFuture<List<VideoDTO>> searchVideo(String name) throws IOException {
        List<VideoDTO> list = new ArrayList<>();
        //设置查询索引
        SearchRequest searchRequest = new SearchRequest("videos");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //模糊匹配
        searchSourceBuilder.query(QueryBuilders
                .fuzzyQuery("all", name)
                .fuzziness(Fuzziness.AUTO));
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name")
                //若有关键字切可以分词，则可以高亮，写*可以匹配所有字段
                .field("introduction")
                //因为高亮查询默认是对查询字段即description就行高亮，可以关闭字段匹配，
                // 这样就可以对查询到的多个字段（前提是有关键词并且改字段可以分词）进行高亮显示
                .requireFieldMatch(false)
                //手动前缀标签
                .preTags("<span style='color:#00aeec'>")
                .postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        //遍历查询
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            //获取类
            VideoDTO videoDTO = JSON.parseObject(source, VideoDTO.class);
            //获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //如果高亮字段包含在introduction中
            if (highlightFields.containsKey("introduction")){
                Text[] fragments = highlightFields.get("introduction").getFragments();
                for (Text fragment : fragments) {
                    //改变属性
                    videoDTO.setIntroduction(fragment.toString());
                }
            }
            //如果高亮字段包含在name中
            if (highlightFields.containsKey("name")){
                Text[] fragments = highlightFields.get("name").getFragments();
                for (Text fragment : fragments) {
                    videoDTO.setName(fragment.toString());
                }
            }
            //将实体类添加到集合
            list.add(videoDTO);
        }
        return CompletableFuture.completedFuture(list);
    }

    @Async("myExecutor")
    public CompletableFuture<List<QuestionDTO>> searchQuestion(String name) throws IOException {
        List<QuestionDTO> list = new ArrayList<>();
        //设置查询索引
        SearchRequest searchRequest = new SearchRequest("questions");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //模糊匹配
        searchSourceBuilder.query(QueryBuilders
                .fuzzyQuery("all", name)
                .fuzziness(Fuzziness.AUTO));
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("questionTitle")
                .requireFieldMatch(false)
                .preTags("<span style='color:#00aeec'>")
                .postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        //遍历查询
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            //获取类
            QuestionDTO questionDTO = JSON.parseObject(source, QuestionDTO.class);
            //获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //如果高亮字段包含在questionTitle中
            if (highlightFields.containsKey("questionTitle")){
                Text[] fragments = highlightFields.get("questionTitle").getFragments();
                for (Text fragment : fragments) {
                    //改变属性
                    questionDTO.setQuestionTitle(fragment.toString());
                }
            }
            //将实体类添加到集合
            list.add(questionDTO);
        }
        return CompletableFuture.completedFuture(list);
    }

    @Async("myExecutor")
    public CompletableFuture<List<CollegeDTO>> searchCollege(String name) throws IOException {
        List<CollegeDTO> list = new ArrayList<>();
        //设置查询索引
        SearchRequest searchRequest = new SearchRequest("colleges");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //模糊匹配
        searchSourceBuilder.query(QueryBuilders
                .fuzzyQuery("all", name)
                .fuzziness(Fuzziness.AUTO));
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name")
                //若有关键字切可以分词，则可以高亮，写*可以匹配所有字段
                .field("introduction")
                //因为高亮查询默认是对查询字段即description就行高亮，可以关闭字段匹配，
                // 这样就可以对查询到的多个字段（前提是有关键词并且改字段可以分词）进行高亮显示
                .requireFieldMatch(false)
                //手动前缀标签
                .preTags("<span style='color:#00aeec'>")
                .postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        //遍历查询
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            //获取类
            CollegeDTO collegeDTO = JSON.parseObject(source, CollegeDTO.class);
            //获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //如果高亮字段包含在introduction中
            if (highlightFields.containsKey("introduction")){
                Text[] fragments = highlightFields.get("introduction").getFragments();
                for (Text fragment : fragments) {
                    //改变属性
                    collegeDTO.setIntroduction(fragment.toString());
                }
            }
            //如果高亮字段包含在name中
            if (highlightFields.containsKey("name")){
                Text[] fragments = highlightFields.get("name").getFragments();
                for (Text fragment : fragments) {
                    collegeDTO.setName(fragment.toString());
                }
            }
            //将实体类添加到集合
            list.add(collegeDTO);
        }
        return CompletableFuture.completedFuture(list);
    }
}
