package com.streamreduce;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.code.morphia.Datastore;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.util.JSON;
import com.streamreduce.core.dao.DAODatasourceType;
import com.streamreduce.core.dao.GenericCollectionDAO;

public class MongoNumberIT extends AbstractServiceTestCase {

    @Autowired
    @Qualifier(value = "businessDBDatastore")
    private Datastore businessDatastore;
    
    @Autowired
    private GenericCollectionDAO gcdao;

    @Test
    public void testNumberFidelity() {
        String json = "{\"min\":" + Constants.PERIOD_MINUTE
            + ",\"hour\":" + Constants.PERIOD_HOUR
            + ",\"day\":" + Constants.PERIOD_DAY
            + ",\"week\":" + Constants.PERIOD_WEEK
            + ",\"month\":" + Constants.PERIOD_MONTH
            + ",\"crap\":" + Constants.PERIOD_WEEK * 30
            + ",\"foo\":\"foo\"}";
        
        BasicDBObject newPayloadObject = (BasicDBObject) JSON.parse(json);
        gcdao.createCollectionEntry(DAODatasourceType.BUSINESS, getClass().toString(), newPayloadObject);
        
        DB db = businessDatastore.getDB();
        DBCollection collection = db.getCollection(getClass().toString());
        BasicDBObject results = (BasicDBObject) collection.findOne(new BasicDBObject("foo", "foo"));
        
        assertTrue( ((Integer) results.get("min")).longValue() == Constants.PERIOD_MINUTE);
        assertTrue( ((Integer) results.get("hour")).longValue() == Constants.PERIOD_HOUR);
        assertTrue( ((Integer) results.get("day")).longValue() == Constants.PERIOD_DAY);
        assertTrue( ((Integer) results.get("week")).longValue() == Constants.PERIOD_WEEK);
        assertTrue( (Long) results.get("crap") == Constants.PERIOD_WEEK * 30);
        assertTrue( (Long) results.get("month") == Constants.PERIOD_MONTH);
        
        String valMin = results.get("min").toString();
        String valHour = results.get("hour").toString();
        String valDay = results.get("day").toString();
        String valWeek = results.get("week").toString();
        String valMonth = results.get("month").toString();
        String valCrap = results.get("crap").toString();
        
        System.out.println("valMin: " + valMin);
        System.out.println("valHour: " + valHour);
        System.out.println("valDay: " + valDay);
        System.out.println("valWeek: " + valWeek);
        System.out.println("valMonth: " + valMonth);
        System.out.println("valCrap: " + valCrap);
        
        assertTrue( Long.valueOf(valMin) == Constants.PERIOD_MINUTE);
        assertTrue( Long.valueOf(valHour) == Constants.PERIOD_HOUR);
        assertTrue( Long.valueOf(valDay) == Constants.PERIOD_DAY);
        assertTrue( Long.valueOf(valWeek) == Constants.PERIOD_WEEK);
        assertTrue( Long.valueOf(valMonth) == Constants.PERIOD_MONTH);
        assertTrue( Long.valueOf(valCrap) == Constants.PERIOD_WEEK * 30);
        
    }
}