package www.pdx.life.networkmonitoring.jsbrage;

/**
 * Message <-> String
 * Created by Jingyi on 17-3-16.
 */

public interface IBridgeConverter {
    Message parseMessage(String msgStr);
}
