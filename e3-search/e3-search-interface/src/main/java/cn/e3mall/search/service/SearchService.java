package cn.e3mall.search.service;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.common.utils.E3Result;

public interface SearchService {
    SearchResult search(String keyWord,Integer page,Integer rows) throws Exception;

}
