package ca.cmpt276.votosurveytaker.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;
/**
 * 
 * Parses notification drawer layout xml file
 *
 */
public class DrawerXmlParser {
	
    private static final String ns = null;
    
    private Context context;
    
    public DrawerXmlParser(Context context) {
    	this.context = context;
    }
    
    public List<DrawerItem> getDrawerItems(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    
    private List<DrawerItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<DrawerItem> entries = new ArrayList<DrawerItem>();

        parser.require(XmlPullParser.START_TAG, ns, "drawer");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                entries.add(readItem(parser));
            } else {
                skip(parser);
            }
        }  
        return entries;
    }

	private DrawerItem readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
		String title = null;
		Integer id = 0;
		Boolean isHeader = false;
		parser.require(XmlPullParser.START_TAG, ns, "item");
		String tag = parser.getName();
		String relType = parser.getAttributeValue(null, "type");  
		if (tag.equals("item")) {
		    if (relType.equals("header")) {
		    	isHeader = true;
		    }
		    if (relType.equals("img")) {
				String src = parser.getAttributeValue(null, "src");
				if(src != null) {
					id = context.getResources().getIdentifier(src, "drawable", context.getPackageName());
				}
		    }
		}
		title = readText(parser);
		int titleId = context.getResources().getIdentifier(title, "string", context.getPackageName());
		parser.require(XmlPullParser.END_TAG, ns, "item");
		return new DrawerItem(context.getString(titleId), id, isHeader);
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
}