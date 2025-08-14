package com.aliyun.container.service.cost.sdk;

import com.aliyun.container.service.cost.sdk.model.CostV2Request;
import com.aliyun.container.service.cost.sdk.model.CostV2Response;
import com.aliyun.container.service.cost.sdk.model.CostData;
import com.aliyun.container.service.cost.sdk.model.Properties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Cost V2 API服务类
 */
public class CostV2Service {
    private final Client client;
    private final Gson gson = new Gson();

    /**
     * 构造函数
     * @param client SDK客户端
     */
    public CostV2Service(Client client) {
        this.client = client;
    }

    /**
     * 查询Cost V2成本数据
     * @param request 请求参数
     * @return 响应数据
     * @throws CostError 请求失败
     */
    public CostV2Response getCostV2(CostV2Request request) throws CostError {
        // 构建URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(
                client.getConfig().getApiServer() + 
                "/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy/v2/cost")
                .newBuilder();

        // 添加查询参数
        urlBuilder.addQueryParameter("window", request.getWindow());

        if (request.getFilter() != null) {
            urlBuilder.addQueryParameter("filter", request.getFilter());
        }

        if (request.getStep() != null) {
            urlBuilder.addQueryParameter("step", request.getStep());
        }

        if (request.getAggregate() != null) {
            urlBuilder.addQueryParameter("aggregate", request.getAggregate());
        }

        if (request.getIdle() != null) {
            urlBuilder.addQueryParameter("idle", request.getIdle().toString());
        }

        if (request.getShareIdle() != null) {
            urlBuilder.addQueryParameter("shareIdle", request.getShareIdle().toString());
        }

        if (request.getShareSplit() != null) {
            urlBuilder.addQueryParameter("shareSplit", request.getShareSplit());
        }

        if (request.getIdleByNode() != null) {
            urlBuilder.addQueryParameter("idleByNode", request.getIdleByNode().toString());
        }

        if (request.getFormat() != null) {
            urlBuilder.addQueryParameter("format", request.getFormat());
        }

        // 构建HTTP请求
        Request httpRequest = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        try {
            // 发送请求
            Response response = client.getHttpUtil().doRequest(httpRequest, client.getConfig());

            // 检查响应状态码
            if (!response.isSuccessful()) {
                throw new CostError(
                        getErrorCodeFromStatus(response.code()),
                        "HTTP request failed with status: " + response.code(),
                        null);
            }

            // 解析响应
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new CostError(ErrorCodes.InternalError, "Empty response body", null);
            }

            String jsonData = responseBody.string();
            CostV2Response result = new CostV2Response();

            // 解析JSON数据
            Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> rawResponse = gson.fromJson(jsonData, responseType);

            // 转换数据格式
            if (rawResponse.containsKey("data")) {
                Type dataType = new TypeToken<List<Map<String, Map<String, Object>>>>(){}.getType();
                List<Map<String, Map<String, Object>>> rawData = 
                        gson.fromJson(gson.toJson(rawResponse.get("data")), dataType);

                // TODO: 实现完整的数据转换逻辑
                // 这里简化处理，实际应该将rawData转换为List<Map<String, CostData>>格式
            }

            return result;
        } catch (IOException e) {
            throw new CostError(ErrorCodes.InternalError, "Failed to process response", e);
        } catch (Exception e) {
            if (e instanceof CostError) {
                throw e;
            }
            throw new CostError(ErrorCodes.InternalError, "Failed to send request", e);
        }
    }

    /**
     * 根据HTTP状态码获取错误码
     * @param statusCode HTTP状态码
     * @return 错误码
     */
    private String getErrorCodeFromStatus(int statusCode) {
        switch (statusCode) {
            case 400:
                return ErrorCodes.BadRequest;
            case 401:
                return ErrorCodes.Unauthorized;
            case 403:
                return ErrorCodes.Forbidden;
            case 404:
                return ErrorCodes.NotFound;
            case 429:
                return ErrorCodes.TooManyRequests;
            case 500:
                return ErrorCodes.InternalError;
            case 503:
                return ErrorCodes.ServiceUnavailable;
            default:
                return ErrorCodes.InternalError;
        }
    }
}