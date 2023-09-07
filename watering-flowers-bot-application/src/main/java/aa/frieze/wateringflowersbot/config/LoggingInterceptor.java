package aa.frieze.wateringflowersbot.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;


/**
 * Конфигурация перехватчика запросов
 */
@Slf4j
@SuppressWarnings("all")
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    /**
     * Переопределения перехвата запросов для логирования
     *
     * @param request   запрос
     * @param body      тело запроса
     * @param execution выполнение запроса
     * @return ответ в HTTP формате
     * @throws IOException потенциально может выбросить исключение
     */
    @Override
    public ClientHttpResponse intercept(@Nullable HttpRequest request,
                                        @Nullable byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        URI uri = request.getURI();
        boolean skipDebug = !StringUtils.endsWith(uri.toString(), "Login");

        if (skipDebug) {
            debugRequest(Objects.requireNonNull(request), body);
        } else {
            log.info("URI         : {}", uri);
        }
        ClientHttpResponse response = execution.execute(request, Objects.requireNonNull(body));


        byte[] bytes = toByteArray(response.getBody());
        if (skipDebug) {
            debugResponse(response, bytes);
        } else {
            log.info("Status externalId  : {}", response.getStatusCode());
        }

        return getClientHttpResponse(response, bytes);
    }

    /**
     * Обработка запроса
     *
     * @param request запрос
     * @param body    тело
     */
    private void debugRequest(HttpRequest request, byte[] body) {
        log.info("============request begin============");
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());

        Map<String, String> headers = new HashMap<>(request.getHeaders().toSingleValueMap());
        log.info("Headers     : {}", headers);

        String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        if (contentType != null && !contentType.contains("image")) {
            log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
        }
        log.info("=============request end=============");
    }

    /**
     * Обработка ответа
     *
     * @param response ответ
     * @param data     данные
     * @throws IOException потенциально может выбросить исключение
     */
    private void debugResponse(ClientHttpResponse response, byte[] data) throws IOException {
        log.info("============response begin============");
        log.info("Status externalId  : {}", defaultIfNull(response.getStatusCode(),
                response.getRawStatusCode()));
        log.info("Status text  : {}", response.getStatusText());
        log.info("Headers      : {}", response.getHeaders());

        String contentType = response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        if (contentType != null && !contentType.contains("image")) {
            log.info("Response body: {}", Optional.ofNullable(data)
                    .map(String::new).orElse(Strings.EMPTY));
        }
        log.info("=============response end=============");
    }

    public static ClientHttpResponse getClientHttpResponse(ClientHttpResponse response, byte[] bytes) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                // fixme kostyl
                return response.getRawStatusCode() == 499 ? HttpStatus.BAD_REQUEST : response.getStatusCode();
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return response.getRawStatusCode();
            }

            @Override
            public String getStatusText() throws IOException {
                return response.getStatusText();
            }

            @Override
            public void close() {
                response.close();
            }

            @Override
            public InputStream getBody() {
                return Optional.ofNullable(bytes).map(ByteArrayInputStream::new)
                        .map(InputStream.class::cast).orElseGet(StreamUtils::emptyInput);
            }

            @Override
            public HttpHeaders getHeaders() {
                return response.getHeaders();
            }
        };
    }
}
