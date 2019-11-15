package cc.weimo.real.url;

import cc.weimo.real.url.util.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Scanner;

/**
 * 获取哔哩哔哩直播的真实流媒体地址。
 * quality=4默认画质为原画
 */
public class GetBilibiliRealUrl {

    private static JSONObject get_real_rid(String rid) {
        String room_url = "https://api.live.bilibili.com/room/v1/Room/room_init?id=" + rid;
        JSONObject response = HttpRequest.create(room_url).get().getBodyJson();
        JSONObject data = response.getJSONObject("data");
        if (data == null) {
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("live_status", data.getBoolean("live_status"));
        jsonObject.put("room_id", data.getLongValue("room_id"));
        return jsonObject;

    }

    private static String get_real_url(String rid) {
        JSONObject roomInfo = get_real_rid(rid);

        if (roomInfo == null) {
            return "直播间不存在";
        }
        if (!roomInfo.getBoolean("live_status")) {
            return "未开播";
        }
        String room_url = "https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + roomInfo.getLongValue("room_id") + "&platform=h5&otype=json&quality=4";
        JSONObject response = HttpRequest.create(room_url).get().getBodyJson();
        JSONArray durl = response.getJSONObject("data").getJSONArray("durl");
        if (durl != null) {
            String result = durl.getJSONObject(0).getString("url");
            String pattern_result = result.substring(result.indexOf("/live_"), result.indexOf(".m3u8") + ".m3u8".length());
            return "https://cn-hbxy-cmcc-live-01.live-play.acgvideo.com/live-bvc" + pattern_result;
        } else {
            return "疑似部分国外IP无法GET到正确数据，待验证";
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入bilibili房间号：");
        String rid = scanner.next();
        String real_url = get_real_url(rid);
        System.out.println("该直播间源地址为：\n" + real_url);
    }
}
