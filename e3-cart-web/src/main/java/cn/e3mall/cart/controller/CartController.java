package cn.e3mall.cart.controller;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;

    @RequestMapping("/cart/add/{itemId}")
    public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue = "1") Integer num, HttpServletRequest request, HttpServletResponse response){
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null){

            cartService.addCart(user.getId(), itemId, num);

            return "cartSuccess";
        }

        //        从cookie中取购物车列表
        List<TbItem> listFromCookie = getListFromCookie(request);
        boolean flag = false;
        for (TbItem item: listFromCookie
             ) {
            if (item.getId() == itemId.longValue()){
                flag = true;
                item.setNum(item.getNum()+num);
                break;
            }
        }
        if (!flag){
            TbItem tbItem = itemService.getItemById(itemId);
            tbItem.setNum(num);
//            取一张图
            String image = tbItem.getImage();
            if (StringUtils.isNotBlank(image)){
                tbItem.setImage(image.split(",")[0]);
            }
            listFromCookie.add(tbItem);
        }

//        写入cookie
        CookieUtils.setCookie(request,response,"e3-cart",JsonUtils.objectToJson(listFromCookie),700000,true);
        return "cartSuccess";
    }

    private List<TbItem> getListFromCookie(HttpServletRequest request){
        String json = CookieUtils.getCookieValue(request, "e3-cart",true);
        if (StringUtils.isBlank(json)){
            return new ArrayList<>();
        }
        List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
        return list;
    }

    @RequestMapping("/cart/cart")
    public String showCart(HttpServletRequest request, Model model,HttpServletResponse response){

        List<TbItem> listFromCookie = getListFromCookie(request);

        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null){
            cartService.mergeCart(user.getId(),listFromCookie);
            CookieUtils.deleteCookie(request,response, "CART");
            List<TbItem> cartList = cartService.getCartList(user.getId());
            model.addAttribute("cartList",cartList);
            return "cart";
        }

        model.addAttribute("cartList",listFromCookie);
        return "cart";
    }

    @RequestMapping(value = "/cart/update/num/{itemId}/{num}",method = RequestMethod.POST)
    @ResponseBody
    public E3Result updateCartNum(@PathVariable Long itemId,@PathVariable Integer num,HttpServletRequest request,HttpServletResponse response){

        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null){
            cartService.updateCartNum(user.getId(),itemId,num);
            return E3Result.ok();
        }

        List<TbItem> listFromCookie = getListFromCookie(request);

        for (TbItem tbitem: listFromCookie
             ) {
            if (tbitem.getId() == itemId.longValue()){
                tbitem.setNum(num);
                break;
            }
        }

        CookieUtils.setCookie(request,response,"e3-cart",JsonUtils.objectToJson(listFromCookie),700000,true);
        return E3Result.ok();
    }

    @RequestMapping("/cart/delete/{itemId}")
    public  String deleteCartItem(@PathVariable Long itemId,HttpServletRequest request,HttpServletResponse response) {

        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null){
            cartService.deleteCartItem(user.getId(),itemId);
            return "redirect:/cart/cart.html";
        }
        List<TbItem> listFromCookie = getListFromCookie(request);
        for (TbItem tbitem: listFromCookie
        ) {
            if (tbitem.getId().longValue() == itemId){
                listFromCookie.remove(tbitem);
                break;
            }
        }
        CookieUtils.setCookie(request,response,"e3-cart",JsonUtils.objectToJson(listFromCookie),700000,true);
        return "redirect:/cart/cart.html";
    }

}
