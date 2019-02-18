package cn.e3mall.search.service.impl;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.mapper.ItemMapper;
import cn.e3mall.search.service.SearchItemService;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SearchItemServiceImpl implements SearchItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrServer solrServer;

    @Override
    public E3Result importAllItems() {
        List<SearchItem> itemList = itemMapper.getItemList();

        try {
            for (SearchItem item:itemList
            ) {
                SolrInputDocument solrInputDocument = new SolrInputDocument();
                solrInputDocument.addField("id",item.getId());
                solrInputDocument.addField("item_title",item.getTitle());
                solrInputDocument.addField("item_sell_point",item.getSell_point());
                solrInputDocument.addField("item_price",item.getPrice());
                solrInputDocument.addField("item_image",item.getImage());
                solrInputDocument.addField("item_category_name",item.getCategory_name());
                solrServer.add(solrInputDocument);
            }

            solrServer.commit();
            return E3Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return E3Result.build(500,"数据导入异常");
        }


    }

    @Override
    public E3Result addDocument(Long itemId) throws Exception {
        SearchItem searchItem = itemMapper.getItemById(itemId);
        SolrInputDocument document = new SolrInputDocument();
        // 3、使用SolrServer对象写入索引库。
        document.addField("id", searchItem.getId());
        document.addField("item_title", searchItem.getTitle());
        document.addField("item_sell_point", searchItem.getSell_point());
        document.addField("item_price", searchItem.getPrice());
        document.addField("item_image", searchItem.getImage());
        document.addField("item_category_name", searchItem.getCategory_name());
        // 5、向索引库中添加文档。
        solrServer.add(document);
        solrServer.commit();
        // 4、返回成功，返回e3Result。
        return E3Result.ok();
    }


}
