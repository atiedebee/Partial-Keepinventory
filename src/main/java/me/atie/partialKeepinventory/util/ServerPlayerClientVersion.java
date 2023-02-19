package me.atie.partialKeepinventory.util;

import me.atie.partialKeepinventory.settings.pkiVersion;

public interface ServerPlayerClientVersion {
    pkiVersion getClientPKIVersion();
    void setClientPKIVersion(pkiVersion version);
}
