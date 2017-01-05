package com.wakehao.bar;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.XmlRes;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WakeHao on 2017/1/5.
 */

public class ItemParser {
    private Context context;
    private List<BottomNavigationItem> items=new ArrayList<>();
    private BottomNavigationItem item;

    public ItemParser(Context context) {
        this.context = context;
    }

    public void parser(@XmlRes int res)
    {
        XmlResourceParser parser = context.getResources().getXml(res);

        try {
            parser.next();
            int eventType = parser.getEventType();
            while(eventType!=XmlResourceParser.END_DOCUMENT)
            {
                if(eventType==XmlResourceParser.START_TAG&&parser.getName().equals("item")) {
                    parseItem(parser);
                }
                else if(eventType==XmlResourceParser.END_TAG){
                    if(parser.getName().equals("item")){
                        if(item!=null){
                            items.add(item);
                            item=null;
                        }
                    }
                }

                eventType=parser.next();
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseItem(XmlResourceParser parser) {
        if(item==null){
            getDefaultItem();
        }
        int attributeCount = parser.getAttributeCount();
            for(int i=0;i<attributeCount;i++){
            switch (parser.getAttributeName(i)){
                case "id":

                    break;
                case "icon":
                    break;
                case "title":

                    item.setTitle(getTitleText(i,parser));
                    break;
            }
        }

    }


    private void getDefaultItem() {
        item=new BottomNavigationItem();

    }

    private String getTitleText(int attrIndex, XmlResourceParser parser) {
        int attributeResourceValue = parser.getAttributeResourceValue(attrIndex, 0);

        if(attributeResourceValue!=0){
            Log.i("test","refrence!");
            return context.getString(attributeResourceValue);
        }
        Log.i("test","not refrence!");
        return parser.getAttributeValue(attrIndex);
    }


    public List<BottomNavigationItem> getBottomNavigationItems(){
        return items;
    }

}
