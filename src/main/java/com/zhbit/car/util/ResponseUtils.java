package com.zhbit.car.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

public class ResponseUtils {

    // header 常量定义
    private static final String ENCODING_PREFIX = "encoding";
    private static final String NOCACHE_PREFIX = "no-cache";
    private static final String ENCODING_DEFAULT = "UTF-8";
    private static final boolean NOCACHE_DEFAULT = true;

    private static final Logger LOG = LoggerFactory.getLogger(ResponseUtils.class);

    /**
     * 直接输出内容的简便函数.
     * <p/>
     * eg. render("text/plain", "hello", "encoding:GBK"); render("text/plain",
     * "hello", "no-cache:false"); render("text/plain", "hello", "encoding:GBK",
     * "no-cache:false");
     *
     * @param headers 可变的header数组，目前接受的值为"encoding:"或"no-cache:",默认值分别为UTF-8和true.
     */
    public static void render(HttpServletResponse response,
                              final String contentType, final String content,
                              final String... headers) {
        try {
            // 分析headers参数
            String encoding = ENCODING_DEFAULT;
            boolean noCache = NOCACHE_DEFAULT;
            for (String header : headers) {
                String headerName = StringUtils.substringBefore(header, ":");
                String headerValue = StringUtils.substringAfter(header, ":");

                if (StringUtils.equalsIgnoreCase(headerName, ENCODING_PREFIX)) {
                    encoding = headerValue;
                } else if (StringUtils.equalsIgnoreCase(headerName,
                        NOCACHE_PREFIX)) {
                    noCache = Boolean.parseBoolean(headerValue);
                } else {
                    throw new IllegalArgumentException(headerName
                            + "不是一个合法的header类型");
                }
            }

            // 设置headers参数
            String fullContentType = contentType + ";charset=" + encoding;
            response.setContentType(fullContentType);
            if (noCache) {
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
            }

            response.getWriter().write(content);
            response.getWriter().flush();

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * 直接输出文本.
     *
     * @see #render(HttpServletResponse, String, String, String...)
     */
    public static void renderText(HttpServletResponse response,
                                  final String text, final String... headers) {
        render(response, "text/plain", text, headers);
    }

    /**
     * 直接输出HTML.
     *
     * @see #render(HttpServletResponse, String, String, String...)
     */
    public static void renderHtml(HttpServletResponse response,
                                  final String html, final String... headers) {
        render(response, "text/html", html, headers);
    }

    /**
     * 直接输出XML.
     *
     * @see #render(HttpServletResponse, String, String, String...)
     */
    public static void renderXml(HttpServletResponse response,
                                 final String xml, final String... headers) {
        render(response, "text/xml", xml, headers);
    }

    /**
     * 直接输出JSON.
     *
     * @param string json字符串.
     * @see #render(HttpServletResponse, String, String, String...)
     */
    public static void renderJson(HttpServletResponse response,
                                  final String string, final String... headers) {
        render(response, "application/json", string, headers);
    }

    /**
     * 直接输出JSON.
     *
     * @param map Map对象,将被转化为json字符串.
     * @see #render(HttpServletResponse, String, String, String...)
     */
    @SuppressWarnings("unchecked")
    public static void renderJson(HttpServletResponse response, final Map map,
                                  final String... headers) {
        String jsonString = JSON.toJSONString(map);
        renderJson(response, jsonString, headers);
    }

    /**
     * 直接输出JSON.
     *
     * @param object Java对象,将被转化为json字符串.
     * @see #render(HttpServletResponse, String, String, String...)
     */
    public static void renderJson(HttpServletResponse response,
                                  final Object object, final String... headers) {
        String jsonString = JSON.toJSONString(object);
        renderJson(response, jsonString, headers);
    }

    /**
     * 输出excel
     *
     * @param response
     * @param excel
     * @param headers
     */
    public static void renderExcel(HttpServletResponse response, final String
            excel, final String... headers) {
        render(response, "excel.xls", excel,
                headers);
    }

    /**
     * 直接输出文件.
     *
     * @param response
     * @param fileName
     * @param data
     * @throws IOException
     * @see #render(HttpServletResponse, String, String, String...)
     */
    public static void renderFile(HttpServletResponse response,
                                  String fileName, byte[] data) throws IOException {
        String fileNameUTF8 = URLEncoder.encode(fileName, "UTF-8");
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + fileNameUTF8 + "\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream;charset=UTF-8");
        OutputStream outputStream = new BufferedOutputStream(
                response.getOutputStream());
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();
    }
}
