package com.example.nagel.io1.service.repository;

import android.os.AsyncTask;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ObjectRepository {
    private Map<String,Object> objectCache;

    private final ObjectDao objectDao;

    @Inject
    public ObjectRepository(ObjectDao objectDao) {
        this.objectDao = objectDao;
        DataBus.getBus().register(this);
        objectCache = new HashMap<>();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Object> ls;
                ls = objectDao.getAllObjects();
                if(ls != null){
                    for (Object object : ls) {
                        objectCache.put(object.id, object);
                    }
                }
            }
        });
    }

    @Subscribe
    public void onObjects(final Events.Objects event) {
        try {
            JSONObject data = new JSONObject(event.getData());
            Iterator<String> iter = data.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject object = new JSONObject(data.get(key).toString());
                String type = object.optString("type");
                String common = object.optString("common");
                String nativ = object.optString("native");
                objectDao.insert(new Object(key, type, common, nativ));
                objectCache.put(key, new Object(key, type, common, nativ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Object> getAllObjects(){
        List<Object> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : objectCache.entrySet()) {
            list.add(entry.getValue());
        }

        return list;
    }

    public List<Object> getObjectsByRole(String role){
        List<Object> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : objectCache.entrySet()) {
            if(role.equals(entry.getValue().getRole())) {
                list.add(entry.getValue());
            }
        }

        return list;
    }

    public List<Object> getObjectsByRoom(String room){
        List<Object> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : objectCache.entrySet()) {
            if(room.equals(entry.getValue().getRole())) {
                list.add(entry.getValue());
            }
        }

        return list;
    }

    public List<Object> getObjectsByFunction(String function){
        List<Object> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : objectCache.entrySet()) {
            if(function.equals(entry.getValue().getRole())) {
                list.add(entry.getValue());
            }
        }

        return list;
    }

    public Object getObject(String id){
        return objectCache.get(id);
    }
}
