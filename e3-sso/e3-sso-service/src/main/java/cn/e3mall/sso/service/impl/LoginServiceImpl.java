package cn.e3mall.sso.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.sso.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private JedisClient jedisClient;

    @Override
    public E3Result login(String username, String password) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> list = tbUserMapper.selectByExample(example);
        if (list == null || list.size() == 0){
            return E3Result.build(400,"用户名或密码错误");
        }

        TbUser user = list.get(0);

        if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
            return E3Result.build(400,"用户名或密码错误");
        }
//        生成token
        String token = UUID.randomUUID().toString();

        user.setPassword(null);
//        存入redis
        jedisClient.set("SESSION:"+token, JsonUtils.objectToJson(user));
//        设置过期时间
        jedisClient.expire("SESSION:"+token,1800);

        return E3Result.ok(token);
    }
}
