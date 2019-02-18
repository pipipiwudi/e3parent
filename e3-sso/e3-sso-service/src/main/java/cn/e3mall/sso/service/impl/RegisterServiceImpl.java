package cn.e3mall.sso.service.impl;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.sso.service.RegisterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Override
    public E3Result checkData(String param, int type) {
        TbUserExample  example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        if (type ==1){
            criteria.andUsernameEqualTo(param);
        }else if (type ==2)
        {
            criteria.andPhoneEqualTo(param);
        }else if (type==3){
            criteria.andEmailEqualTo(param);
        }else {
            return E3Result.build(400,"参数错误");
        }
        List<TbUser> list = tbUserMapper.selectByExample(example);
        if (list != null && list.size()>0){
            return E3Result.ok(false);
        }

        return E3Result.ok(true);
    }

    @Override
    public E3Result register(TbUser tbUser) {
        if (StringUtils.isBlank(tbUser.getUsername())|| StringUtils.isBlank(tbUser.getPassword()) || StringUtils.isBlank(tbUser.getPhone())){
            return E3Result.build(400,"数据不完整 插入失败");
        }
        E3Result e3Result = checkData(tbUser.getUsername(), 1);
        if (!(boolean)e3Result.getData()){
            return E3Result.build(400,"此用户名已存在");
        }

        E3Result e3Result1 = checkData(tbUser.getPhone(), 2);
        if (!(boolean)e3Result1.getData()){
            return E3Result.build(400,"此号码已注册");
        }

        tbUser.setCreated(new Date());
        tbUser.setUpdated(new Date());
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
        tbUser.setPassword(md5DigestAsHex);
        tbUserMapper.insert(tbUser);
        return E3Result.ok();
    }
}
