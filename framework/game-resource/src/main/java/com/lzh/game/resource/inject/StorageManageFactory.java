package com.lzh.game.resource.inject;

import com.lzh.game.resource.Storage;
import com.lzh.game.resource.data.ResourceManageHandle;
import com.lzh.game.resource.data.ResourceModel;
import com.lzh.game.resource.data.ResourceModelMeta;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StorageManageFactory implements FactoryBean<DefaultStorageManage> {

    @Autowired
    private ResourceManageHandle resourceManageHandle;

    @Autowired
    private ResourceModelMeta resourceModelMeta;

    @Override
    public DefaultStorageManage getObject() throws Exception {

        DefaultStorageManage manage = new DefaultStorageManage(resourceManageHandle);

        for (ResourceModel model: resourceModelMeta) {
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
