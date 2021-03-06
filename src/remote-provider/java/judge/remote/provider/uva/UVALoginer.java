package judge.remote.provider.uva;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import judge.httpclient.DedicatedHttpClient;
import judge.httpclient.HttpStatusValidator;
import judge.remote.RemoteOjInfo;
import judge.remote.account.RemoteAccount;
import judge.remote.loginer.RetentiveLoginer;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

@Component
public class UVALoginer extends RetentiveLoginer {

    @Override
    public RemoteOjInfo getOjInfo() {
        return UVAInfo.INFO;
    }

    @Override
    protected void loginEnforce(RemoteAccount account, DedicatedHttpClient client) {
        String html = client.get("/index.php").getBody();
        if (html.contains("mod_login_logoutform")) {
            return;
        }

        // Shit on you Joomla!
        client.getContext().setAttribute(HttpClientContext.COOKIE_STORE, new BasicCookieStore());
        html = client.get("/index.php").getBody();

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        String reg = "<input type=\"hidden\" name=\"([\\s\\S]*?)\" value=\"([\\s\\S]*?)\" />";
        Matcher matcher = Pattern.compile(reg).matcher(html);
        int number = 0;
        while (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);
            if (number > 0 && number < 9) {
                nvps.add(new BasicNameValuePair(name, value));
            }
            ++number;
        }
        nvps.add(new BasicNameValuePair("remember", "yes"));
        nvps.add(new BasicNameValuePair("username", account.getAccountId()));
        nvps.add(new BasicNameValuePair("passwd", account.getPassword()));
        HttpEntity entity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);

        client.post( //
                "/index.php?option=com_comprofiler&task=login", //
                entity, //
                HttpStatusValidator.SC_MOVED_PERMANENTLY);
    }

}
