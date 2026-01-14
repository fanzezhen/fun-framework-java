package com.github.fanzezhen.fun.framework.data.elasticsearch.base.template;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.data.template.ITemplate;
import com.github.fanzezhen.fun.framework.core.log.base.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.FunFunction;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchProperties;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.enums.FunDataElasticsearchExceptionEnum;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.BaseSearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.DocumentData;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.IDocumentSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseElasticsearchTemplate implements IElasticsearchTemplate {

    protected final FunElasticsearchProperties.Config config;
    protected final FunLogHelper funLogHelper;
    protected final List<IDocumentSerializer> documentSerializerList;
    protected final List<IResponseDeserializer> responseDeserializerList;

    protected BaseElasticsearchTemplate(FunElasticsearchProperties.Config config,
                                        FunLogHelper funLogHelper,
                                        List<IDocumentSerializer> documentSerializerList,
                                        List<IResponseDeserializer> responseDeserializerList) {
        this.config = config;
        this.funLogHelper = funLogHelper;
        this.responseDeserializerList = responseDeserializerList;
        this.documentSerializerList = documentSerializerList;
    }

    /**
     * 清除滚动搜索的scroll上下文
     *
     * @param scrollIds 需要清除的一个或多个scroll ID
     * @return 如果清除操作成功则返回true，否则返回false
     */
    public abstract boolean clearScroll(String scrollId, String... scrollIds);

    @Override
    public <T> List<T> searchList(Object request, Class<T> clz) {
        return search(request, clz).asDocumentList();
    }

    @Override
    public <T> T searchOne(Object request, Class<T> clz) {
        return search(request, clz).asDocument();
    }

    /**
     * 清除滚动搜索的scroll上下文
     *
     * @param clz       文档类型
     * @param scrollIds 需要清除的一个或多个scroll ID
     *
     * @return 如果清除操作成功则返回true，否则返回false
     */
    @Override
    public <T> boolean clearScroll(Class<T> clz, String scrollId, String... scrollIds) {
        return clearScroll(scrollId, scrollIds);
    }

    /**
     * 获取索引名称
     */
    protected <T> String getIndexName(Class<T> clz) {
        return ITemplate.getTable(clz, config.getIndexPrefix());
    }

    /**
     * 将文档对象转化为 DocumentData
     *
     * @param documents 文档对象
     * @param aClass    文档对象类型
     *
     * @return 解析后的文档Bean
     */
    protected List<DocumentData> convertDocumentData(Class<?> aClass, Object... documents) {
        List<DocumentData> documentDataList = new ArrayList<>();
        for (Object document : documents) {
            final DocumentData documentData = documentSerializerList.stream()
                .filter(e -> e.isSupport(document, aClass))
                .findFirst()
                .orElseThrow(() -> new SecurityException("不能解析此文档，可能是没有能够使用的序列化器：" + document))
                .serialize(document, aClass);
            // 检查id字段是否为空
            if (CharSequenceUtil.isBlank(documentData.getId())) {
                throw new SecurityException("更新的 Document 对象没有字段被标记 @EsId 注解标记，或者值为空：" + document);
            }
            documentDataList.add(documentData);
        }
        return documentDataList;
    }

    /**
     * 将文档对象转化为 DocumentData
     *
     * @param documents 文档对象
     * @param aClass    文档对象类型
     *
     * @return 解析后的文档Bean
     */
    protected List<DocumentData> convertDocumentData(Class<?> aClass, Collection<?> documents) {
        return convertDocumentData(aClass, documents.toArray());
    }

    /**
     * 将response对象转化为SearchResult
     *
     * @param response 响应结果
     * @param documentClass    文档对象类型
     *
     * @return 解析后的文档Bean
     */
    protected<T> ISearchResult<T> convertResponseToResult(Object response, Class<T> documentClass) {
        if (response == null){
            return BaseSearchResult.empty(documentClass);
        }
        for (IResponseDeserializer responseDeserializer : responseDeserializerList) {
            if (responseDeserializer.isSupport(response)){
                return responseDeserializer.deserialize(response, documentClass);
            }
        }
        throw new ServiceException(FunDataElasticsearchExceptionEnum.RESPONSE_DESERIALIZER_ERROR, response.getClass(), CharSequenceUtil.EMPTY, "未注册");
    }

    /**
     * 执行并打印日志
     */
    protected <T, R> R executeByLog(T requestParam, FunFunction<T, R> invoker) {
        return funLogHelper.executeByLog(
            this.getClass().getName(),
            invoker,
            requestParam
        );
    }
}
