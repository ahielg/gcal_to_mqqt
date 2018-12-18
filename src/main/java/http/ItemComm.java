package http;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ahielg
 * @date 2018-12-18
 */
public class ItemComm {
    public static final Gson gson = new Gson();
    //@Value("${openhab.url}")
    private String openhabURL;

    //@Value("${openhab.username}")
    private String openhabUsername;

    //@Value("${openhab.password}")
    private String openhabPassword;

    private ItemComm() {
    }

    public ItemComm(String openhabURL, String openhabUsername, String openhabPassword) {
        this.openhabURL = openhabURL;
        this.openhabUsername = openhabUsername;
        this.openhabPassword = openhabPassword;
    }

    public ItemComm(String openhabURL) {
        this.openhabURL = openhabURL;
    }

    public String toItem(Map<String, String> item) {
        //return new Item(item.get("name"), camelToSpace(item.get("name")), item.get("label").toLowerCase(), item.get("link"),            item.get("state"));
        return item.get("name");
    }

    public boolean isTaged(Map item) {
        boolean tagged = false;
        Object tags = item.get("tags");
        if (tags != null) {
            tagged = ((List<String>) tags).contains("Lighting");
        }
        return tagged;
    }


    public List<Map<String, String>> getAllItems() {
        List<Map<String, String>> collect = null;

        try {
            URL url = new URL(openhabURL + "/rest/items?recursive=false");
            URLConnection uc = url.openConnection();

            addAuth(uc);
            InputStream inputStream = uc.getInputStream();
            String allItems = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            collect = ItemComm.gson.fromJson(allItems, ArrayList.class);
        } catch (IOException e) {
            System.err.println("Error getting OH items: " + e.getMessage());
        }

        return collect;
    }

    public boolean updateState(String itemId, String state) {
        boolean result = false;
        try {
            byte[] postData = state.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            String request = openhabURL + "/rest/items/" + itemId + "/state";
            URL url = new URL(request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("charset", "utf-8");

            if (openhabUsername != null) {
                String userPass = openhabUsername + ':' + openhabPassword;
                conn.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder().encode(userPass.getBytes())));
            }

            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
            InputStream content = conn.getInputStream();
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            result = true;
        } catch (IOException e) {
            System.err.println("Error while updating item [" + itemId + "] error: " + e.getMessage());
        }
        return result;
    }

    public Optional<String> getItem(String id, String openhabURL) {
        String result = null;
        try {
            URL url = new URL(openhabURL);
            URLConnection uc = url.openConnection();
            addAuth(uc);
            InputStream inputStream = uc.getInputStream();
            String allItems = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            Map<String, String> item = ItemComm.gson.fromJson(allItems, Map.class);
            result = toItem(item);
        } catch (IOException e) {
            System.err.println("Error while getting item [" + id + "] error: " + e.getMessage());
        }
        return Optional.ofNullable(result);
    }

    private void addAuth(URLConnection uc) {
        if (openhabUsername != null) {
            String userPass = openhabUsername + ':' + openhabPassword;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userPass.getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);
        }
    }

    private String camelToSpace(String label) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1 $2";
        return label.replaceAll(regex, replacement).toLowerCase();
    }
}
