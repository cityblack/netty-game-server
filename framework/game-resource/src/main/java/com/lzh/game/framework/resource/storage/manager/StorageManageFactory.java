package com.lzh.game.framework.resource.storage.manager;

import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import com.lzh.game.framework.resource.data.meta.ResourceMetaManager;
import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.resource.storage.StorageFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

public class StorageManageFactory implements FactoryBean<StorageManager> {

    @Autowired
    private ResourceMetaManager resourceModelMeta;

    @Autowired
    private StorageFactory storageFactory;

    @Override
    public StorageManager getObject() throws Exception {

        var manage = new DefaultStorageManager(storageFactory);

        for (ResourceMeta<?> meta : resourceModelMeta) {
            Class<?> type = meta.getDataType();
            if (manage.containStorage(type)) {
                continue;
            }
            Storage<?, ?> storage = manage.createStorage(meta);
            manage.registerStore(type, storage);
            storage.reload();
        }

        return manage;
    }

    @Override
    public Class<DefaultStorageManager> getObjectType() {
        return DefaultStorageManager.class;
    }

}
