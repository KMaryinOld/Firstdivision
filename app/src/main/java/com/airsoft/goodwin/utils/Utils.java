package com.airsoft.goodwin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;

import com.airsoft.goodwin.Chat.Message;
import com.airsoft.goodwin.Duty.DutyActivity;
import com.airsoft.goodwin.Duty.DutyPosition;
import com.airsoft.goodwin.Inventory.InventoryThing;
import com.airsoft.goodwin.Linen.LinenHistoryRecord;
import com.airsoft.goodwin.UserInfo.UserInfo;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Utils {
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    public static Hashtable<String, String> readAtributes(Element data, String tag) {
        Hashtable<String, String> dataReaded = new Hashtable<>();
        NodeList nodelist = data.getElementsByTagName(tag);

        for (int i = 0; i < nodelist.getLength(); i++) {
            Node n = nodelist.item(i);
            if (n instanceof Element && n.hasAttributes()) {
                NamedNodeMap attrs = n.getAttributes();
                for (int j = 0; j < attrs.getLength(); j++) {
                    Attr attribute = (Attr) attrs.item(j);
                    dataReaded.put(attribute.getName(), attribute.getValue());
                }
            }
        }
        return dataReaded;
    }

    public static Document getDomElement(String xml) {
        Document doc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setCoalescing(true);
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        return doc;
    }

    public static UserInfo parsePersonalOfficeUserInfo(String XMLParams) {
        Document doc = Utils.getDomElement(XMLParams);
        Node el = doc.getChildNodes().item(0);
        return parseUserInfo(el);
    }

    public static Map<Integer, ArrayList<UserInfo>> parseDutyUsersInfo(String XMLParams) {
        Map<Integer, ArrayList<UserInfo>> dutyUsersInfo = new HashMap<>();

        Document doc = Utils.getDomElement(XMLParams);
        Node content = doc.getChildNodes().item(0);

        NodeList dutyNodes = content.getChildNodes();

        for (int i = 0; i < dutyNodes.getLength(); ++i) {
            if (!(dutyNodes.item(i) instanceof Element)) {
                continue;
            }
            int dutyType = -1;
            switch (((Element) dutyNodes.item(i)).getTagName()) {
                case "unduty":
                    dutyType = DutyActivity.UNDUTY;
                    break;
                case "coy":
                    dutyType = DutyActivity.COY_DUTY;
                    break;
                case "division":
                    dutyType = DutyActivity.DIVISION_DUTY;
                    break;
                case "oxpaha":
                    dutyType = DutyActivity.OXPAHA_DUTY;
                    break;
            }
            if (dutyType == -1) {
                continue;
            }
            ArrayList<UserInfo> users = new ArrayList<>();
            NodeList usersNodes = dutyNodes.item(i).getChildNodes();
            for (int j = 0; j < usersNodes.getLength(); ++j) {
                if (!(usersNodes.item(j) instanceof Element)) {
                    continue;
                }
                users.add(parseUserInfo(usersNodes.item(j)));
            }
            dutyUsersInfo.put(dutyType, users);
        }

        return dutyUsersInfo;
    }

    public static Map<Integer, DutyPosition> parseDutyPositions(String XMLParams) {
        Map<Integer, DutyPosition> dutyPositions = new HashMap<>();

        Document doc = Utils.getDomElement(XMLParams);
        Node content = doc.getChildNodes().item(0);
        NodeList dutyNodes = content.getChildNodes();

        for (int i = 0; i < dutyNodes.getLength(); ++i) {
            if (!(dutyNodes.item(i) instanceof Element)) {
                continue;
            }

            if (!((Element) dutyNodes.item(i)).getTagName().equals("positions")) {
                continue;
            }

            NodeList positionNodes = dutyNodes.item(i).getChildNodes();
            for(int p = 0; p < positionNodes.getLength(); ++p) {
                if (!(positionNodes.item(p) instanceof Element)) {
                    continue;
                }
                DutyPosition position = new DutyPosition();
                position.id = Integer.parseInt(((Element)positionNodes.item(p)).getElementsByTagName("id")
                        .item(0).getTextContent());
                position.name = ((Element)positionNodes.item(p)).getElementsByTagName("name").item(0).getTextContent();
                position.dutyId = Integer.parseInt(((Element)positionNodes.item(p)).getElementsByTagName("duty_id")
                        .item(0).getTextContent());

                dutyPositions.put(position.id, position);
            }
        }

        return dutyPositions;
    }

    public static UserInfo parseUserInfo(Node userInfoElement) {
        UserInfo userInfo = new UserInfo();

        NodeList childs = userInfoElement.getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            Element item = (Element)childs.item(i);
            switch (item.getTagName()) {
                case "user_info":
                    Node idNode = (item).getElementsByTagName("id").item(0);
                    userInfo.id = idNode.getTextContent();

                    Node firstnameNode = (item).getElementsByTagName("firstname").item(0);
                    userInfo.firstname = firstnameNode.getTextContent();

                    Node middlenameNode = (item).getElementsByTagName("middlename").item(0);
                    userInfo.middlename = middlenameNode.getTextContent();

                    Node lastnameNode = (item).getElementsByTagName("lastname").item(0);
                    userInfo.lastname = lastnameNode.getTextContent();

                    Node rankNode = (item).getElementsByTagName("rank").item(0);
                    userInfo.rank = rankNode.getTextContent();

                    Node emailNode = (item).getElementsByTagName("email").item(0);
                    userInfo.email = emailNode.getTextContent();

                    Node personalNumberNode = (item).getElementsByTagName("personalnumber").item(0);
                    userInfo.personalNumber = personalNumberNode.getTextContent();

                    Node dateregistrationNumberNode = (item).getElementsByTagName("dateregistration").item(0);
                    userInfo.dateregistration = dateregistrationNumberNode.getTextContent();
                    break;
                case "additional_info" :
                    Node nicknameNode = (item).getElementsByTagName("nickname").item(0);
                    userInfo.nickname = nicknameNode.getTextContent();

                    Node birthNode = (item).getElementsByTagName("date_birth").item(0);
                    userInfo.birth = birthNode.getTextContent();

                    Node nationNode = (item).getElementsByTagName("nation").item(0);
                    userInfo.nation = nationNode.getTextContent();

                    Node gradNode = (item).getElementsByTagName("grad").item(0);
                    userInfo.grad = gradNode.getTextContent();

                    Node unitNode = (item).getElementsByTagName("unit").item(0);
                    userInfo.unit = unitNode.getTextContent();

                    Node positionNode = (item).getElementsByTagName("position").item(0);
                    userInfo.position = positionNode.getTextContent();

                    Node batteryNode = (item).getElementsByTagName("battery").item(0);
                    userInfo.battery = batteryNode.getTextContent();

                    Node telnumNode = (item).getElementsByTagName("telnum").item(0);
                    userInfo.telnum = telnumNode.getTextContent();
                    break;
                case "allowance":

                    break;
                case "photo":
                    Node photoPathNode = (item).getElementsByTagName("filepath").item(0);
                    userInfo.photoPath = photoPathNode.getTextContent();
                    break;
                case "role":

                    break;
                case "capabilities":
                    NodeList capabilities = (item).getElementsByTagName("capability");
                    for (int c = 0; c < capabilities.getLength(); ++c) {
                        Node node = capabilities.item(c);
                        String str = node.getTextContent();
                        userInfo.capabilities.add(str);
                    }
                    break;
                case "color":
                    userInfo.dutyColor = Integer.parseInt(item.getTextContent());
                    break;
                case "position_id":
                    userInfo.dutyPosition = Integer.parseInt(item.getTextContent());
                    break;
            }
        }

        return userInfo;
    }

    public static List<UserInfo> parseDialogUsersList(String XMLParams) {
        List<UserInfo> usersList = new ArrayList<>();
        Document doc = Utils.getDomElement(XMLParams);
        NodeList childs = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            Element item = (Element)childs.item(i);
            NodeList users = item.getChildNodes();
            for (int u = 0; u < users.getLength(); u++) {
                if (!(users.item(u) instanceof Element)) {
                    continue;
                }
                usersList.add(parseUserInfo(users.item(u)));
            }
        }

        return usersList;
    }

    public static Map<Integer, String> parseLinenTypes(String XMLParams) {
        Map<Integer, String> linenTypes = new HashMap<>();

        Document doc = Utils.getDomElement(XMLParams);
        NodeList childs = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            Element item = (Element)childs.item(i);
            if (item.getTagName().equals("types")) {
                NodeList xmlLinenTypes = item.getElementsByTagName("type");
                for (int t = 0; t < xmlLinenTypes.getLength(); ++t) {
                    Element linenTypeNode = (Element) xmlLinenTypes.item(t);
                    Integer id = Integer.parseInt(linenTypeNode.getElementsByTagName("id").item(0).getTextContent());
                    String name = linenTypeNode.getElementsByTagName("name").item(0).getTextContent();

                    linenTypes.put(id, name);
                }
            }
        }

        return linenTypes;
    }

    public static ArrayList<LinenHistoryRecord> parseLinenHistory(String XMLParams) {
        ArrayList<LinenHistoryRecord> records = new ArrayList<>();

        Document doc = Utils.getDomElement(XMLParams);
        NodeList childs = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            Element item = (Element)childs.item(i);
            if (item.getTagName().equals("history")) {
                NodeList xmlHistoryRecords = item.getElementsByTagName("history_record");
                for (int t = 0; t < xmlHistoryRecords.getLength(); ++t) {
                    Element historyRecordNode = (Element) xmlHistoryRecords.item(t);
                    LinenHistoryRecord historyRecord = new LinenHistoryRecord();
                    historyRecord.type = Integer.parseInt(historyRecordNode.getElementsByTagName("type").item(0).getTextContent());
                    String time = historyRecordNode.getElementsByTagName("date").item(0).getTextContent();
                    long timestampLong = Long.parseLong(time)*1000;
                    Date d = new Date(timestampLong);
                    historyRecord.date = Calendar.getInstance();
                    historyRecord.date.setTime(d);

                    NodeList recordItems = historyRecordNode.getElementsByTagName("items").item(0).getChildNodes();
                    for (int itemId = 0; itemId < recordItems.getLength(); ++itemId) {
                        if (!(recordItems.item(itemId) instanceof Element)) {
                            continue;
                        }
                        Element recordItemNode = (Element) recordItems.item(itemId);
                        historyRecord.items.put(Integer.parseInt(recordItemNode.getAttribute("id")),
                                Integer.parseInt(recordItemNode.getTextContent()));
                    }

                    records.add(historyRecord);
                }
            }
        }

        return records;
    }

    public static List<UserInfo> parseComradesList(String XMLParams) {
        List<UserInfo> users = new ArrayList<>();

        Document doc = Utils.getDomElement(XMLParams);
        NodeList childs = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            users.add(parseUserInfo(childs.item(i)));
        }

        return users;
    }

    public static List<Message> parseMessagesList(String XMLParams) {
        List<Message> messages = new ArrayList<>();

        Document doc = Utils.getDomElement(XMLParams);
        NodeList childs = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            messages.add(parseMessageInfo(childs.item(i)));
        }

        return messages;
    }

    public static Message parseMessageInfo(Node messageElement) {
        Message message = new Message();

        NodeList childs = messageElement.getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            Element item = (Element)childs.item(i);
            switch (item.getTagName()) {
                case "id":
                    message.id = Integer.parseInt(item.getTextContent());
                    break;
                case "user_id":
                    message.userId = Integer.parseInt(item.getTextContent());
                    break;
                case "message_text":
                    message.text = item.getTextContent();
                    break;
                case "firstname":
                    message.firstname = item.getTextContent();
                    break;
                case "middlename":
                    message.middlename = item.getTextContent();
                    break;
                case "lastname":
                    message.lastname = item.getTextContent();
                    break;
                case "photo":
                    message.photo = item.getTextContent();
                    break;
            }
        }
        return message;
    }

    public static List<InventoryThing> parseInventoryItems(String XMLParams) {
        List<InventoryThing> items = new ArrayList<>();

        Document doc = Utils.getDomElement(XMLParams);
        NodeList childs = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            if (((Element) childs.item(i)).getTagName().equals("things")) {
                NodeList things = childs.item(i).getChildNodes();
                for (int j = 0; j < things.getLength(); ++j) {
                    if (!(things.item(j) instanceof Element)) {
                        continue;
                    }
                    items.add(parseInventoryThing(things.item(j)));
                }
            }
        }

        return items;
    }

    public static InventoryThing parseInventoryThing(Node inventoryNode) {
        InventoryThing inventoryItem = new InventoryThing();

        NodeList childs = inventoryNode.getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            Element item = (Element)childs.item(i);
            switch (item.getTagName()) {
                case "id":
                    inventoryItem.id = Integer.parseInt(item.getTextContent());
                    break;
                case "user_id":
                    inventoryItem.userId = Integer.parseInt(item.getTextContent());
                    break;
                case "equipped_user_id":
                    inventoryItem.equippedUserId = Integer.parseInt(item.getTextContent());
                    break;
                case "timeissued":
                    inventoryItem.timeIssued = Integer.parseInt(item.getTextContent());
                    break;
                case "is_equipped":
                    inventoryItem.isEquipped = item.getTextContent().equals("1");
                    break;
                case "thing_id":
                    inventoryItem.thingId = Integer.parseInt(item.getTextContent());
                    break;
                case "thing_name":
                    inventoryItem.thingName = item.getTextContent();
                    break;
                case "thing_type_id":
                    inventoryItem.thingTypeId = Integer.parseInt(item.getTextContent());
                    break;
                case "thing_photo_filename":
                    inventoryItem.thingPhotoFilename = item.getTextContent();
                    break;
            }
        }
        return inventoryItem;
    }

    public static Map<Integer, UserInfo> parseInventoryUsers(String XMLParams) {
        Map<Integer, UserInfo> users = new HashMap<>();

        Document doc = Utils.getDomElement(XMLParams);
        NodeList childs = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (!(childs.item(i) instanceof Element)) {
                continue;
            }
            if (((Element) childs.item(i)).getTagName().equals("users")) {
                NodeList userNodes = childs.item(i).getChildNodes();
                for (int j = 0; j < userNodes.getLength(); ++j) {
                    if (!(userNodes.item(j) instanceof Element)) {
                        continue;
                    }
                    UserInfo userInfo = parseUserInfo(userNodes.item(j));
                    users.put(Integer.parseInt(userInfo.id), userInfo);
                }
            }
        }

        return users;
    }

    public static boolean isErrorResponse(String XMLParams) {
        Document doc = Utils.getDomElement(XMLParams);
        try {
            Element content = (Element) doc.getFirstChild();
            return content.getElementsByTagName("error").getLength() > 0;
        } catch (NullPointerException e) {
            return true;
        }
    }

    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static String getTimestampFromCalendar(Calendar pickedDate) {
        Calendar date = (Calendar) pickedDate.clone();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        //date.add(Calendar.HOUR, -1);
        Long tsLong = date.getTimeInMillis() / 1000;
        return tsLong.toString();
    }

    public static String getDate(long timeStamp){

        try{
            DateFormat sdf = new SimpleDateFormat("M dd yyyy", Locale.GERMAN);
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }
}
