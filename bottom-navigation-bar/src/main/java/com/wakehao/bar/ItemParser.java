package com.wakehao.bar;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.support.annotation.XmlRes;
import android.support.v4.content.ContextCompat;
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
    private BottomNavigationItem.Config config;

    public ItemParser(Context context,BottomNavigationItem.Config config) {
        this.context = context;
        this.config=config;
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
            item=getDefaultItem();
        }
        int attributeCount = parser.getAttributeCount();
            for(int i=0;i<attributeCount;i++){
            switch (parser.getAttributeName(i)){
                case "id":

                    break;
                case "icon":
                    item.setIconRes(parser.getAttributeResourceValue(i,0));
                    break;
                case "icon2":
                    item.setIconResSelected(parser.getAttributeResourceValue(i,0));
                    break;
                case "title":

                    item.setTitle(getTitleText(i,parser));
                    break;
                case "shiftedColor":
                    //shift mode
                    if(config.getSwitchMode()==1)
                    {
                        Integer shiftedColor=getColor(i,parser);
                        if(shiftedColor!=null)item.setShiftedColor(shiftedColor);
                    }
                    break;
            }
        }

    }




    private BottomNavigationItem getDefaultItem() {
        BottomNavigationItem bar=new BottomNavigationItem(context);
        bar.setConfig(config);
        return bar;
    }

    private String getTitleText(int attrIndex, XmlResourceParser parser) {
        int attributeResourceValue = parser.getAttributeResourceValue(attrIndex, 0);

        if(attributeResourceValue!=0){
            return context.getString(attributeResourceValue);
        }
        return parser.getAttributeValue(attrIndex);
    }
    private Integer getColor(int i, XmlResourceParser parser) {
        int colorResourceValue = parser.getAttributeResourceValue(i, 0);
        if(colorResourceValue!=0){
            return ContextCompat.getColor(context,colorResourceValue);
        }
        try {
            return Color.parseColor(parser.getAttributeValue(i));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public List<BottomNavigationItem> getBottomNavigationItems(){
        return items;
    }

}
