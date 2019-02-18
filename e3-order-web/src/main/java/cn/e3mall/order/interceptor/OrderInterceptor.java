package cn.e3mall.order.interceptor;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;
import jdk.nashorn.internal.parser.Token;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OrderInterceptor implements HandlerInterceptor {

   @Autowired
   private TokenService tokenService;

   @Autowired
   private CartService cartService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String token = CookieUtils.getCookieValue(httpServletRequest, "e3-token");

        if (StringUtils.isBlank(token)){
            httpServletResponse.sendRedirect("http://localhost:8088/page/login?redirect="+httpServletRequest.getRequestURI());
            return false;
        }
        E3Result e3Result = tokenService.getUseByToken(token);
        if (e3Result.getStatus() != 200){
            httpServletResponse.sendRedirect("http://localhost:8088/page/login?redirect="+httpServletRequest.getRequestURI());
            return false;
        }

        TbUser user = (TbUser) e3Result.getData();

        httpServletRequest.setAttribute("user",user);

        String json = CookieUtils.getCookieValue(httpServletRequest, "CART", true);
        if (StringUtils.isNotBlank(json)){
            cartService.mergeCart(user.getId(), JsonUtils.jsonToList(json, TbItem.class));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
