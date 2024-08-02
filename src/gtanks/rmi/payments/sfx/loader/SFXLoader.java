package gtanks.rmi.payments.sfx.loader;

import gtanks.logger.Logger;
import gtanks.rmi.payments.sfx.SFX;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SFXLoader {
    public static void load(String config) throws FileNotFoundException, IOException, ParseException {
        load(new File(config));
    }

    public static void load(File file) throws FileNotFoundException, IOException, ParseException {
        Logger.log("Load sfx lighting...");
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject)parser.parse((Reader)(new FileReader(file)));
        SFX.sfx_data = json;
    }
}
