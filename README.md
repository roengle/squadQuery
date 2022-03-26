# squadQuery
Simple library that implements [server queries](https://developer.valvesoftware.com/wiki/Server_queries#A2S_RULES)
for Squad. Provides methods to fetch `A2S_INFO` and `A2S_Rules` responses. `A2S_PLAYERS` responses
are not implemented as these are broken for Squad, see [Why A2S_PLAYERS Requests Are Not Supported](#why-a2s_players-requests-are-not-supported)
for more information.

### Using squadQuery
Download the latest release of [squadQuery](https://github.com/roengle/squadQuery/releases/latest).

With `squadQuery.jar` included in your project dependencies, you can use squadQuery in the following way:

```java
import query.Query;
import response.*;

public class Tester {
    public static void main(String[] args) {
        //Create Query object
        Query query = new Query("IP ADDRESS", 27015);

        //Query info
        A2SInfoResponse infoResponse = query.queryInfo();
        //Get some properties of info response
        System.out.println("Map: " + infoResponse.getMap());
        System.out.println("Max Players: " + infoResponse.getMaxPlayers());

        System.out.print("Keywords: ");
        infoResponse.getKeywords().ifPresent(System.out::println);

        //Query rules
        A2SRulesResponse rulesResponse = query.queryRules();
        //Get some properties of rules response
        System.out.println("Num Rules: " + rulesResponse.getNumRules());
        System.out.println("# of players in public queue: " + rulesResponse.getRuleValue("PublicQueue_i"));
        System.out.println("# of players in reserve queue: " + rulesResponse.getRuleValue("ReservedQueue_i"));
        System.out.println("# of players in server: " + rulesResponse.getRuleValue("PlayerCount_i"));
        //NUMPRIVCONN and NUMPUBCONN should always add up to Max Players from A2S_INFO query
        System.out.println("# of reserved slots for server: " + rulesResponse.getRuleValue("NUMPRIVCONN"));
        System.out.println("# of public slots for server: " + rulesResponse.getRuleValue("NUMPUBCONN"));
        System.out.println("Match timeout: " + rulesResponse.getRuleValue("MatchTimeout_f"));
        
    }
}
```
See the following for specific usages:

* [Query](https://github.com/roengle/squadQuery/blob/main/src/query/Query.java)
* [A2SInfoResponse](https://github.com/roengle/squadQuery/blob/main/src/response/A2SInfoResponse.java)
* [A2SRulesResponse](https://github.com/roengle/squadQuery/blob/main/src/response/A2SRulesResponse.java)

### Why A2S_PLAYERS Requests Are Not Supported
Per [the documentation](https://developer.valvesoftware.com/wiki/Server_queries#Multi-packet_Response_Format) for Source Queries,
multi-packet responses should begin with the first four bytes being `FF FF FF FE`. All `A2S_PLAYERS` queries sent to squad servers will only return
single-packet responses, which begin with `FF FF FF FF`. Due to the maximum packet size of 1400 bytes, one packet is not enough
to represent a full server of 100 players. On average, about 70 people can be expressed, as the rest of the data is truncated by the 
Squad server. Until Squad servers respond to `A2S_PLAYERS` queries differently, player lists cannot be fully retrieved this way.

An alternative to this would be connecting to the RCON server running on the Squad server, and getting the response of the 
`ListPlayers` command, which would appropriately split packets as needed to return a full response. A downside to this approach 
is that you're required to have the server's RCON password, whereas you would not need any credentials using server queries.

## Credits
* [Source Query Protocol Wiki](https://developer.valvesoftware.com/wiki/Server_queries)
