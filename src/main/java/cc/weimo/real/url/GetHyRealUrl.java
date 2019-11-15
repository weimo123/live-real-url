package cc.weimo.real.url;

import cc.weimo.real.url.util.http.HttpContentType;
import cc.weimo.real.url.util.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取虎牙直播的真实流媒体地址。
 */
public class GetHyRealUrl {

    private static final Pattern PATTERN = Pattern.compile("(?<=hasvedio: ')(.*\\.m3u8)");

    private static String get_real_url(String rid) {
        String room_url = "https://m.huya.com/" + rid;
        String response = HttpRequest.create(room_url)
                .setContentType(HttpContentType.FORM)
                .putHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Mobile Safari/537.36")
                .get().getBody();
        Matcher matcher = PATTERN.matcher(response);
        if (!matcher.find()) {
            return null;
        }
        String result = matcher.group();
        if (StringUtils.isBlank(result)) {
            return "未开播或直播间不存在";
        }
        return result.replaceAll("_\\d{3,4}\\.m3u8", ".flv");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入虎牙房间号：");
        String rid = scanner.next();
        String real_url = get_real_url(rid);
        System.out.println("该直播间源地址为：\n" + real_url);
    }
}
