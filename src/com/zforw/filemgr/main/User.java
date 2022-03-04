package com.zforw.filemgr.main;

/**
 * @version: 1.0
 * @author: zforw
 * @date: 2022/02/21 11:28 下午
 * @project: FIleMgr
 * @description:
 */
public class User {
    private boolean privilege;
    User() {
        privilege = false;
    }
    public boolean login(String acc, String pwd) {
        if("admin".equals(acc) && "admin".equals(pwd)) {
            return privilege = true;
        } else if("123".equals(acc) && "123".equals(pwd)) {
            privilege = false;
            return true;
        } else {
            return false;
        }
    }
    boolean getPrivilege() {
        return getPrivilege();
    }
}
