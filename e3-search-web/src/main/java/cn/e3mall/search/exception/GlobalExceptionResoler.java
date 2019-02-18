package cn.e3mall.search.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GlobalExceptionResoler implements HandlerExceptionResolver {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionResoler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) {
       logger.error("系统发生异常",e);
       ModelAndView modelAndView = new ModelAndView();
       modelAndView.addObject("message","系统发生异常,请稍后重试");
       modelAndView.setViewName("error/exception");
    return modelAndView;
    }
}
