package com.lzh.game.framework.resource.inject;

import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.resource.data.ResourceManager;
import com.lzh.game.framework.resource.data.ResourceMeta;
import com.lzh.game.framework.resource.data.ResourceMetaManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StorageManageFactory implements FactoryBean<DefaultStorageManager> {

    @Autowired
    private ResourceManager resourceManageHandle;

    @Autowired
    private ResourceMetaManager resourceModelMeta;

    @Override
    public DefaultStorageManager getObject() throws Exception {

        DefaultStorageManager manage = new DefaultStorageManager(resourceManageHandle);

        for (ResourceMeta model: resourceModelMeta) {
            Class<?> type = model.getDataType();
            if (manage.containStorage(type)) {
               continue;
            }
            Storage storage = manage.createStorage(type);
            manage.registerStore(type, storage);
        }

        return manage;
    }

    @Override
    public Class<DefaultStorageManager> getObjectType() {
        return DefaultStorageManager.class;
    }

}
