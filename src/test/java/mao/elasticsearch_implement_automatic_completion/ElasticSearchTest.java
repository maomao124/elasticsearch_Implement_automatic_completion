package mao.elasticsearch_implement_automatic_completion;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Project name(项目名称)：elasticsearch_Implement_automatic_completion
 * Package(包名): mao.elasticsearch_implement_automatic_completion
 * Class(类名): ElasticSearchTest
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/6/1
 * Time(创建时间)： 20:19
 * Version(版本): 1.0
 * Description(描述)： 测试自动补全
 */


@SpringBootTest
public class ElasticSearchTest
{

    private static RestHighLevelClient client;

    /**
     * Before all.
     */
    @BeforeAll
    static void beforeAll()
    {
        client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));
    }

    /**
     * After all.
     *
     * @throws IOException the io exception
     */
    @AfterAll
    static void afterAll() throws IOException
    {
        client.close();
    }

    /**
     * 测试自动补全功能
     * <p>
     * 请求内容：
     * <pre>
     *
     * GET /test2/_search
     * {
     *   "suggest":
     *   {
     *     "title_suggest":
     *     {
     *       "text": "s",
     *       "completion":
     *       {
     *         "field": "title",
     *         "skip_duplicates": true,
     *         "size": 10
     *       }
     *     }
     *   }
     * }
     *
     * </pre>
     * <p>
     * 结果：
     * <pre>
     *
     * {
     *   "took" : 1,
     *   "timed_out" : false,
     *   "_shards" : {
     *     "total" : 1,
     *     "successful" : 1,
     *     "skipped" : 0,
     *     "failed" : 0
     *   },
     *   "hits" : {
     *     "total" : {
     *       "value" : 0,
     *       "relation" : "eq"
     *     },
     *     "max_score" : null,
     *     "hits" : [ ]
     *   },
     *   "suggest" : {
     *     "title_suggest" : [
     *       {
     *         "text" : "s",
     *         "offset" : 0,
     *         "length" : 1,
     *         "options" : [
     *           {
     *             "text" : "SK-II",
     *             "_index" : "test2",
     *             "_id" : "nVUsH4EBfwatmgrgIAsV",
     *             "_score" : 1.0,
     *             "_source" : {
     *               "title" : [
     *                 "SK-II",
     *                 "PITERA"
     *               ]
     *             }
     *           },
     *           {
     *             "text" : "Sony",
     *             "_index" : "test2",
     *             "_id" : "nFUsH4EBfwatmgrgFgu5",
     *             "_score" : 1.0,
     *             "_source" : {
     *               "title" : [
     *                 "Sony",
     *                 "WH-1000XM3"
     *               ]
     *             }
     *           },
     *           {
     *             "text" : "switch",
     *             "_index" : "test2",
     *             "_id" : "nlUsH4EBfwatmgrgKAsR",
     *             "_score" : 1.0,
     *             "_source" : {
     *               "title" : [
     *                 "Nintendo",
     *                 "switch"
     *               ]
     *             }
     *           }
     *         ]
     *       }
     *     ]
     *   }
     * }
     *
     * </pre>
     * <p>
     * 程序结果：
     * <pre>
     *
     * 补全字段：s
     * 结果：
     * -->SK-II
     * -->Sony
     * -->switch
     *
     * </pre>
     *
     * @throws Exception Exception
     */
    @Test
    void automatic_completion() throws Exception
    {
        //构建请求
        SearchRequest searchRequest = new SearchRequest("test2");
        //构建请求体
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //补全
        searchSourceBuilder.suggest(new SuggestBuilder().addSuggestion(
                "title_suggest",
                new CompletionSuggestionBuilder("title").text("s").skipDuplicates(true).size(10)));
        //放入到请求中
        searchRequest.source(searchSourceBuilder);
        //发起请求
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //获取数据
        //获取suggest部分
        Suggest suggest = searchResponse.getSuggest();
        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>>
                title_suggest = suggest.getSuggestion("title_suggest");
        List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries = title_suggest.getEntries();
        for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : entries)
        {
            String text = entry.getText().string();
            System.out.println("补全字段：" + text);
            System.out.println("结果：");
            for (Suggest.Suggestion.Entry.Option option : entry)
            {
                String result = option.getText().string();
                System.out.println("-->" + result);
            }
        }
    }


    /**
     * 自动补全
     *
     * @param automaticCompletionText 自动补全的文本
     * @throws Exception Exception
     */
    void automaticCompletionService(String automaticCompletionText) throws Exception
    {
        //构建请求
        SearchRequest searchRequest = new SearchRequest("test2");
        //构建请求体
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //补全
        searchSourceBuilder.suggest(new SuggestBuilder().addSuggestion(
                "title_suggest",
                new CompletionSuggestionBuilder("title").text(automaticCompletionText).skipDuplicates(true).size(10)));
        //放入到请求中
        searchRequest.source(searchSourceBuilder);
        //发起请求
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //获取数据
        //获取suggest部分
        Suggest suggest = searchResponse.getSuggest();
        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>>
                title_suggest = suggest.getSuggestion("title_suggest");
        List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries = title_suggest.getEntries();
        for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : entries)
        {
            String text = entry.getText().string();
            System.out.println("补全字段：" + text);
            System.out.println("结果：");
            for (Suggest.Suggestion.Entry.Option option : entry)
            {
                String result = option.getText().string();
                System.out.println("-->" + result);
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        beforeAll();
        while (true)
        {
            Scanner input = new Scanner(System.in);
            System.out.print("请输入要补全的关键字：");
            String inputString = input.next();
            if (inputString.equals("exit"))
            {
                afterAll();
                return;
            }
            new ElasticSearchTest().automaticCompletionService(inputString);
            System.out.println();
            System.out.println("--------");
            System.out.println();
        }
    }
}
