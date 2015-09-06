package cn.appem.bcwl.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * User:wewecn on 2015/6/29 11:16
 * Email:wewecn@qq.com
 */
public class CommonUtil {
//    public static String parseJson(String str) {
//        str = str.replace("\\", "");
//        str = str.substring(1, str.length() - 1);
//        return str;
//    }
//    public static List<T> parseJson(String str) {
//        java.lang.reflect.Type type = new TypeToken<T>() {}.getType();
//        JsonBean jsonBean = gson.fromJson(str, type);
//    }

    public static String getContactPhone(Context context,Cursor cursor) {
        int phoneColumn = cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String result = "";
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人电话的cursor
            Cursor phone = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int index = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                    int typeindex = phone
//                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
//                    int phone_type = phone.getInt(typeindex);
                    String phoneNumber = phone.getString(index);
                    result = phoneNumber;
//                    switch (phone_type) {//此处请看下方注释
//                        case 2:
//                            result = phoneNumber;
//                            break;
//                        default:
//                            break;
//                    }
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        return result;
    }
}
