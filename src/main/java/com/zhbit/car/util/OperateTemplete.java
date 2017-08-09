package com.zhbit.car.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lz on 2016/4/25.
 */
public abstract class OperateTemplete {
    public static final Logger log = LoggerFactory.getLogger(OperateTemplete.class);

    private HttpServletRequest request;
    protected String str;
    protected Map<String, Object> map = new HashMap<String, Object>();
    protected List<Object> list = new ArrayList<Object>();
    protected List<BindingResult> validResultList = new ArrayList<BindingResult>();

    public OperateTemplete() {
    }

    ;

    /**
     * 构造函数
     *
     * @param request
     */
    public OperateTemplete(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * @param request
     * @param results
     */
    public OperateTemplete(HttpServletRequest request, BindingResult... results) {
        this.request = request;
        for (BindingResult result : results) {
            validResultList.add(result);
        }
    }


    /**
     * Controller层统一调用的方法
     */
    protected abstract void doSomething() throws Exception;

    /**
     * 设置map值
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        map.put(key, value);
    }

    /**
     * 输出xml
     *
     * @param response
     */
    public void operate(HttpServletResponse response) {
        try {
            doSomething();
        } catch (Exception e) {
            log.error("operate fail!", e);
            str = returnFail(getExcptionMessage(e));
        }
        ResponseUtils.renderXml(response, str);
    }


    /**
     * 配合@ResponseBody输出json
     *
     * @return
     */
    public Map<String, Object> operate() {
        try {
            doSomething();
            map.put("success", true);
        } catch (Exception e) {
            log.error("operate fail!", e);
            map.put("success", false);
            map.put("data", getExcptionMessage(e));
        }
        return map;
    }

    /**
     * 配合@ResponseBody输出json 只输出结果list
     *
     * @return
     */
    public List<Object> operateList() {
        try {
            doSomething();
        } catch (Exception e) {
            log.error("operateList fail!", e);
        }
        return list;
    }

    /**
     * view
     *
     * @return
     */
    public String operateModel() {
        try {
            doSomething();
        } catch (Exception e) {
            log.error("operateModel fail!", e);
        }
        return str;
    }

    /**
     * 输出视图
     *
     * @param viewName
     * @return
     */
    public ModelAndView operateView(String viewName) {
        try {
            doSomething();
        } catch (Exception e) {
            log.error("operateView fail!", e);
            str = returnFail(getExcptionMessage(e));
        }
        return new ModelAndView(viewName, map);
    }

    /**
     * 输出excel
     *
     * @param viewName
     * @return
     */
    public ModelAndView operateExcel(String viewName) {
        return operateView(viewName);
    }

    /**
     * 输出pdf
     *
     * @param viewName
     * @return
     */
    public ModelAndView operatePdf(String viewName) {
        return operateView(viewName);
    }

    /**
     * 输出文件
     *
     * @param response
     * @return
     */
    public void operateFile(HttpServletResponse response) {
        try {
            doSomething();
            String fileName = map.get("fileName").toString();
            byte[] data = (byte[]) map.get("fileData");
            ResponseUtils.renderFile(response, fileName, data);
        } catch (Exception e) {
            log.error("operateFile fail!", e);
            str = returnFail(getExcptionMessage(e));
        }
    }

    /**
     * 返回成功
     *
     * @return
     */
    private String returnSuccess() {
        return "ok";
    }

    /**
     * 返回错误信息
     *
     * @param msg
     * @return
     */
    private String returnFail(String msg) {
        return msg;
    }


    /**
     * 异常信息（当范围message长度大于100时只显示指定内容）
     *
     * @param e
     * @return
     */
    private String getExcptionMessage(Exception e) {
        if (String.valueOf(e.getMessage()).length() > 100) {
            return "服务器处理异常！";
        } else {
            return e.getMessage();
        }
    }

    /**
     * 请求参数校验方法
     *
     * @return boolean
     * @throws Exception
     */
    protected boolean doValidate() throws Exception {
        if (validResultList != null) {
            List<HashMap<String, String>> errorList = new ArrayList<HashMap<String, String>>();
            for (BindingResult result : validResultList) {
                if (result.hasErrors()) {
                    HashMap<String, String> errorMap = new HashMap<String, String>();
                    errorMap.put("object", result.getObjectName());
                    errorMap.put("field", result.getFieldError().getField());
                    errorMap.put("message", result.getFieldError()
                            .getDefaultMessage());
                    errorList.add(errorMap);
                }
            }
            if (errorList.size() > 0) {
                throw new Exception(JSON.toJSONString(errorList));
            }
        }
        return true;
    }

}
