
package com.jayway.volleydemo.data;

import java.util.ArrayList;
import java.util.List;

import com.jayway.volleydemo.domain.server.ServerModel.JsonItem;
import com.jayway.volleydemo.domain.server.ServerModel.JsonList;

public class Repository {

    private static List<JsonList> lists = null;

    public static List<JsonList> getLists() {
        return lists;
    }

    public static JsonItem getChild(int childID, int groupID) {
        return lists.get(groupID).items.get(childID);
    }

    public static void initLists(int numberOfLists) {
        lists = new ArrayList<JsonList>(numberOfLists);
    }

    public static void addList(JsonList list, int order) {
        if (!list.items.isEmpty()) {
            lists.add(list.order, list);
        }
    }

    public static CharSequence getListTitle(int position) {
        return lists.get(position).title;
    }

    public static JsonList getList(int listID) {
        if (listID < lists.size()) {
            return lists.get(listID);
        } else {
            return null;
        }
    }

    public static boolean gotContent() {
        return lists != null;
    }
}
