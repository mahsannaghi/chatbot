package com.paya.EncouragementService.enumeration;

import org.springframework.stereotype.Component;

@Component
public class RoleConstant {
    // All roles in pleaser service
    public enum ROLE {
        PERSONNEL("0", "personnel"),
        EXECUTIVE_MANAGER("1", "executive_manager"),
        ENCOURAGEMENT_SPECIALIST("2", "encouragement_specialist"),
        HUMAN_RESOURCE_COMMISSION_ADMIN("3", "human_resource_commission_admin");

        private final String value;
        private final String keyName;

        ROLE(String value, String keyName) {
            this.value = value;
            this.keyName = keyName;
        }

        public String getValue() {
            return value;
        }

        public String getKeyName(){
            return keyName;
        }

        public static ROLE fromValue(String value) {
            for (ROLE role : ROLE.values()) {
                if (role.getValue().equals(value)) {
                    return role;
                }
            }
            return null;
        }

        public static ROLE fromKeyName(String keyName) {
            for (ROLE role : ROLE.values()) {
                if (role.getKeyName().equals(keyName)) {
                    return role;
                }
            }
            return null;
        }

    }
}
