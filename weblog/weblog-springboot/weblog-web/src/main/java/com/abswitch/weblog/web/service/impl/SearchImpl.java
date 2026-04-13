package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.search.LuceneHelper;
import com.abswitch.weblog.search.config.LuceneProperties;
import com.abswitch.weblog.search.index.ArticleIndex;
import com.abswitch.weblog.web.model.vo.search.SearchArticlePageListReqVO;
import com.abswitch.weblog.web.model.vo.search.SearchArticlePageListRspVO;
import com.abswitch.weblog.web.service.SearchService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringReader;
import java.util.List;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-30 17:00
 * @Description：
 */
@Service
@Slf4j
public class SearchImpl implements SearchService {

    @Autowired
    private LuceneHelper luceneHelper;
    @Autowired
    private LuceneProperties luceneProperties;
    @Override
    public Response searchArticlePageList(SearchArticlePageListReqVO searchArticlePageListReqVO) {

        int current = Math.toIntExact(searchArticlePageListReqVO.getCurrent());
        int size = Math.toIntExact(searchArticlePageListReqVO.getSize());


        String word = searchArticlePageListReqVO.getWord();
        // 想要搜索的文档字段（这里指定对文章标题、摘要进行检索，任何一个字段包含该关键词，都会被搜索到）
        String[] columns = {ArticleIndex.COLUMN_TITLE, ArticleIndex.COLUMN_SUMMARY};
        // 查询总记录数
        long total = luceneHelper.searchTotal(ArticleIndex.NAME, word, columns);
        List<Document> documents = luceneHelper.search(ArticleIndex.NAME, word, columns, current, size);

        // 若未查询到相关文档，只接 return
        if (CollectionUtils.isEmpty(documents)) {
            return PageResponse.ok(total, current, size, null);
        }

        // ======================== 开始关键词高亮处理 ========================
        // 中文分析器
        Analyzer analyzer = new SmartChineseAnalyzer();
        QueryParser parser = new QueryParser(ArticleIndex.COLUMN_TITLE, analyzer);
        Query query = null;
        try {
            query = parser.parse(word);
        } catch (ParseException e) {
            log.error("解析关键词错误:", e);
        }

        // 返参 VO
        List<SearchArticlePageListRspVO> vos = Lists.newArrayList();
        // 遍历查询到的文档，进行关键词高亮处理
        documents.forEach(document -> {
            try {
                // 文章标题
                String title = document.get(ArticleIndex.COLUMN_TITLE);

                String summary = document.get(ArticleIndex.COLUMN_SUMMARY);

                // 高亮标题
                String highlightedTitle = getHighlightedText(title, ArticleIndex.COLUMN_TITLE, word);
                // 高亮摘要
                String highlightedSummary = getHighlightedText(summary, ArticleIndex.COLUMN_SUMMARY, word);

                String id = document.get(ArticleIndex.COLUMN_ID);
                String cover = document.get(ArticleIndex.COLUMN_COVER);
                String createTime = document.get(ArticleIndex.COLUMN_CREATE_TIME);

                // 组装 VO
                SearchArticlePageListRspVO vo = SearchArticlePageListRspVO.builder()
                        .id(Long.valueOf(id))
                        .title(highlightedTitle)
                        .summary(highlightedSummary)
                        .cover(cover)
                        .createDate(createTime)
                        .build();

                vos.add(vo);
            } catch (Exception e) {
                log.error("文档转换错误: ", e);
            }
        });

        return PageResponse.ok(total, current, size, vos);
    }

    private String getHighlightedText(String columnValue, String field, String keyword) {
        if (StringUtils.isBlank(columnValue)) return columnValue;
        try {
            Analyzer analyzer = new SmartChineseAnalyzer();
            QueryParser parser = new QueryParser(field, analyzer);
            Query query = parser.parse(keyword);
            // 创建高亮器
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"color: #f73131\">", "</span>");
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            // 获取高亮的片段
            TokenStream tokenStream = analyzer.tokenStream(field, new StringReader(columnValue));
            String highlighterColumnValue = highlighter.getBestFragment(tokenStream, columnValue);
            // 如果没有匹配到关键词，则返回原始文本
            return StringUtils.isNotBlank(highlighterColumnValue) ? highlighterColumnValue : columnValue;
        } catch (Exception e) {
            log.error("高亮失败", e);
            return columnValue;
        }
    }
}