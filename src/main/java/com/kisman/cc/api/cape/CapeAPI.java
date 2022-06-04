package com.kisman.cc.api.cape;

import com.kisman.cc.api.util.PasteBinAPI;
import com.kisman.cc.api.util.exception.PasteBinBufferedReaderException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CapeAPI {
    public PasteBinAPI pasteBinAPI;

    public List<String> uuids = new ArrayList<>();

    public static final String URL = "https://pastebin.com/raw/Mjhz9nxW";

    public CapeAPI() {
        try {
            pasteBinAPI = new PasteBinAPI(URL);
            uuids.addAll(pasteBinAPI.get());
        } catch (PasteBinBufferedReaderException ignored) {}
    }

    public boolean is(UUID uuid) {
        return uuids.contains(uuid.toString());
    }
}