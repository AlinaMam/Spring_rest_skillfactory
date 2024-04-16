package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class LoggingFilter extends OncePerRequestFilter {
    private final static Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    //тип для логирования
    private static final List<MediaType> MEDIA_TYPE_LIST = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA //загрузка файлов, объектов, какой-то бинарный объект
    );

    //список типов данных, с которыми может работать фильтр
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponce(response), filterChain);
        }
    }

    //оборачивает запросы и ответы
    //если мы считаем данные из запроса один раз, то мы потом их уже не сможем прочитать ещё раз
    protected void doFilterWrapped(ContentCachingRequestWrapper contentCachingRequestWrapper, ContentCachingResponseWrapper contentCachingResponseWrapper, FilterChain filterChain) throws IOException, ServletException {
        try {
            //будет выполняться до выполнения запроса
            beforeRequest(contentCachingRequestWrapper, contentCachingResponseWrapper);
            filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
        } finally {
            afterRequest(contentCachingRequestWrapper, contentCachingResponseWrapper);
            contentCachingResponseWrapper.copyBodyToResponse();
        }
    }

    //перед запросом
    protected void beforeRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper responce) {
        if (logger.isInfoEnabled()) {//если уровень логирования инфо и выше, то залогируй
            logRequestHeader(request, request.getRemoteAddr() + "!>");
        }
    }

    //после запроса
    protected void afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper responce) {
        if (logger.isInfoEnabled()) {
            logRequestBody(request, request.getRemoteAddr() + "!>");
            logResponce(responce, request.getRemoteAddr() + "!>");
        }
    }

    //логирует header запроса
    private static void logRequestHeader(ContentCachingRequestWrapper request, String prefix) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            logger.info("{}, {}, {}", request.getMethod(), request.getRequestURI());
        } else {
            logger.info("{}, {}, {}?{}", prefix, request.getMethod(), request.getRequestURI(), queryString);
        }
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> Collections.list(request.getHeaders(headerName))
                        .forEach(headerValue -> logger.info("{}, {}, {}", prefix, headerName, headerValue)));
        logger.info("{}", prefix);
    }

    //логирует тело запроса
    private static void logRequestBody(ContentCachingRequestWrapper request, String prefix) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, request.getContentType(), request.getCharacterEncoding(), prefix);
        }
    }
    //для логирования ответа

    private static void logResponce(ContentCachingResponseWrapper responce, String prefix) {
        int status = responce.getStatus();
        logger.info("{}, {}, {}", prefix, status, HttpStatus.valueOf(status).getReasonPhrase());
        responce.getHeaderNames()
                .forEach(header -> responce.getHeaders(header)
                        .forEach(headerValue -> logger.info("{}, {}, {}", prefix, header, headerValue)));
        logger.info("{}", prefix);//для визуального разделения где заканчиваются заголовки, где ответ
        byte[] content = responce.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, responce.getContentType(), responce.getCharacterEncoding(), prefix);
        }
    }

    //для логирования контента
    private static void logContent(byte[] content, String contentType, String contentEncoding, String prefix) {
        //поддерживает ли приложением mediaType, которые мы получили, для логирования
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = MEDIA_TYPE_LIST.stream().anyMatch(visubleType -> visubleType.includes(mediaType));
        if (visible) {
            try {
                String contentString = new String(content, contentEncoding);
                //отделяем служебные символы от тех данных, которые нам интересны
                Stream.of(contentString.split("\r\n|\r\n"))
                        .forEach(line -> logger.info("{} {}", prefix, line));
            } catch (UnsupportedEncodingException e) {
                //если мы не можем залогировать сообщаем их размер
                logger.info("{}, [{} bytes content]", prefix, content.length);
            }
        } else {
            //если mediaType не поддерживается
            logger.info("{}, [{} bytes content]", prefix, content.length);
        }
    }

    //оборачиваем request
    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest httpServletRequest) {
        if (httpServletRequest instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) httpServletRequest;
        } else {
            return new ContentCachingRequestWrapper((httpServletRequest));
        }
    }

    //оборачиваем responce
    private static ContentCachingResponseWrapper wrapResponce(HttpServletResponse httpServletResponse) {
        if (httpServletResponse instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) httpServletResponse;
        } else {
            return new ContentCachingResponseWrapper((httpServletResponse));
        }
    }
}
