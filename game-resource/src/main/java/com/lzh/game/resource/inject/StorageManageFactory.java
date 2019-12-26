package com.lzh.game.resource.inject;

import com.lzh.game.resource.Storage;
import com.lzh.game.resource.data.ResourceManageHandler;
import com.lzh.game.resource.data.ResourceModel;
import com.lzh.game.resource.data.ResourceModelFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StorageManageFactory implements FactoryBean<DefaultStorageManage> {

    @Autowired
    private ResourceManageHandler resourceManageHandler;

    @Autowired
    private ResourceModelFactory resourceModelFactory;

    @Override
    public DefaultStorageManage getObject() throws Exception {

        DefaultStorageManage manage = new DefaultStorageManage(resourceManageHandler);

        for (ResourceModel model: resourceModelFactory) {
            Class<?> type = model.getDataType();
            if (manage.containStorage(type)) {
               continue;
            }
            Storage storage = manage.createStorage(type);
            manage.putStorage(type, storage);
        }

        return manage;
    }

    @Override
    public Class<DefaultStorageManage> getObjectType() {
        return DefaultStorageManage.class;
    }

}
