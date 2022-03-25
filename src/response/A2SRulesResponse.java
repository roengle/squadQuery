package response;

import util.BufferHelper;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

public class A2SRulesResponse extends Response{
    private final Byte numRules;
    private final Map<String, String> ruleMap = new LinkedHashMap<>();

    public A2SRulesResponse(byte[] rawData) {
        super(rawData);

        ByteBuffer buffer = ByteBuffer.wrap(rawData);
        buffer.get();
        buffer.get();
        buffer.get();
        buffer.get();

        buffer.get();

        numRules = buffer.get();
        buffer.get();
        for(int i = 0; i < numRules; i++){
            String name = BufferHelper.getStringFromBuffer(buffer);
            String value = BufferHelper.getStringFromBuffer(buffer);

            ruleMap.put(name, value);
        }
    }

    public static A2SRulesResponse from(byte[] receivedData) {
        return new A2SRulesResponse(receivedData);
    }

    public Byte getNumRules() {
        return numRules;
    }

    public String getRuleValue(String rule){
        return ruleMap.get(rule);
    }

    @Override
    public String toString() {
        ruleMap.forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });

        return new StringJoiner(", ", A2SRulesResponse.class.getSimpleName() + "[", "]")
                .add("numRules=" + numRules)
                .add("ruleMap=" + ruleMap)
                .toString();
    }
}
