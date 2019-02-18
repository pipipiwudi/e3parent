package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import org.apache.commons.lang3.StringUtils;

public interface LoginService {
    E3Result login(String username,String password);
}
