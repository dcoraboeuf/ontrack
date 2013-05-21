package net.ontrack.acceptance.support;

import org.apache.commons.lang3.StringUtils;

public class AccSupport {

    public static String getAdminPassword() {
        String pwd = System.getProperty("itAdminPassword");
        if (StringUtils.isNotBlank(pwd)) {
            return pwd;
        } else {
            return "admin";
        }
    }

}
