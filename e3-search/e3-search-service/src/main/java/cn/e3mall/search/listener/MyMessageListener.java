package cn.e3mall.search.listener;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.service.impl.SearchItemServiceImpl;
import cn.e3mall.search.service.impl.SearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener {
    @Autowired
    private SearchItemServiceImpl searchItemServiceImpl;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = null;
            Long itemId = null;
            //取商品id
            if (message instanceof TextMessage) {
                textMessage = (TextMessage) message;
                itemId = Long.parseLong(textMessage.getText());
            }
            //向索引库添加文档
            searchItemServiceImpl.addDocument(itemId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
