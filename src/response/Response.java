package response;

public abstract class Response {
    private final byte[] rawData;

    public Response(byte[] rawData){
        this.rawData = rawData;
    }

    public final byte[] getRawData() {
        return rawData;
    }
}
