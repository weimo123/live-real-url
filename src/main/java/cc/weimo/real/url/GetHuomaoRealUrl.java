package cc.weimo.real.url;

import cc.weimo.real.url.util.MD5Util;
import cc.weimo.real.url.util.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取火猫直播的真实流媒体地址。
 */
public class GetHuomaoRealUrl {

    private static final Pattern PATTERN = Pattern.compile("(?<=var channelOneInfo = )(\\{.*\\})(?=;)");

    private static String get_real_url(String rid) throws NoSuchAlgorithmException {
        String room_url = "https://www.huomao.com/" + rid;
        String response = HttpRequest.create(room_url)
                .get().getBody();
        Matcher matcher = PATTERN.matcher(response);
        if (!matcher.find()) {
            return "直播间不存在";
        }
        String result = matcher.group();
        JSONObject jsonObject = JSONObject.parseObject(result);
        String video_id = jsonObject.getString("stream");

        long time = System.currentTimeMillis();
        String tag_from = "huomaoh5room";
        String sign_context = video_id + tag_from + time + "6FE26D855E1AEAE090E243EB1AF73685";
        String token = MD5Util.md5String(sign_context);

        JSONObject bodyJson = HttpRequest.create("https://www.huomao.com/swf/live_data")
                .appendParameter("streamtype", "live")
                .appendParameter("VideoIDS", video_id)
                .appendParameter("time", time)
                .appendParameter("cdns", 1)
                .appendParameter("from", tag_from)
                .appendParameter("token", token)
                .post().getBodyJson();

        if (!"1".equals(bodyJson.getString("roomStatus"))) {
            return "未开播";
        }

        JSONArray streamList = bodyJson.getJSONArray("streamList");
        for (int i = 0; i < streamList.size(); i++) {
            JSONObject object = streamList.getJSONObject(i);
            if (object.getIntValue("default") == 1) {
                return object.getJSONArray("list").getJSONObject(0).getString("cc/weimo/real/url");
            }
        }

        return null;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入火猫房间号：");
        String rid = scanner.next();
        String real_url = get_real_url(rid);
        System.out.println("该直播间源地址为：\n" + real_url);
    }
}
