package in.nikitapek.chestempty.util;

import com.amshulman.mbapi.util.PermissionsEnum;

public enum Commands implements PermissionsEnum {
    CHESTEMPTY;

    @Override
    public String getPrefix() {
        return "chestempty.";
    }
}
